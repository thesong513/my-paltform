package com.thesong.engine.interpreter

import java.io.ByteArrayOutputStream

import scala.tools.nsc.interpreter.Results

/**
 * @Author thesong
 * @Date 2020/10/28 17:10
 * @Version 1.0
 */
object AbstractSparkInterpreter {
  // 反向肯定预测，匹配命令中的换行符
  // 命令：就是要执行的代码中的换行符
  private[interpreter] val KEEP_NEWLINE_RE="""(?<\n)"""
}

abstract class AbstractSparkInterpreter extends Interpreter {
  import AbstractSparkInterpreter._
  // 操作流，返回响应
  protected val outputStream = new ByteArrayOutputStream()
  // 方便底层实现，用于判断是否已经启动引擎 spark-shell
  protected def isStart(): Boolean
  // 中断姻亲，命令执行结果
  protected def interpreter(order:String):Results.Result
  // spark-shell绑定初始化变量值
  protected def bind(
          name:String,
          className:String,
          value:Object,
          method:List[String]
          )
  //关闭spark-shell
  protected def close():Unit
}