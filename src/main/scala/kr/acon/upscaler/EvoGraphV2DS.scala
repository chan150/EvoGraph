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

import it.unimi.dsi.fastutil.longs.{Long2ObjectArrayMap, LongOpenHashBigSet}
import kr.acon.parser.EvoGraphParser

import scala.annotation.tailrec
import scala.util.Random


class EvoGraphV2DS(vidMax: Long, eidMax: Long, sf: Long) extends Serializable {

  private[upscaler] def this(p: EvoGraphParser) {
    this(p.vidMax, p.eidMax, p.scaleFactor)
  }

  @inline private[this] def getDegree(r: Random = new Random()) = {
    //    val n = (k - 1) * eidMax
    val p = 1D / eidMax
    val s = sf - 1
    (s + math.sqrt(s * (1 - p)) * r.nextGaussian).round
  }

  private[this] def referenceWithinSF(r: Random = new Random(), sk: Long = sf, dk: Long = sf, k: Long = sf): (Long, Long) = {
    if (k <= 1 || (sk != sf && dk != sf)) {
      (sk, dk)
    } else {
      val direction = r.nextBoolean()
      val _k = (r.nextDouble() * k).round
      if (direction) {
        referenceWithinSF(r, k, dk, _k)
      } else {
        referenceWithinSF(r, sk, k, _k)
      }
    }
  }

  @inline private[this] def reference(r: Random = new Random()) = {
    referenceWithinSF(r)
  }

  @inline private[this] def originalEdges(src: Long, dst: Long) = {
    val adj = new LongOpenHashBigSet()
    adj.add(dst)
    Iterator((src, adj))
  }

  @inline private[this] def upscalingEdges(src: Long, dst: Long, r: Random = new Random()) = {
    val associatedEdges = new Long2ObjectArrayMap[LongOpenHashBigSet]()
    val size = getDegree(r)

    val adj = new LongOpenHashBigSet()
    adj.add(dst)
    associatedEdges.put(src, adj)

    var i = 0
    while (i < size) {
      val (sk, dk) = reference(r)
      val (u, v) = ((sk - 1) * vidMax + src, (dk - 1) * vidMax + dst)
      if (!associatedEdges.containsKey(u)) {
        val map = new LongOpenHashBigSet()
        map.add(v)
        associatedEdges.put(u, map)
      } else {
        associatedEdges.get(u).add(v)
      }
      i += 1
    }
    new Iterator[(Long, LongOpenHashBigSet)] {
      val iterator = associatedEdges.keySet().iterator()

      override def hasNext: Boolean = iterator.hasNext

      override def next(): (Long, LongOpenHashBigSet) = {
        val vid = iterator.nextLong()
        (vid, associatedEdges.get(vid))
      }
    }
  }

  private[upscaler] def determineAllEdges(src: Long, dst: Long, r: Random = new Random()) = {
    if (sf == 1) {
      originalEdges(src, dst)
    } else {
      upscalingEdges(src, dst, r)
    }
  }
}