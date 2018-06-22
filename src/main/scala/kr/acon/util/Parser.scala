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

import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import kr.acon.lib.io._
import org.apache.hadoop.io.compress.{BZip2Codec, CompressionCodec, SnappyCodec}
import org.apache.hadoop.mapred.OutputFormat

class Parser extends Serializable {
  var hdfs = "./"
  var file = "graph"
  var machine = 120
  var format = "adj"
  var compress = "none"
  var function = "none"

  // TrillionG
  var logn = 20
  var ratio = 16
  var a = 0.57d
  var b = 0.19d
  var c = 0.19d
  var d = 0.05d
  def n = Math.pow(2, logn).toLong
  def e = ratio * n
  def param = (a, b, c, d)
  var noise = 0.0d
  var rng = System.currentTimeMillis
  var opt = 0

  // BA model (TrillionBA)
  var ban = 1000000l
  var bam = 2
  var bam0 = 2
  var bal = 10000l

  // Graph Scaling Problem (EvoGraph)
  var inputPath: String = null
  var scaleFactor: Long = 1
  var eidMax: Long = 0
  var vidMax: Long = 0

  // Debug (doesn't work anymore)
  var threshold = Long.MaxValue
  var upscaling = false
  var mass = 0
  var spark = true
  var par = 0

  val termBasic = List("-machine", "-m", "-hdfs", "-format", "-compress", "-rng", "-output", "-function", "-f")
  val termSKG = List("-logn", "-ratio", "-p", "-n", "-r", "-noise", "-opt", "-threshold")
  val termBA = List("-ba.n", "-ba.m", "-ba.m0", "-ba.l")
  val termGUS = List("-gs.input", "-gs.sf", "-gs.eid", "-gs.vid") //, "-gs.on", "-gs.mass", "-gs.spark", "-gs.par")
  val termGP = List("-gp.input", "-gp.method")

  val term = termBasic.++(termSKG).++(termBA).++(termGP).++(termGUS)

  def isNSKG = (noise != 0d)

  def argsParser(args: Array[String]) {
    var i = 0
    while (i < args.length) {
      if (term.contains(args(i))) {
        setParm(args(i).replaceFirst("-", ""), args(i + 1))
        i += 2
      } else {
        setParm("output", args(i))
        i += 1
      }
    }
  }

  def setParm(name: String, value: String) {
    name match {
      case "p" => {
        val split = value.trim.split(",")
        a = split(0).toDouble
        if (split.length == 1) {
          b = a / 3.0
          c = a / 3.0
        }
        if (split.length >= 3) {
          b = split(1).toDouble
          c = split(2).toDouble
        }
        d = 1 - a - b - c
      }
      case "hdfs" => hdfs = value
      case "logn" | "n" => logn = value.toInt
      case "ratio" | "r" => ratio = value.toInt
      case "machine" | "m" => machine = value.toInt
      case "output" => file = value
      case "noise" => noise = value.toDouble
      case "rng" => rng = value.toLong
      case "opt" => opt = value.toInt
      case "threshold" => threshold = value.toLong
      case "format" => format = value
      case "compress" => compress = value
      case "function" | "f" => function = value
      case "ba.n" => ban = value.toLong
      case "ba.m" => bam = value.toInt
      case "ba.m0" => bam0 = value.toInt
      case "ba.l" => bal = value.toInt

      case "gs.input" => inputPath = value.toString
      case "gs.sf" => scaleFactor = value.toLong

      case "gs.eid" => eidMax = value.toLong
      case "gs.vid" => vidMax = value.toLong
    }
  }

  def printTrillionG {
    println("|V|(=n)=%d, |Ei|(=m0)=%d, |E|(=m)=%d".format(ban, bam0, bam))
    println("|V|(=l)=%d (Local BA)".format(bal))
    println("PATH=%s, Machine=%d".format(hdfs + file, machine))
    println("OutputFormat=%s, CompressCodec=%s".format(format, compress))
    println("RandomSeed=%d".format(rng))
  }

  def getFunctionClass: Class[_ <: Predef.Function] = {
    var functionClass: Any = null

    if (function.startsWith("count")) {
      functionClass = classOf[Predef.COUNT]
    } else if (function.startsWith("out")) {
      functionClass = classOf[Predef.OUT]
    } else if (function.startsWith("in")) {
      functionClass = classOf[Predef.IN]
    } else if (function == "undirected") {
      functionClass = classOf[Predef.UNDIRECTED]
    } else if (function == "both") {
      functionClass = classOf[Predef.BOTH]
    } else if (function == "cc") {
      functionClass = classOf[Predef.CC]
    } else if (function == "none") {
      functionClass = null
    } else {
      try {
        functionClass = Class.forName(function)
          .asInstanceOf[Class[Predef.Function]]
      } catch {
        case _: Throwable => {
          println("Mismatch function class")
          functionClass = null
        }
      }
    }

    functionClass.asInstanceOf[Class[_ <: Predef.Function]]
  }

  def getOutputFormat: Class[_ <: OutputFormat[Long, LongOpenHashSet]] = {
    var outputFormatClass: Any = null
    if (format.startsWith("adj")) {
      if (format == "adj" || format == "adj6") {
        outputFormatClass = classOf[ADJ6OutputFormat]
      } else if (format == "adj4") {
        outputFormatClass = classOf[ADJ4OutputFormat]
      } else if (format == "adj8") {
        outputFormatClass = classOf[ADJ8OutputFormat]
      }
    } else if (format == "tsv")
      outputFormatClass = classOf[TSVOutputFormat]
    else if (format.startsWith("csr")) {
      if (format == "csr" || format == "csr6") {
        outputFormatClass = classOf[CSR6OutputFormat]
      } else if (format == "csr4") {
        outputFormatClass = classOf[CSR4OutputFormat]
      } else if (format == "csr8") {
        outputFormatClass = classOf[CSR8OutputFormat]
      }
    } else if (format == "none") {
      outputFormatClass = null
    } else {
      try {
        outputFormatClass = Class.forName(format)
          .asInstanceOf[Class[OutputFormat[Long, LongOpenHashSet]]]
      } catch {
        case _: Throwable => {
          println("Mismatch output format class")
          outputFormatClass = null
        }
      }
    }
    outputFormatClass.asInstanceOf[Class[OutputFormat[Long, LongOpenHashSet]]]
  }

  def getCompressCodec: Class[_ <: CompressionCodec] = {
    var compressCodecClass: Any = null
    if (compress == "none") {
      compressCodecClass = null
    } else if (compress == "snappy") {
      compressCodecClass = classOf[SnappyCodec]
    } else if (compress == "bzip" || compress == "bzip2") {
      compressCodecClass = classOf[BZip2Codec]
    } else {
      try {
        compressCodecClass = Class.forName(compress)
          .asInstanceOf[Class[CompressionCodec]]
      } catch {
        case _: Throwable => {
          println("Mismatch compression codec")
          compressCodecClass = null
        }
      }
    }
    compressCodecClass.asInstanceOf[Class[CompressionCodec]]
  }
}