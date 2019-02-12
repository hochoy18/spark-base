package com.hochoy.scalatest.actor

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

/** Describe:
  *
  * https://blog.csdn.net/u012302681/article/details/50667783
  *
  * Created by IntelliJ IDEA.
  * Time: 9:59
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
object TestActor {

  import scala.actors.Actor

  def main(args: Array[String]): Unit = {
    calculate(10)
  }

  /**
    * 给定一个Int整数number ，使用actor计算从1 到number的总和
    *
    * @param number
    */
  def calculate(number: Int): Unit = {
    val num = number
    val caller = Actor.self //获得当前线程的引用
    for (i ← 1 to num) {
      Actor.actor {
        caller ! { //调用 ! 发送消息
          println(i) // 打印每次发送的i
          i //发送i，下面receive中的case的 sumInSent

        }
      }
    }
    // 下句的 /: 等效于List.foldLeft方法
    val sum = (0 /: (1 to num)) {
      (partialSum, elem) ⇒
        Actor.receive {
          case sumInSent: Int ⇒ partialSum + elem
        }
    }
    println(s"sum =  $sum")
  }

}

// 服务端发送消息到客户端
case class ServerMessage(msg: String)

// 客户端发送消息到服务端
case class ClientMessage(msg: String)

//创建服务端Actor
class Server extends Actor {
  override def receive: Receive = {
    case "start" ⇒ println("服务端已启动....")
    case ClientMessage(msg) ⇒ {
      println(s"服务端接收到：$msg")
      Thread.sleep(3000)
      //sender 返回给客户端相应消息
      sender ! ServerMessage(s"服务端已收到消息：${msg}")
    }
  }
}

object Server extends App {
  //服务端的IP地址
  val host: String = "192.168.1.70"
  //服务端的端口号（自定义设置，只要端口不被占用即可）
  val port: Int = 5555
  // 通信协议
  val conf = ConfigFactory.parseString(
    s"""|akka.actor.provider="akka.remote.RemoteActorRefProvider"
        |akka.remote.netty.tcp.hostname=$host
        |akka.remote.netty.tcp.port=$port
     """.stripMargin
  )
  val actorSystem : ActorSystem = ActorSystem("sys-hochoy", conf)

  /**
    * 调用ActorSystem.actorOf(..) 创建Actor ，
    *
    * << actorOf / actorSelection / actorFor的区别 >>
    *
    * actorOf 创建一个新的actor，创建的actor为调用该方法所属的context的直接子actor。
    *
    * actorSelection 查找现有actor，并不会创建新的actor
    */
  val serverActorRef :ActorRef = actorSystem.actorOf(Props[Server], "server-hochoy")
  serverActorRef ! "start"
}

class Client(host: String, port: Int) extends Actor {
  var serverActorRef: ActorSelection = _

  override def preStart(): Unit = {
    serverActorRef = context.actorSelection(s"akka.tcp://sys-hochoy@${host}:${port}/user/server-hochoy")
  }

  override def receive: Receive = {
    case "start" ⇒ println("客户端已启动....")
    case msg: String ⇒ {
      serverActorRef ! ClientMessage(msg)
    }
    case ServerMessage(msg) ⇒ {
      println(s"响应消息 ： $msg")
    }
  }
}

object Client extends App {
  val host: String = "192.168.1.70"
  val port: Int = 7777

  val serverPort: Int = 5555
  val conf = ConfigFactory.parseString(
    s"""|akka.actor.provider="akka.remote.RemoteActorRefProvider"
        |akka.remote.netty.tcp.hostname=$host
        |akka.remote.netty.tcp.port=$port
     """.stripMargin
  )
  val clientSystem = ActorSystem("client",conf)
  val actorRef = clientSystem.actorOf(Props(new Client(host,serverPort)),"client")
  actorRef ! "start"
  while (true){
    print("客户端：")
    val str = StdIn.readLine()
    actorRef !(str)
  }
}
