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

import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicLong

object HashFunctions {
  val md5 = MessageDigest.getInstance("MD5")
  val sha256 = MessageDigest.getInstance("SHA-256")

  @inline def byXORSHIFT(x:Long): Long ={
    var xl = x
    xl ^= (xl << 21)
    xl ^= (xl >>> 35)
    xl ^= (xl << 4)
    xl.abs
  }

  @inline def byPrimitiveRandom(x: Long): Long = {
    val multiplier = 0x5DEECE66DL
    val mask = (1L << 48) - 1
    val addend = 0xBL
    val seed = new AtomicLong((x ^ multiplier) & mask)

    @inline def next(bits: Int) = {
      var oldSeed = 0L
      var nextSeed = 0L
      do {
        oldSeed = seed.get
        nextSeed = (oldSeed * multiplier + addend) & mask
      } while ( {
        !seed.compareAndSet(oldSeed, nextSeed)
      })
      (nextSeed >>> (48 - bits)).toInt
    }
    ((next(32).toLong << 32) + next(32)).abs
  }

  @inline def byMD5(x: Long): Long = {
    synchronized{
      val decode = md5.digest(x.toString.getBytes())
      val encode = new BigInteger(1, decode)
      encode.longValue().abs
    }
  }

  @inline def bySHA(x: Long): Long = {
    synchronized{
      val decode = sha256.digest(x.toString.getBytes())
      val encode = new BigInteger(1, decode)
      encode.longValue().abs
    }
  }
}
