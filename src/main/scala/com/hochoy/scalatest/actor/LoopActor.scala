package com.hochoy.scalatest.actor

import scala.actors.Actor

/** Describe:
  * Created by IntelliJ IDEA.
  * Time: 9:35
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
class LoopActor extends Actor {

  def act() {
    while (true){
      receive{
        case Login(username,password) ⇒println(s" login username is  $username, password is $password")
        case Register(username,password) ⇒ println(s"register username is $username,password is $password")
      }
    }
  }

}

case class Login(username:String,password:String)
case class Register(username:String,password:String)

object test1 {

  def main(args: Array[String]): Unit = {
    val actor = new LoopActor
    actor.start()
    actor ! Register("admin","1234")
    actor ! Login("test","123456")
  }
}

case class Message(content: String, sender: Actor)
class LeoTelephoneActor extends Actor{
  override def act(): Unit = {
    while (true){
      receive{
        case Message(content,sender) ⇒{println(s"leo phone : $content");sender ! "i'm Leo,please call me after 10 min"}
      }
    }
  }
}
class JackTelephoneActor(val leoTelephoneActor: Actor) extends Actor{
  def act(): Unit ={
    leoTelephoneActor ! Message("hello, leo ,I am jack",this)
    receive{
      case response:String ⇒ println(s"jack telephone : $response")
    }
  }
}

import scala.actors.Actor

class Loop1Actor extends Actor{

  override def act(): Unit = {
    while (true){
      receive{
        case s:String ⇒ println(s"................s is $s")
      }
    }
  }
}
object t{
  def main(args: Array[String]): Unit = {
    val l = new Loop1Actor()
    l.start()
    l ! "哈哈哈哈哈哈 哈哈哈     "
  }
}
import scala.actors.Actor._
object tt{

  def main(args: Array[String]): Unit = {
    val actor2 = actor{
      while (true){
        receive{
          case s:String ⇒ println(s"slslslsl................   $s")
        }
      }
    }
    actor2.start()
    actor2 ! "nihaoa "
    actor2 ! "hello world "
  }
}

object A1 extends Actor{
  def act(): Unit ={
    loop({
      receive({
        case (n1:Int,n2:Int,act:Actor)⇒act ! (n1 + n2)
      })
    })
  }
}
object actorTest{
  def main(args: Array[String]): Unit = {
    A1.start()
    A1 ! (1,2,self)
    println(self.receive{case x⇒x})
  }
}

object T{  def main(args: Array[String]): Unit = {    println("hello world ....")  } }
