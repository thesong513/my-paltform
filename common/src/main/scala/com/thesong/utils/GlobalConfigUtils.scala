package com.thesong.utils

import com.typesafe.config.ConfigFactory

/**
 * @Author thesong
 * @Date 2020/11/2 18:13
 * @Version 1.0
 * @Describe
 */
class GlobalConfigUtils {
  /**
   * 加载application.conf配置文件(kv对格式的内容)
   *
   * @return
   */

  private def conf = ConfigFactory.load()


  def heartColumnFamily = "MM" //conf.getString("heart.table.columnFamily")
  val getProp = (argv: String) => conf.getString(argv)


}

/**
 * 为了配置访问的安全性的一种写法
 */
object GlobalConfigUtils extends GlobalConfigUtils
