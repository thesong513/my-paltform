package example.scala.actor

import scala.actors.Actor

/**
 * @Author thesong
 * @Date 2020/10/26 17:43
 * @Version 1.0
 */

case class Mytest(msg: String, myActor: Actor)

class HelloAkka extends Actor {
  override def act(): Unit = {
    while (true) {
      receive {
        case "hello" => println("hello my akka")
        case "hi" => println("hi test")
        case Mytest(msg, myActor) => {
          println(msg)
          myActor ! "mytest1"
        }
        case _ => println("无法处理数据1")
      }
    }
  }
}


class HiAkka extends Actor {
  override def act(): Unit = {
    while (true) {
      receive {
        case "mytest1" => println("mytest1")
        case "mytest2" => println("mytest2")
        case _ => println("无法处理数据3")
      }
    }
  }
}


object TestAkka {
  def main(args: Array[String]): Unit = {
    val myActor = new HiAkka
        myActor.start()
    val helloAkka = new HelloAkka
    helloAkka.start()
    helloAkka ! "hello"
    helloAkka ! Mytest("========", myActor)
  }
}
