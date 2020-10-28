package com.thesong.engine.interpreter

import org.apache.spark.SparkConf

/**
 * @Author thesong
 * @Date 2020/10/28 16:25
 * @Version 1.0
 */
object Interpreter {
  // 目的把引擎的所有的响应统一管理,上层的抽象
  abstract class ExecuteReponse

  // 执行成功的任务
  case class ExecuteSuccess(content:Object) extends ExecuteReponse

  case class ExecuteError(executeName:String, //任务名称
                          executeValue: String, //任务失败原因
                          trackback:Seq[String] //失败的堆栈信息，一行一行的
                         )

  case class ExecuteIncomplete() extends ExecuteReponse

  case class ExecuteAborted(message: String) extends ExecuteReponse

}

/**
 * 对外提供的方法
 */

trait Interpreter{
  import Interpreter._

  /**
   * 启动引擎方法
   */
  def start():SparkConf

  /**
   * 关闭引擎方法
   */
  def close():Unit

  /**
  * 命令执行方法(代码/SQL),为了保证安全，包内可见
  */
  private [interpreter] def execute(order:String) :ExecuteReponse
}