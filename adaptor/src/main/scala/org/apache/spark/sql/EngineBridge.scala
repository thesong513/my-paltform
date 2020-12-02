package org.apache.spark.sql

import java.io.CharArrayWriter

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.catalyst.json.JacksonGenerator
import org.apache.spark.sql.catalyst.json.JSONOptions

/**
 * @Author thesong
 * @Date 2020/12/2 9:55
 * @Version 1.0
 * @Describe
 */
class EngineBridge {





}

object EngineBridge{

  def toJSON(dataFrame:DataFrame): String = {
    val rowSchema = dataFrame.schema
    val writer = new CharArrayWriter()

    val gen = new JacksonGenerator(rowSchema, writer)

    val encoder = RowEncoder.apply(rowSchema).resolveAndBind()
    val res = dataFrame.collect().map(row => {
      gen.write(encoder.toRow(row))
      gen.flush()
      val json = writer.toString
      json
    })
    gen.close()
    res.mkString("")
  }

  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder().master("local").appName("test").getOrCreate()
    val df = sparkSession.createDataFrame(Seq(("ming", 20, 131),("li", 30,50))).toDF("name","age","phoneNum")

    df.show()
    val strings = toJSON(df)
    println(strings)


  }


}
