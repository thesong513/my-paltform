package com.thesong.engine

/**
 * @Author thesong
 * @Date 2020/11/4 12:09
 * @Version 1.0
 * @Describe
 */
package object config {

  val PARALLELISM = ConfigBuilder("platform.engine.parallelism")
    .doc("引擎的并行度")
    .intConf
    .createWithDefault(6)
}
