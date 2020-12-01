package com.thesong.engine

import com.thesong.engine.adaptor.DML.LoadAdaptor
import com.thesong.engine.antlr.EngineBaseListener
import com.thesong.engine.antlr.EngineParser.SqlContext
import org.apache.spark.sql.SparkSession

/**
 * @Author thesong
 * @Date 2020/11/23 17:39
 * @Version 1.0
 * @Describe
 */
class EngineSQLExecListener(_sparkSession: SparkSession) extends EngineBaseListener {

  def sparkSession = _sparkSession

  override def exitSql(ctx: SqlContext): Unit = {
    ctx.getChild(0).getText.toLowerCase() match {
      case "load" => {
        println("load操作")
        new LoadAdaptor(this).parse(ctx)
      }

      case "select" =>
      case "save" =>
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
