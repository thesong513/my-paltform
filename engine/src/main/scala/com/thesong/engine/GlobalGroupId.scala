package com.thesong.engine

/**
 * @Author thesong
 * @Date 2020/11/4 13:08
 * @Version 1.0
 * @Describe
 */
object GlobalGroupId {
  var groupId:Int =0
  def getGroupId:Int={
    this.synchronized {
      groupId += 1
      getGroupId
    }
  }

}
