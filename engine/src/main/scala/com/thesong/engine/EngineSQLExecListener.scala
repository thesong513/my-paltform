package com.thesong.engine

import java.util.concurrent.ConcurrentHashMap

import com.thesong.engine.adaptor.DML.{LoadAdaptor, SelectAdaptor}
import com.thesong.engine.antlr.EngineBaseListener
import com.thesong.engine.antlr.EngineParser.{OverwriteContext, SqlContext}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

/**
 * @Author thesong
 * @Date 2020/11/23 17:39
 * @Version 1.0
 * @Describe
 */
class EngineSQLExecListener(_sparkSession: SparkSession) extends EngineBaseListener {

  def sparkSession = _sparkSession

  //保存结果，方便监听器对象获取。可以返回客户端
  private val _result = new ConcurrentHashMap[String, String]()

  def addResult(k: String, v: String): EngineSQLExecListener = {
    _result.put(k, v)
    this
  }

  def getResult(k: String) = {
    _result.getOrDefault(k, "")
  }

  def result() = _result

  override def exitSql(ctx: SqlContext): Unit = {
    ctx.getChild(0).getText.toLowerCase() match {
      case "load" => {
        println("load操作")
        new LoadAdaptor(this).parse(ctx)
      }
      case "select" => {
        println("select操作")
        new SelectAdaptor(this).parse(ctx);
      }
      case "save" => {
        println("save操作")
      }
      case "create" =>
      case "insert" =>
      case "drop" =>
      case "truncate" =>
      case "show" =>
      case "explain" =>
      case "include" =>
    }
  }


}
