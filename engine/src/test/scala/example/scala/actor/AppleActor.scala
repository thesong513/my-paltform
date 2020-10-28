package example.scala.actor

import scala.actors.Actor

/**
 * @Author thesong
 * @Date 2020/10/27 16:47
 * @Version 1.0
 */

case class SynMsg(id:Int, msg:String)
case class AsynMsg(id:Int, msg:String)
case class ReplyMsg(id:Int, msg:String)

class AppleActor extends Actor{
  override def act(): Unit = {
    while(true){
      receive{
        case "start"=>{
          println("start...")
        }
        case SynMsg(id,msg)=>{
          println(id+", sync msg:"+ msg)
          Thread.sleep(5000)
          this.sender ! ReplyMsg(3, "finished")
        }
        case AsynMsg(id, msg)=>{
          println(id+"async: "+msg)
          Thread.sleep(5000)
        }
        case _=>{
          print("无效的消息，无法处理")
        }
      }
    }
  }
}

object  AppleActor{
  def main(args: Array[String]): Unit = {
    val a = new AppleActor
    a.start()
    //异步消息发送
    a ! AsynMsg(1,"hello actor")

    val reply = a !! SynMsg(2, "hello actor")

    println(reply.apply())
    println(reply.isSet)

  }
}
