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

package kr.acon.lib.io

import java.io.DataOutputStream

import kr.acon.lib.io.recordwriter.{ADJ4RecordWriter, ADJ6RecordWriter, ADJ8RecordWriter}

class ADJ4OutputFormat
    extends BaseOutputFormat {
  @inline final override def getRecordWriter(out: DataOutputStream) = new ADJ4RecordWriter(out)
}

class ADJ6OutputFormat
    extends BaseOutputFormat {
  @inline final override def getRecordWriter(out: DataOutputStream) = new ADJ6RecordWriter(out)
}

class ADJ8OutputFormat
    extends BaseOutputFormat {
  @inline final override def getRecordWriter(out: DataOutputStream) = new ADJ8RecordWriter(out)
}
