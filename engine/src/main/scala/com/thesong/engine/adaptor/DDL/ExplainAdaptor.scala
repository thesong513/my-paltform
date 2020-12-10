package com.thesong.engine.adaptor.DDL

import com.thesong.engine.EngineSQLExecListener
import com.thesong.engine.`trait`.{ParseLogicalPlan, ParseLogicalTools}
import com.thesong.engine.antlr.{EngineLexer, EngineParser}
import org.antlr.v4.runtime.misc.Interval

/**
 * @Author thesong
 * @Date 2020/12/8 11:28
 * @Version 1.0
 * @Describe
 */
class ExplainAdaptor(engineSQLExecListener: EngineSQLExecListener) extends ParseLogicalPlan with ParseLogicalTools{
  override def parse(ctx: EngineParser.SqlContext): Unit = {
    val sparkSession = engineSQLExecListener.sparkSession
    val input = ctx.start.getTokenSource.asInstanceOf[EngineLexer]._input
    val start = ctx.start.getStartIndex
    val stop = ctx.stop.getStopIndex
    val interval = new Interval(start,stop)
    val originalText = input.getText(interval)
    val chunks:Array[String] = originalText.replace(";","").split("\\s")

    chunks.length match {
      case 2=>{
        engineSQLExecListener.addResult("explainStr",sparkSession.table(chunks(1)).queryExecution.toString())
      }
      case _=>{
        engineSQLExecListener.addResult("explainStr",sparkSession.sql(chunks.tail.mkString(" ")).queryExecution.toString())
      }
    }

  }
}
