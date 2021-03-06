package com.thesong.engine.adaptor

import com.thesong.engine.EngineSQLExecListener
import com.thesong.utils.GlobalConfigUtils
import org.apache.spark.sql.{DataFrame, DataFrameWriter, Row, SaveMode}

/**
 * @Author thesong
 * @Date 2020/12/8 9:48
 * @Version 1.0
 * @Describe
 */
class BatchJobSaveAdaptor(
                           engineSQLExecListeen: EngineSQLExecListener,
                           data:DataFrame,
                           savePath:String,
                           tableName:String,
                           format:String,
                           mode:SaveMode,
                           partitionByCol:Array[String],
                           numPartition:Int,
                           option: Map[String,String]
                         ) {

  //定义一个函数
  val parse = {
    var writer:DataFrameWriter[Row] = data.write
    //Array,Seq
    writer = writer.format(format).mode(mode).partitionBy(partitionByCol:_*).options(option)
    format match {
      case "json" | "orc" | "parquet" | "csv" => {

      }
      case "text" => {
        data.rdd.repartition(numPartition).saveAsTextFile(savePath)
      }
      case "hbase" => {

      }
      case "jdbc" => {
        writer.format("org.apache.spark.sql.execution.customDatasource.jdbc")
          .option("dirver", option.getOrElse("driver", GlobalConfigUtils.getProp("jdbc.driver")))
          .option("url", option.getOrElse("url", GlobalConfigUtils.getProp("jdbc.url")))
          .option("dbtable",savePath)
          .save()
      }
      case "hive" => {

      }
      case "mongo" => {

      }
      case "redis" => {

      }
      case "es" => {

      }
      case _ => {

      }
    }
  }

}
