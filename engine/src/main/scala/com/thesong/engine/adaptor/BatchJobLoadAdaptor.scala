package com.thesong.engine.adaptor

import com.thesong.engine.EngineSQLExecListener
import com.thesong.engine.`trait`.ParseLogicalTools
import com.thesong.utils.GlobalConfigUtils
import org.apache.spark.sql.{DataFrame, DataFrameReader}

/**
 * @Author thesong
 * @Date 2020/12/1 13:32
 * @Version 1.0
 * @Describe
 */
class BatchJobLoadAdaptor(engineSQLExecListener: EngineSQLExecListener,
                          var format: String,
                          var path: String,
                          var tavleName: String,
                          option: Map[String, String]
                         ) extends ParseLogicalTools {

  val parse = {
    var table: DataFrame = null;
    val frameReader: DataFrameReader = engineSQLExecListener.sparkSession.read
    frameReader.options(option)
    format match {
      case "jdbc" => {
        frameReader.option("dbtable", path)
          .option("driver", option.getOrElse("driver", GlobalConfigUtils.getProp("jdbc.driver")))
          .option("url", option.getOrElse("url", GlobalConfigUtils.getProp("jdbc.url")))
        table = frameReader.load()
      }
      case "hbase" => {}
      case "es" => {}
      case "kafka" => {}
      case "json" | "csv" | "orc" | "parquet" | "text" => {
        table = frameReader.option("header", "true").format(format).load(path)
      }
      case "xml" => {}
      case "redis" => {}
      case "jsonStr" => {}
      case _ => {}
    }

    // 注册进入sparkSession
    table.createTempView(tavleName)
    //
    //    val retDf = engineSQLExecListener.sparkSession.sql("select * from tb");
    //    retDf.show(numRows = 10)
  }

}
