package com.thesong.engine.interpreter

import org.apache.spark.SparkConf
import org.json4s.JObject

import scala.tools.nsc.interpreter.Results

/**
 * @Author thesong
 * @Date 2020/10/28 16:25
 * @Version 1.0
 */
object Interpreter {

  // 目的把引擎的所有的响应统一管理,上层的抽象
  abstract class ExecuteResponse

  // 执行成功的任务
  case class ExecuteSuccess(content: JObject) extends ExecuteResponse

  case class ExecuteError(executeName: String, //任务名称
                          executeValue: String, //任务失败原因
                          trackback: Seq[String] //失败的堆栈信息，一行一行的
                         ) extends ExecuteResponse

  case class ExecuteIncomplete() extends ExecuteResponse

  case class ExecuteAborted(message: String) extends ExecuteResponse

}

/**
 * 对外提供的方法
 */

trait Interpreter {

  import Interpreter._

  /**
   * 启动引擎方法
   */
  def start(): SparkConf

  /**
   * 关闭引擎方法
   */
  def close(): Unit

  /**
   * 命令执行方法(代码/SQL),为了保证安全，包内可见
   */
  private[interpreter] def execute(order: String): ExecuteResponse

  //  /**
  //   * 只有代码调用次方法
  //   *
  //   * @param order
  //   * @return
  //   */
  //  protected def interpret(order: String): Results.Result
  /**
   * 只有代码调用次方法
   *
   * @param order
   * @return
   */
  //  protected def interpret(order: String): Results.Result
}