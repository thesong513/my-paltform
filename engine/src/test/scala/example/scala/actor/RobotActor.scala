package example.scala.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}


/**
 * @Author thesong
 * @Date 2020/10/27 17:44
 * @Version 1.0
 */

trait Action{
  val msg:String
  val time:Int
}

case  class TurnOnLight(time: Int) extends Action{
  val msg = "小爱同学，把房间灯打开"

}
case  class TurnOffLight(time: Int) extends Action{
  val msg = "小爱同学，把房间灯关闭"

}

class RobotActor extends Actor{
  override def receive: Receive = {
    case on:TurnOnLight=>{
      println(s"${on.msg} after ${on.time} hour")
    }
    case off:TurnOffLight=>{
      println(s"${off.msg} after ${off.time} hour")
    }
    case _=>{
      println("不能处理消息")
    }
  }
}


object RobotActor extends App{
  val actorSystem = ActorSystem("lianshu")
  val robotActor: ActorRef = actorSystem.actorOf(Props(new RobotActor()), name = "robotActor")
  robotActor ! TurnOnLight(1)
  robotActor ! TurnOffLight(2)
  robotActor ! "test"

}
