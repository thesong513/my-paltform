package example.scala.actor

import scala.actors.Actor

/**
 * @Author thesong
 * @Date 2020/10/26 17:34
 * @Version 1.0
 */
class HiActor extends Actor {
  override def act(): Unit = {
    while(true){
      receive{
        case "hello" => println("hello wode hello")
        case "hi" => println("hi")
        case _=> println("无法处理数据")
      }
    }

  }
}

object HiActor{
  def main(args: Array[String]): Unit = {
    val hiActor = new HiActor
    hiActor.start()
    hiActor ! "hello"
  }
}
