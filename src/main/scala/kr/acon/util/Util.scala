/*
 *        ______            ______                 __
 *       / ____/   ______  / ____/________ _____  / /_
 *      / __/ | | / / __ \/ / __/ ___/ __ `/ __ \/ __ \
 *     / /___ | |/ / /_/ / /_/ / /  / /_/ / /_/ / / / /
 *    /_____/ |___/\____/\____/_/   \__,_/ .___/_/ /_/
 *                                      /_/
 *
 *    Copyright (C) 2018 Himchan Park (chan150@dgist.ac.kr)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package kr.acon.util

import org.apache.spark.{AccumulatorParam, SparkContext}
import org.apache.spark.rdd.RDD

object Util {
  @inline final def binarySearch(a: Array[Double], toIndex: Int, key: Double) = {
    var low = 0
    var high = toIndex - 1
    while (low <= high) {
      val mid = (low + high) >>> 1
      val midVal = a(mid)

      if (midVal < key)
        low = mid + 1 // Neither val is NaN, thisVal is smaller
      else if (midVal > key)
        high = mid - 1 // Neither val is NaN, thisVal is larger
      else {
        val midBits = java.lang.Double.doubleToLongBits(midVal)
        val keyBits = java.lang.Double.doubleToLongBits(key)
        if (midBits == keyBits) { // Values are equal
          mid // Key found
        } else if (midBits < keyBits) // (-0.0, 0.0) or (!NaN, NaN)
          low = mid + 1
        else // (0.0, -0.0) or (NaN, !NaN)
          high = mid - 1
      }
    }
    low - 1
  }


  implicit class BalancedPartitionRange(self: SparkContext) extends Serializable {
    def rangeHash(
                   start: Long,
                   end: Long,
                   step2: Long = 1,
                   numSlices: Int): RDD[Long] = {
      val step = numSlices
      self.parallelize(0 until numSlices, numSlices).mapPartitionsWithIndex { (i, _) =>
        val partitionStart = start + i
        val partitionEnd = end

        def getSafeMargin(bi: BigInt): Long =
          if (bi.isValidLong) {
            bi.toLong
          } else if (bi > 0) {
            Long.MaxValue
          } else {
            Long.MinValue
          }

        val safePartitionStart = getSafeMargin(partitionStart)
        val safePartitionEnd = getSafeMargin(partitionEnd)

        new Iterator[Long] {
          private[this] var number: Long = safePartitionStart
          private[this] var overflow: Boolean = false

          override def hasNext =
            if (!overflow) {
              if (step > 0) {
                number < safePartitionEnd
              } else {
                number > safePartitionEnd
              }
            } else false

          override def next() = {
            val ret = number
            number += step
            if (number < ret ^ step < 0) {
              overflow = true
            }
            ret
          }
        }
      }
    }
  }

  implicit class RangePartitionFromDegreeRDD(self: RDD[(Long, Double)]) extends Serializable {
    def rangePartition(numMachine: Int, n: Long, e: Long) = {
      class SetAccumulatorParam extends AccumulatorParam[Set[(Long, Double)]] {
        def zero(initialValue: Set[(Long, Double)]): Set[(Long, Double)] = {
          initialValue
        }

        def addInPlace(v1: Set[(Long, Double)], v2: Set[(Long, Double)]): Set[(Long, Double)] = {
          v1.++(v2)
        }
      }
      val sc = self.sparkContext
      val aggr = sc.accumulator(Set((0l, 0d)))(new SetAccumulatorParam)
      val lastGlobal = self.fold((0l, 0d)) {
        case (left, right) =>
          val first = if (left._1 > right._1) right else left
          val second = if (left._1 > right._1) left else right
          if (first._2 > (e / numMachine / 100)) {
            aggr.add(Set(first))
            second
          } else {
            (second._1, first._2 + second._2)
          }
      } // end of last
      aggr.add(Set(lastGlobal))
      val sorted = aggr.value.toSeq.sortBy { case (vid, acc) => vid }
      val calculated = sorted
      val range = for (i <- (0 until calculated.length - 1))
        yield (if (calculated(i)._1 <= calculated(i + 1)._1 - 1)
          (calculated(i)._1, calculated(i + 1)._1 - (if (i == calculated.length - 2) 0 else 1))
        else (-1l, -1l))
      val range2 = range.filter(p => p._1 >= 0 && p._2 >= 0).zipWithIndex
      val range2finalize = range2.map { case ((f, s), i) => if (i == range2.length - 1) ((f, n - 1), i) else ((f, s), i) }
      val range3 = range2finalize.map { case ((st, ed), index) => (st, ed) }
      val rangeRDD = sc.parallelize(range3, numMachine)
      val threshold = (Int.MaxValue / 4).toLong
      val rangeRDD2 = rangeRDD.flatMap {
        case (f, s) =>
          val end = math.ceil((s - f + 1).toDouble / threshold.toDouble).toInt
          if (end == 1) Iterable((f, s))
          else {
            val array = new Array[(Long, Long)](end)
            var i = 0
            while (i + 1 < end) {
              array(i) = (f + threshold * i, f + threshold * (i + 1) - 1)
              i += 1
            }
            array(end - 1) = (f + threshold * (end - 1), s)
            array
          }
      }.flatMap(x => x._1 to x._2)
      rangeRDD2
    }
  }

}