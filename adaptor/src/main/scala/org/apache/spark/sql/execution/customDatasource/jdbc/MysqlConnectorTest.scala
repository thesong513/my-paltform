package org.apache.spark.sql.execution.customDatasource.jdbc

import org.apache.spark.util.Utils

/**
 * @Author thesong
 * @Date 2020/12/11 10:42
 * @Version 1.0
 * @Describe
 */
object MysqlConnectorTest extends App {

  val cls = Utils.getContextOrSparkClassLoader.loadClass("com.mysql.jdbc.Driver")
  println(cls.getName)

}
