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

package kr.acon

import kr.acon.upscaler.EvoGraphGenerator
import kr.acon.generator.skg.SKGGenerator

object ApplicationMain {
  def main(args: Array[String]): Unit = {
    val apps = Seq("TrillionG", "TrillionBA", "EvoGraph", "EvoGraphV2", "TGSim")
    require(args.length >= 1, s"argument must be larger than 1, args=${args.deep}")
    require(apps.contains(args(0)), s"Unknown application, " +
      s"please set application type in [${apps.mkString(", ")}]")

    val remainArgs = args.slice(1, args.length)
    println(s"Launching ${args(0)}...")
    args(0) match {
      case "TrillionG" => SKGGenerator(remainArgs)
      case "EvoGraph" => EvoGraphGenerator(remainArgs)
      case "EvoGraphV2" => println("To appear")
//        EvoGraphV2Generator(remainArgs) // TODO: experimental

      case "TrillionBA" => println("To appear") //TODO: support BA model
      case "TGSim" => println("To appear") //TODO: support simulation
      case _ =>
    }
  }
}