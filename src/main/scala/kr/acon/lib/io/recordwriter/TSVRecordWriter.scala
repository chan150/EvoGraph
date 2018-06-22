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

package kr.acon.lib.io.recordwriter

import java.io.DataOutputStream

import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet

class TSVRecordWriter(out: DataOutputStream) extends BaseRecordWriter(out) {
  @inline final val utf8 = "UTF-8";

  @inline final val newline = "\n".getBytes(utf8)
  @inline final val keyValueSeparator = "\t".getBytes(utf8)

  @inline override def write(key: Long, value: LongOpenHashBigSet) = {
    val iteration = value.iterator()
    val k = key.toString.getBytes(utf8)
    while (iteration.hasNext) {
      val v = iteration.nextLong().toString.getBytes(utf8)
      synchronized {
        out.write(k)
        out.write(keyValueSeparator)
        out.write(v)
        out.write(newline)
      }
    }
  }
}