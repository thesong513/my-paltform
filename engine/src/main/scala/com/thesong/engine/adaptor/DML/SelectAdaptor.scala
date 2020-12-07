package com.thesong.engine.adaptor.DML

import java.util.UUID

import com.thesong.engine.EngineSQLExecListener
import com.thesong.engine.`trait`.{ParseLogicalPlan, ParseLogicalTools}
import com.thesong.engine.antlr.{EngineLexer, EngineParser}
import org.antlr.v4.runtime.misc.Interval


/**
 * @Author thesong
 * @Date 2020/12/1 15:12
 * @Version 1.0
 * @Describe
 */


class SelectAdaptor(engineSQLExecListener: EngineSQLExecListener) extends ParseLogicalPlan with ParseLogicalTools{
  override def parse(ctx: EngineParser.SqlContext): Unit = {
    val sparkSession = engineSQLExecListener.sparkSession
    // 需要从输入中获取select语句
    // load xxx from as xx;\n select * from xx;
    val input = ctx.start.getTokenSource.asInstanceOf[EngineLexer]._input
    val start = ctx.start.getStartIndex
    val stop = ctx.stop.getStopIndex
    val interval = new Interval(start, stop)
    // select * from xx;
    val originalText = input.getText(interval)

    val tmpTable = UUID.randomUUID().toString.replace("-","")
    sparkSession.sql(originalText).createOrReplaceTempView(tmpTable)
    engineSQLExecListener.addResult("tmpTable",tmpTable)
//    engineSQLExecListener.sparkSession.sql(s"select * from ${tmpTable}").show(10)
  }
}
