package com.thesong.utils

import com.thesong.domain.engine.PlatEngine
import org.I0Itec.zkclient.ZkClient
import org.I0Itec.zkclient.exception.{ZkMarshallingError, ZkNoNodeException, ZkNodeExistsException}
import org.I0Itec.zkclient.serialize.ZkSerializer
import org.apache.zookeeper.data.Stat

import scala.collection.Seq

/**
 * @Author thesong
 * @Date 2020/11/2 17:17
 * @Version 1.0
 * @Describe
 */
object ZKUtils {
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
    // 规划引擎路径
    val brokerIdPath = ZKUtils.engine_path + s"/${id}"
    // 引擎信息 ip:port
    val brokeInfo = s"${host}:${port}"
    try {
      createPersistentPathIfNotExists(zkClient, ZKUtils.engine_path)
      createEphemeralPathAndParentPathIfNotExits(zkClient, brokerIdPath, brokeInfo)
    } catch {
      case e: ZkNodeExistsException => {
        throw new RuntimeException("注册失败，节点已存在")
      }
    }
  }


  def readDataMaybeNotExist(zkClient: ZkClient, path: String): (Option[String], Stat) = {
    val stat = new Stat()
    val dataAndStat = try {
      (Some(zkClient.readData(path, stat)), stat)
    } catch {
      case e1: ZkNoNodeException => (None, stat)
      case e2: Throwable => throw e2
    }
    dataAndStat
  }

  // 获取引擎
  def getPlatEngine(zkClient: ZkClient, engineId: Int): Option[PlatEngine] = {
    //指定节点路径（engineId），获取不同的节点信息（不同引擎信息）
    val dataAndStat: (Option[String], Stat) = ZKUtils.readDataMaybeNotExist(zkClient, ZKUtils.engine_path + s"/${engineId}")

    dataAndStat._1 match {
      case Some(engineInfo) => {
        Some(PlatEngine(engineId, engineInfo))
      }
      case None => None
    }

  }

  //获取子节点
  def getChildrenMayNotExist(client: ZkClient, path: String): Seq[String] = {
    import scala.collection.JavaConversions._
    try {
      client.getChildren(path)
    } catch {
      case e: ZkNoNodeException => return Nil
      case e2: Throwable => throw e2
    }
  }

  // 从多台机器获取platformEngine
  def getPlatEngineInCluster(zkClient: ZkClient): Seq[PlatEngine] = {
    val childSortedData: Seq[String] = ZKUtils.getChildrenMayNotExist(zkClient, engine_path).sorted
    val childrenSortedData2Int: Seq[Int] = childSortedData.map(x => x.toInt)
    val engineDatas: Seq[Option[PlatEngine]] = childrenSortedData2Int.map(x => ZKUtils.getPlatEngine(zkClient, x))
    val platEngines: Seq[PlatEngine] = engineDatas.filter(_.isDefined).map(x => x.get)
    platEngines
  }


}
