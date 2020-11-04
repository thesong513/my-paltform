package com.thesong.engine

import java.io.{ByteArrayOutputStream, PrintStream}

import akka.actor.{Actor, Props}
import com.thesong.domain.{CommandMode, ResultDataType}
import com.thesong.domain.engine.{Instruction, Job}
import com.thesong.engine.interpreter.SparkInterpreter
import com.thesong.utils.{GlobalConfigUtils, ZKUtils}
import com.thesong.logging.Logging
import org.I0Itec.zkclient.ZkClient
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @Author thesong
 * @Date 2020/11/4 12:45
 * @Version 1.0
 * @Describe
 */
class JobActor (_interpreter:SparkInterpreter,
  engineSession:EngineSession,
  sparkConf:SparkConf) extends Actor with Logging {

  //组装jobActor的有效路径，存储到zookeeper（前端通过这个路径，可以获取akka引擎，然后来做前后端对接的操作）
  val valid_engine_path = s"${ZKUtils.valid_engine_path}/${engineSession.engineInfo}_${context.self.path.name}"
  var zkClient: ZkClient = _
  var token: String = _
  var job: Job = _


  var sparkSession: SparkSession = _
  var interpreter: SparkInterpreter = _

  //初始化
  override def preStart():Unit= {
    warn(
      """
        |               __                           __                 __
        |_____    _____/  |_  ___________    _______/  |______ ________/  |_
        |\__  \ _/ ___\   __\/  _ \_  __ \  /  ___/\   __\__  \\_  __ \   __\
        | / __ \\  \___|  | (  <_> )  | \/  \___ \  |  |  / __ \|  | \/|  |
        |(____  /\___  >__|  \____/|__|    /____  > |__| (____  /__|   |__|
        |     \/     \/                         \/            \/
      """.stripMargin)
    zkClient = ZKUtils.getZkClient(GlobalConfigUtils.getProp("zk.servers"))
    interpreter = _interpreter
    sparkSession = EngineSession.createSpark(sparkConf).newSession()

    ZKUtils.registerActorInPlatEngine(zkClient, valid_engine_path, engineSession.tag.getOrElse("default"))
  }

  // 处理业务
  override def receive: Receive = {
    case Instruction(commandMode, instruction, variables, _token) => {
      actorHook() { () => {
        var assemble_instruction = instruction
        token = _token
        job = Job(instruction = instruction, variables = variables)
        //获取groupId
        val groupId = GlobalGroupId.getGroupId
        job.engineInfoAndGroup = s"${engineSession.engineInfo}_${groupId}"
        //将job的信息响应到客户端
        sender() ! job

        sparkSession.sparkContext.clearJobGroup()
        sparkSession.sparkContext.setJobDescription(s"runing job:${instruction}")
        sparkSession.sparkContext.setJobGroup("groupId:" + groupId, "instruction:" + instruction)

        commandMode match {
          case CommandMode.SQL => {

          }

          case CommandMode.CODE => {
            job.mode = CommandMode.CODE

            /**
             * 处理前：
             * line1；
             * line2;
             * line3;
             *
             * 处理后:
             * line1；line2;line3;
             */
            assemble_instruction = assemble_instruction.replaceAll("'", "\"").replaceAll("\n", " ")
            val response = interpreter.execute(assemble_instruction)
          }

        }
      }
      }
    }
  }

  // 结束
  override def postStop(): Unit = {
    warn(
      """
        |             _                       _
        |  ____  ____| |_  ___   ____     ___| |_  ___  ____
        | / _  |/ ___)  _)/ _ \ / ___)   /___)  _)/ _ \|  _ \
        |( ( | ( (___| |_| |_| | |      |___ | |_| |_| | | | |
        | \_||_|\____)\___)___/|_|      (___/ \___)___/| ||_/
        |                                              |_|
      """.stripMargin)
    interpreter.close()
    sparkSession.stop()
  }

  /**
   * 钩子：通过钩子能够找到操作的对象,类似引用
   * 作用:捕获业务处理异常，方便发送到客户端进行跟踪
   *
   * @param func
   */
  def actorHook()(func: () => Unit): Unit = {
    try {
      func()
    } catch {
      case e: Exception => {
        val out = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(out))
        val job = Job(data = out.toString(), dataType = ResultDataType.ERROR)
        //通过sender 把job对象发送到客户端
        sender ! job
      }
    }
  }

}


object JobActor{
  def apply(_interpreter: SparkInterpreter, engineSession: EngineSession, sparkConf: SparkConf):Props  = {
    Props(new JobActor(_interpreter, engineSession, sparkConf))
  }
}