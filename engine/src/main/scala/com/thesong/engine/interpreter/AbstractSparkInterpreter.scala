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
  private[interpreter] val KEEP_NEWLINE_RE = """(?<=\n)""".r
}

abstract class AbstractSparkInterpreter extends Interpreter {

  import AbstractSparkInterpreter._

  // 操作流，返回响应
  protected val outputStream = new ByteArrayOutputStream()

  // 方便底层实现，用于判断是否已经启动引擎 spark-shell
  protected def isStart(): Boolean

  // 中断姻亲，命令执行结果
  protected def interpreter(order: String): Results.Result

  // spark-shell绑定初始化变量值
  protected def bind(
                      name: String,
                      className: String,
                      value: Object,
                      method: List[String]
                    )

  //关闭spark-shell
  override def close(): Unit = {}

  // 读取流中的数据
  private def readStdout() = {
    val output = outputStream.toString("UTF-8")
    outputStream.reset()
    output
  }

  // 解析错误信息
  protected[interpreter] def parseError(stdout: String): (String, Seq[String]) = {
    val lines = KEEP_NEWLINE_RE.split(stdout)
    val traceback = lines.tail
    val errValues = lines.headOption.map(_.trim).getOrElse("UNKNOW ERROR")
    (errValues, traceback)
  }

  private def excuteLine(order: String): Interpreter.ExecuteReponse = {
    scala.Console.withOut(outputStream) {
      interpreter(order) match {
        case Results.Success => {
          Interpreter.ExecuteSuccess(TEXT_PLAIN -> readStdout())
        }
        case Results.Incomplete => {
          Interpreter.ExecuteIncomplete()
        }
        case Results.Error => {
          val tuple: (String, Seq[String]) = parseError(readStdout())
          Interpreter.ExecuteError("ERROR", tuple._1, tuple._2)
        }
      }
    }
  }

  /**
   * 打破双亲委托机制的，优先加载子类加载器
   * 双亲委派模型的作用:保证JDK核心类的优先加载
   * 缺陷：如果想执行自己的spark-shell，不想执行spark的，原则违背双亲委派机制
   * 解决：打破双亲委派机制
   * 方式：
   * 1、自定义类加载器，重写loadClass方法；
   * 2、使用线程上下文类加载器；
   *
   * spark启动的时候已经加载spark的classpath路径下的jar包，使用线程上下文切换的方式解析执行
   * 交互的命令
   **/

  protected def restoreContextClassLoader[T](fn

  :=> T
  ): T = {
    val contextClassLoader = Thread.currentThread().getContextClassLoader
    try {
      fn
    } finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader)
    }
  }


}