package com.thesong.engine.adaptor.DML

import com.thesong.engine.EngineSQLExecListener
import com.thesong.engine.`trait`.{ParseLogicalPlan, ParseLogicalTools}
import com.thesong.engine.adaptor.BatchJobSaveAdaptor
import com.thesong.engine.antlr.EngineParser
import com.thesong.engine.antlr.EngineParser.{AppendContext, BooleanExpressionContext, ColContext, ErrorIfExistsContext, ExpressionContext, FormatContext, IgnoreContext, NumPartitionContext, OverwriteContext, PathContext, SqlContext, TableNameContext, UpdateContext}
import org.apache.spark.sql.{DataFrame, SaveMode}

/**
 * @Author thesong
 * @Date 2020/12/7 14:18
 * @Version 1.0
 * @Describe
 */
class SaveAdaptor(engineSQLExecListener: EngineSQLExecListener) extends ParseLogicalPlan with ParseLogicalTools {

  var data: DataFrame = null
  var mode = SaveMode.ErrorIfExists
  // 文件系统路径
  var save_path: String = ""
  // 存储数据的引擎：hdfs，hbase，redis
  var format: String = ""
  var option = Map[String, String]()
  var tableName: String = ""
  var partitionByCol = Array[String]()
  var numPartition: Int = 1

  override def parse(ctx: SqlContext): Unit = {
    (0 until ctx.getChildCount()).foreach(tokenIndex=>{
      ctx.getChild(tokenIndex) match {
        case tag: OverwriteContext => {
          mode = SaveMode.Overwrite
        }
        case tag: AppendContext => {
          mode = SaveMode.Append
        }
        case tag :IgnoreContext=> {
          mode = SaveMode.Ignore
        }
        case tag :ErrorIfExistsContext=> {
          mode = SaveMode.ErrorIfExists
        }
          // spark 目前不支持 update操作，后续实现让spark支持update
        case tag :UpdateContext=> {
          option += ("savemode"->"update")
      }
        case tag :TableNameContext=> {
          tableName = tag.getText
          // dataframe 注册到sparkSession临时视图中去，这样才可以获取
          data = engineSQLExecListener.sparkSession.table(tableName)
        }
        case tag:FormatContext=>{
          format = tag.getText
        }
        case tag:PathContext=>{
          save_path = cleanStr(tag.getText)
        }
        case tag:ExpressionContext=>{
          // where关键字后面的条件获取方式
          option+=(cleanStr(tag.identifier().getText)->cleanStr(tag.STRING().getText))
          // select * from where name=? and age=?
        }
        case tag:BooleanExpressionContext=>{
          option+=(cleanStr(tag.expression().identifier().getText)->cleanStr(tag.expression().STRING().getText))
        }
        case tag:ColContext=>{
          partitionByCol = cleanStr(tag.getText).split(",")
        }
        case tag:NumPartitionContext=>{
          numPartition = tag.getText.toInt
        }
        case _=>{}
      }
    })

    // 流处理和批处理判断
    if(engineSQLExecListener.env().contains("stream")){
      // 流处理save操作

    }else{
      //批处理save操作
      new BatchJobSaveAdaptor(
        engineSQLExecListener,
        data,
        save_path,
        tableName,
        format,
        mode,
        partitionByCol,
        numPartition,
        option
      ).parse

    }
  }
}
