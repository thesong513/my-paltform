package com.thesong.engine

import com.thesong.engine.interpreter.SparkInterpreter
import org.apache.spark.SparkConf

/**
 * @Author thesong
 * @Date 2020/10/28 13:35
 * @Version 1.0
 */

/**
 * 平台服务启动入口
 */
object App {

  def parseArgs(args: Array[String]): Map[String, String] = {
    var argsMap: Map[String, String] = Map()
    var argv = args.toList
    while (argv.nonEmpty) {
      // ("tail")
      //
      argv match {
        case "-engine.zkServer" :: value :: tail => {
          argsMap += ("zkServer" -> value)
          argv = tail
        }
        case "-engine.tag" :: value :: tail => {
          argsMap += ("engine.tag" -> value)
          argv = tail
        }
        case Nil =>{}
        case tail => {
          println(s"对不起，无法识别：${tail.mkString(" ")}")

        }
      }
    }
  argsMap
  }


  def main(args: Array[String]): Unit = {
    //    val tmpArgs = Array("-engine.zkServer","node01:8181","node02:8181")
    //    val stringToString = parseArgs(tmpArgs)

    val interpreter = new SparkInterpreter
    val sparkConf: SparkConf = interpreter.start()
    sparkConf.set("spark.driver.host", "localhost")
  }

}
