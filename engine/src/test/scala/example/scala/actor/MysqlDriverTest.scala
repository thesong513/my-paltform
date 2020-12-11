package example.scala.actor

import org.apache.spark.sql.{DataFrame, DataFrameReader, SparkSession}
import org.apache.spark.util.Utils

/**
 * @Author thesong
 * @Date 2020/12/11 10:24
 * @Version 1.0
 * @Describe
 */
object MysqlDriverTest extends App {

  val sparkSession = SparkSession.builder().master("local").appName("testmysql").getOrCreate()
  val reader:DataFrameReader = sparkSession.read;
  reader.format("jdbc")
    .option("url", "jdbc:mysql://localhost:3306/mysql")
    .option("driver", "com.mysql.jdbc.Driver")
    .option("dbtable", "tb")
    .option("user", "root")
    .option("password", "jufeng@2020")
  val df = reader.load()
  df.show()



}
