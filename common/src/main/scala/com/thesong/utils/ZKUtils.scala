package com.thesong.utils

import com.thesong.domain.engine.PlatEngine
import org.I0Itec.zkclient.ZkClient
import org.I0Itec.zkclient.exception.{ZkMarshallingError, ZkNoNodeException, ZkNodeExistsException}
import org.I0Itec.zkclient.serialize.ZkSerializer
import org.apache.zookeeper.ZKUtil
import org.apache.zookeeper.data.Stat

import scala.collection.Seq

/**
 * @Author thesong
 * @Date 2020/11/2 17:17
 * @Version 1.0
 * @Describe
 */
class ZKUtils {
  var zkClient: ZkClient = null

  val sessionTimeout = 60000
  val connectionTimeout = 60000

  //引擎路径(zookeeper中的路径),路径就是节点(持久化的节点，一直存在)
  var engine_path = "/platform/engine"

  //actor的引擎路径
  var valid_engine_path = "/platform/valid_engine"


  def getZkClient(zkServers: String): ZkClient = {
    if (zkClient == null) {
      zkClient = new ZkClient(zkServers, sessionTimeout, connectionTimeout, new ZkSerializer {
        override def serialize(o: Any): Array[Byte] = {
          try {
            // 对zk中存储的数据进行序列化
            o.toString.getBytes("UTF-8")

          } catch {
            case e: ZkMarshallingError => return null
          }
        }

        override def deserialize(bytes: Array[Byte]): AnyRef = {
          try {
            //对zk数据进行反序列化
            new String(bytes, "UTF-8")
          } catch {
            case e: ZkMarshallingError => return null
          }
        }
      })
    }
    zkClient
  }


  def createPersistentPathIfNotExists(zkClient: ZkClient, path: String): Unit = {
    if (zkClient.exists(path)) {
      // true 父目录不存在的时候优先创建父目录，再创建子目录
      zkClient.createPersistent(path, true)
    }

  }

  // 创建父目录
  def createParentPath(path: String): Unit = {
    val parentDir = path.substring(0, path.lastIndexOf("/"))
    if (parentDir.length != 0) {
      zkClient.createPersistent(parentDir, true)
    }
  }

  def createEphemeralPathAndParentPathIfNotExits(zkClient: ZkClient, path: String, data: String): Unit = {
    zkClient.createEphemeral(path, data)
  }

  /**
   * 向zk中注册引擎
   *
   * @param zkClient
   * @param id
   * @param host
   * @param port
   */

  def registerEngineInZookeeper(zkClient: ZkClient, id: Int, host: String, port: Int): Unit = {
    val brokerIdPath = ZKUtils.engine_path
  }


}
