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

import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet
import org.apache.spark.rdd.RDD
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions
import org.apache.spark.storage.StorageLevel

import scala.sys.process._

object Predef {

  abstract class Function {
    type t1 = RDD[(Long, LongOpenHashBigSet)]
    type t2 = Parser

    def f(e: t1, parser: t2)
  }

  class COUNT extends Function {
    override def f(e: t1, parser: t2) {
      e.count
    }
  }

  class OUT extends Function {
    override def f(e: t1, parser: t2) {
      val d = e.flatMap { x =>
        x._2.toLongArray.flatMap { y => Iterator((x._1, 1L)) }
      }.reduceByKey(_ + _).map(x => (x._2, 1L)).reduceByKey(_ + _).map(x => x._1 + "\t" + x._2)
      d.saveAsTextFile(parser.hdfs + parser.file)
      plotDegree(parser)
    }
  }

  class IN extends Function {
    override def f(e: t1, parser: t2) {
      val d = e.flatMap(x => x._2.toLongArray.map { y => (y, 1L) }).reduceByKey(_ + _)
        .map(x => (x._2, 1L)).reduceByKey(_ + _).map(x => x._1 + "\t" + x._2)
      d.saveAsTextFile(parser.hdfs + parser.file)
      plotDegree(parser)
    }
  }

  class BOTH extends Function {
    override def f(e: t1, parser: t2): Unit = {
      //TODO:
    }
  }

  class UNDIRECTED extends Function {
    override def f(e: t1, parser: t2): Unit = {
      val d = e.flatMap { x =>
        x._2.toLongArray.flatMap { y =>
          Iterator((x._1, 1L), (y, 1L))
        }
      }.reduceByKey(_ + _).map(x => (x._2, 1L)).reduceByKey(_ + _).map(x => s"${x._1}\t${x._2}")

      d.saveAsTextFile(parser.hdfs + parser.file)
      plotDegree(parser)
    }
  }

  class CC extends Function {
    override def f(e: t1, parser: t2): Unit = {
      //TODO:
    }
  }

  def count(e: RDD[(Long, LongOpenHashBigSet)], parser: Parser) {
    e.count
  }

  def plotDegree(parser: Parser) {
    "hadoop fs -copyToLocal " + parser.file + " temp" !;
    "gnuplot app/degree.plot" !;
    "mv temp " + parser.file !;
    "mv output.eps " + parser.file + ".eps" !;
  }

  def plotOutDegree(e: RDD[(Long, LongOpenHashBigSet)], parser: Parser) {
    val d = e.map { x =>
      (x._1, x._2.size64())
    }.reduceByKey(_ + _).map(x => (x._2, 1L)).reduceByKey(_ + _).map(x => x._1 + "\t" + x._2)

    d.saveAsTextFile(parser.hdfs + parser.file)
    plotDegree(parser)
  }

  def plotInDegree(e: RDD[(Long, LongOpenHashBigSet)], parser: Parser) {
    val d = e.flatMap(x => x._2.toLongArray.map { y => (y, 1L) }).reduceByKey(_ + _)
      .map(x => (x._2, 1L)).reduceByKey(_ + _).map(x => x._1 + "\t" + x._2)
    d.saveAsTextFile(parser.hdfs + parser.file)
    plotDegree(parser)
  }

  def plotBothDegree(e: RDD[(Long, LongOpenHashBigSet)], parser: Parser) {
    val el = e.persist(StorageLevel.DISK_ONLY_2)
    val ori = parser.file
    parser.file = ori + "_out"
    plotOutDegree(el, parser)
    parser.file = ori + "_in"
    plotInDegree(el, parser)
  }

  def saveAsobject(e: RDD[(Long, LongOpenHashBigSet)], parser: Parser) {
    e.saveAsObjectFile(parser.hdfs + parser.file)
  }

  def plotUndirectDegree(e: RDD[(Long, LongOpenHashBigSet)], parser: Parser) {
    val d = e.flatMap {
      x =>
        x._2.toLongArray.flatMap { y =>
          Iterator((x._1, 1L), (y, 1L))
        }
    }.reduceByKey(_ + _).map(x => (x._2, 1L)).reduceByKey(_ + _).map(x => s"${x._1}\t${x._2}")

    d.saveAsTextFile(parser.hdfs + parser.file)
    plotDegree(parser)
  }
}