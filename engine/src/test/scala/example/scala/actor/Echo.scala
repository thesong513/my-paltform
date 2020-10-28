package example.scala.actor

import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.remote.RemoteActor._

/**
 * @Author thesong
 * @Date 2020/10/28 10:37
 * @Version 1.0
 */
class Echo extends Actor{
  override def act(): Unit = {
    alive(8080)
    register('myapp, self)

    loop{
      react{
        case msg=>{
          println(msg)
        }
      }
    }

  }
}

object Echo{
  def main(args: Array[String]): Unit = {
    var echo = new Echo
    echo.start
    println("echo is started")
  }
}
