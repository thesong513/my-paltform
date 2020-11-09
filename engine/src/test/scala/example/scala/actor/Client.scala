package example.scala.actor

import java.net.InetAddress
import akka.actor.ActorSystem
import akka.pattern.Patterns
import akka.util.Timeout
import com.thesong.domain.CommandMode
import com.thesong.domain.engine.Instruction
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.Duration

/**
 * @Author thesong
 * @Date 2020/10/28 10:44
 * @Version 1.0
 */
object Client extends App {
  //客户端actor ip
  val host = InetAddress.getLocalHost().getHostAddress
  //客户端actor 端口
  val port = 3001
  val conf = ConfigFactory.parseString(
    s"""
    |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
    |akka.remote.netty.tcp.hostname = ${host}
    |akka.remote.netty.tcp.port = ${port}
    |""".stripMargin
  )

  val instruction =
    "val textFile = spark.sparkContext.textFile(\"hdfs://ns1/words\");"+
    "val count = textFile.flatMap(line=>line.split(\" \")).map(x=>(x,1)).reduceByKey(_+_);"+
    "count.repartition(1).saveAsTextFile(\"hdfs://ns1/count\")"

  val commandMode = CommandMode.CODE
  val variables = "[]"
  val token = ""

  val clientSys = ActorSystem("client", conf)

  val ip = InetAddress.getLocalHost().getHostAddress
  val actorAddr = s"${ip}:3000"
  val actorName = "actor_1"
  val selection = clientSys.actorSelection("akka.tcp://system@" + actorAddr + "/user/" + actorName)

  Patterns.ask(selection, Instruction(commandMode, instruction, variables, token), new Timeout(Duration.create(10, "s")))


}

