package com.thesong.engine.adaptor.DML

import com.thesong.engine.EngineSQLExecListener
import com.thesong.engine.`trait`.{ParseLogicalPlan, ParseLogicalTools}
import com.thesong.engine.adaptor.BatchJobLoadAdaptor
import com.thesong.engine.antlr.EngineParser
import com.thesong.engine.antlr.EngineParser.{BooleanExpressionContext, ExpressionContext, FormatContext, PathContext, TableNameContext}

/**
 * @Author thesong
 * @Date 2020/11/24 11:41
 * @Version 1.0
 * @Describe
 */
class LoadAdaptor(engineSQLExecListener: EngineSQLExecListener) extends ParseLogicalPlan with ParseLogicalTools {
  // format 代表将要操作的数据类型（Text，jdbc，csv，json）
  var format = ""
  var path = ""
  var tableName = ""
  var option = Map[String, String]()

  //    : ('load'|'LOAD') format '.'? path ('where' | 'WHERE')? expression? booleanExpression*  'as' tableName
  override def parse(ctx: EngineParser.SqlContext): Unit = {
    (0 until ctx.getChildCount).foreach(tokenIndex => {
      ctx.getChild(tokenIndex) match {
        case s: FormatContext => {
          format = s.getText
        }
        case s: ExpressionContext => { //匹配where后面表达式内容
          option += (cleanStr(s.identifier().getText) -> cleanStr(s.STRING().getText))
        }
        case s: PathContext => {
          path = cleanStr(s.getText)
        }
        case s: BooleanExpressionContext => {
          //ps = 100
          option += (cleanStr(s.expression().getText) -> cleanStr(s.expression().STRING().getText))
        }
        case s: TableNameContext => {
          tableName += s.getText
        }
        case _ =>
      }
    })

    //判断sparkjob 是流式还是离线
    if (option.contains("spark.job.mode") && option("spark.job.mode").equals("stream")) {
      println(s"流式处理：formmat=${format},path:${path},tablename:${tableName}")
    } else {
      println(s"离线处理：formmat=${format},path:${path},tablename:${tableName}")
      new BatchJobLoadAdaptor(engineSQLExecListener, format, path, tableName, option).parse
    }
  }
}

