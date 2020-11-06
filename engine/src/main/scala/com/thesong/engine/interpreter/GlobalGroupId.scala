package com.thesong.engine.interpreter

/**
 * @Author thesong
 * @Date 2020/11/6 15:06
 * @Version 1.0
 * @Describe
 */
object GlobalGroupId {
  var groupId: Int = 0

  def getGroupId: Int = {
    this.synchronized {
      groupId += 1
      groupId
    }
  }

}
