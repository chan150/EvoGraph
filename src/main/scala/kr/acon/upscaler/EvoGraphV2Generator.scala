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

package kr.acon.upscaler

import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet
import kr.acon.generator.BaseGenerator
import kr.acon.parser.EvoGraphParser
import org.apache.spark.rdd.RDD

import scala.util.Random

object EvoGraphV2Generator extends BaseGenerator {
  override val appName = "(experimental) EvoGraph+: An advanced variant of EvoGraph exploiting top-down approach"

  override val parser = new EvoGraphParser

  var eidMax = 0l
  var vidMax = 0l

  override def postProcessing(): Unit = {
    println("Input=%s, |V0|=%d, |E0|=%d, SF=%d".format(parser.inputPath, parser.vidMax, parser.eidMax, parser.scaleFactor))
    println("PATH=%s, Machine=%d".format(parser.hdfs + parser.file, parser.machine))
    println("OutputFormat=%s, CompressCodec=%s".format(parser.format, parser.compress))
    println("RandomSeed=%d".format(parser.rng))
  }

  override def run: RDD[(Long, LongOpenHashBigSet)] = {


    val inputFile = sc.textFile(parser.hdfs + parser.inputPath, parser.machine)
    val original = inputFile.map { x => val s = x.split("\\s+"); (s(0).toLong, s(1).toLong) }


    if (eidMax <= 0 || vidMax <= 0) {
      println("Please set \"gs.vid\" and \"gs.eid\" to reduce load time")
      parser.eidMax = original.count()
      parser.vidMax = original.map(x => math.max(x._1, x._2)).reduce(math.max)
    }

    require(parser.scaleFactor >= 1, s"Scale factor must be larger than 1 (SF=${parser.scaleFactor})")

    val seed = parser.rng
    val ds = new EvoGraphV2DS(parser)
    val bc = sc.broadcast(ds)

    original.mapPartitionsWithIndex {
      case (index, partition) =>
        val scaling = bc.value
        val r = new Random(seed + index)
        partition.flatMap {
          case (src, dst) =>
            scaling.determineAllEdges(src, dst, r)
        }
    }
  }


}