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

package kr.acon.generator.upscaling

import it.unimi.dsi.fastutil.ints.IntBigArrays
import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet
import kr.acon.generator.BaseGenerator
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.io.Source

object EvoGraphGenerator extends BaseGenerator {
  override val appName = "EvoGraph: An Effective and Efficient Graph Upscaling Method for Preserving Graph Properties"

  override def run(sc: SparkContext): RDD[(Long, LongOpenHashBigSet)] = {

    import kr.acon.util.Util._

    implicit class ImplArrayArrayInt(self: Array[Array[Int]]) {
      def set(eid: Long, value: Int) = {
        IntBigArrays.set(self, eid, value)
      }
    }

    var eidMax = parser.eidMax
    var vidMax = parser.vidMax

    if (eidMax <= 0 || vidMax <= 0) {
      println("[Warn] \"gs.vid\" or \"gs.eid\" has a wrong value")
      eidMax = 0
      vidMax = 0
      for (line <- Source.fromFile(parser.inputPath).getLines.filter(s => !s.startsWith("#"))) {
        val split = line.split("\\s+")
        val (src, dest) = (split(0).toInt, split(1).toInt)
        vidMax = math.max(vidMax, math.max(src, dest))
        eidMax += 1
      }
      vidMax += 1
    }

    val edgeSrcArray = IntBigArrays.newBigArray(eidMax)
    val edgeDestArray = IntBigArrays.newBigArray(eidMax)
    var i = 0l
    for (line <- Source.fromFile(parser.inputPath).getLines.filter(s => !s.startsWith("#"))) {
      val split = line.split("\\s+")
      val (src, dest) = (split(0).toInt, split(1).toInt)
      edgeSrcArray.set(i, src)
      edgeDestArray.set(i, dest)
      i += 1
    }

    val ds = new GraphUpscalingMethod(edgeSrcArray, edgeDestArray, vidMax, eidMax)

    val broadcast = sc.broadcast(ds)
    val range = sc.rangeHash(0, eidMax * parser.scaleFactor - 1, 1, parser.machine)
    val edges = range.map {
      eid =>
        val scaling = broadcast.value
        val adjacency = new LongOpenHashBigSet(1)
        val (src, dest) = scaling.determine(eid)
        adjacency.add(dest)
        (src, adjacency)
    }
    edges
  }
}