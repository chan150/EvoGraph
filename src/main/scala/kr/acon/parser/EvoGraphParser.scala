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

package kr.acon.parser

class EvoGraphParser extends Parser {
  // Graph Scaling Problem (EvoGraph)
  var inputPath: String = "toy"
  var scaleFactor: Long = 1
  var eidMax: Long = 0
  var vidMax: Long = 0

  val termEvoGraph = List("-gs.input", "-gs.sf", "-gs.eid", "-gs.vid")

  override val term = termBasic.++(termEvoGraph)

  override def setParameters(name: String, value: String) {
    super.setParameters(name, value)
    name match {
      case "gs.input" => inputPath = value.toString
      case "gs.sf" => scaleFactor = value.toLong
      case "gs.eid" => eidMax = value.toLong
      case "gs.vid" => vidMax = value.toLong
      case _ => // important to set
    }
  }
}
