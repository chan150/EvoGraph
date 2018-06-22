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

package kr.acon.generator

import java.util.Date

import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet
import kr.acon.util.Parser
import org.apache.hadoop.io.LongWritable
import org.apache.spark.rdd.RDD
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions
import org.apache.spark.{SparkConf, SparkContext}

abstract class BaseGenerator extends Serializable {
  val parser = new Parser
  val appName = "Base Graph Generator"

  def run(sc: SparkContext): RDD[(Long, LongOpenHashBigSet)] = {
    println("do nothing")
    null
  }

  def postProcessing() = {

  }

  def apply(implicit args: Array[String] = new Array[String](0)) {
    val appNameArgs = appName + " / " + (new Date).toString + " / " + args.mkString(" ")
    val conf = new SparkConf().setAppName(appNameArgs)
    val sc = new SparkContext(conf)
    parser.argsParser(args)

    val startTime = new Date

    val edges = run(sc)
    if (edges != null) {
      if(parser.getFunctionClass != null) {
        parser.getFunctionClass.newInstance().f(edges, parser)
      } else {
        if (parser.getOutputFormat != null) {
          writeEdges(edges)
        } else {
          edges.count
        }
      }
    }

    val endTime = new Date

    println((endTime.getTime - startTime.getTime) / 1000f + " seconds spent.")
    postProcessing()
    sc.stop
  } // end of apply

  def writeEdges(edges: RDD[(Long, LongOpenHashBigSet)]) {
    val path = parser.hdfs + parser.file
    val format = parser.getOutputFormat
    val codec = parser.getCompressCodec
    if (codec != null)
      edges.saveAsHadoopFile(path, classOf[LongWritable], classOf[LongOpenHashBigSet], format, codec)
    else
      edges.saveAsHadoopFile(path, classOf[LongWritable], classOf[LongOpenHashBigSet], format)
  }
}
