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
import kr.acon.util.HashFunctions


class EvoGraphDS(edgeSrc: Array[Array[Int]],
                 edgeDest: Array[Array[Int]],
                 vidMax: Long,
                 eidMax: Long)
  extends Serializable {

  type ED = Long
  type VD = Long

  def H(x: Long) = HashFunctions.byXORSHIFT(x)

  def determine(eid: ED) = {
    eid.determine
  }

  implicit class ImplicitArrayArrayInt(self: Array[Array[Int]]) {
    def getLong(eid: ED) = {
      IntBigArrays.get(self, eid)
    }
  }

  implicit class ImplicitEdgeIdentifier(x: ED) {

    def h1 = {
      H(x) % (x.sf * eidMax)
    }

    def h2 = {
      if (H(x) % 2 == 0)
        true
      else
        false
    }

    def ref(eid: ED): VD = {
      x % vidMax + eid.sf * vidMax
    }

    def sf = {
      (x / eidMax)
    }

    def determine: (VD, VD) = {
      val dir = x.h2
      val (u, v) = if (x < eidMax) {
        x.direct
      } else {
        x.h1.determine
      }
      if (dir)
        (u.ref(x), v)
      else
        (u, v.ref(x))
    }

    def direct: (VD, VD) = {
      (edgeSrc.getLong(x), edgeDest.getLong(x))
    }
  }

}