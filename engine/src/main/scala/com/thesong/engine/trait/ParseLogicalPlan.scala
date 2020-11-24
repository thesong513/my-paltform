package com.thesong.engine.`trait`

import com.thesong.engine.antlr.EngineParser.SqlContext

/**
 * @Author thesong
 * @Date 2020/11/24 11:27
 * @Version 1.0
 * @Describe
 */
trait ParseLogicalPlan {
  def parse(ctx:SqlContext):Unit

}

trait ParseLogicalTools {
  def cleanStr(str: String): String = {
    str match {
      //把sql输出的内容提取出来
      case x if x.startsWith("```") && x.endsWith("```") => x.substring(3, x.length - 3)
      case x if x.startsWith("'") && x.startsWith("\"") || x.startsWith("`") => x.substring(1, x.length - 1)
      case _ => str
    }
  }
}
