package example.scala.actor

import scala.actors.Actor._
import scala.actors.remote.Node
import scala.actors.remote.RemoteActor._

/**
 * @Author thesong
 * @Date 2020/10/28 10:44
 * @Version 1.0
 */
object Client {
  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      println("usage: scala client [msg]")
      return
    }
    actor {
      val actor1 = select(Node("localhost", 8080), 'myapp)
      actor1 !? args(0) match {
        case msg => println("server response is " + msg)
      }
    }
  }
}

