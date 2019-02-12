package com.hochoy.scalatest.actor

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

/** Describe:
  * Created by IntelliJ IDEA.
  * Time: 17:40
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
object MessageSendReceiveActor {
}

// 服务端发送消息到客户端
case class MSRServerMessage(msg: String)

// 客户端发送消息到服务端
case class MSRClientMessage(msg: String)

class MSRServer(host: String, port: Int) extends Actor {
  var clientActorRef: ActorSelection = _

  override def preStart(): Unit = {
    clientActorRef = context.actorSelection(s"akka.tcp://client-hochoy@${host}:${port}/user/client-test")
  }

  override def receive: Receive = {
    case "start" ⇒ println("服务端已经启动....")
    case msg: String ⇒ {
      clientActorRef ! MSRServerMessage(msg)
    }
    case MSRClientMessage(msg) ⇒ {
      println(s"服务端：$msg")
    }
  }
}

object MSRServer extends App {
  val host: String = "192.168.1.70"
  val port: Int = 9999
  val clientHost: String = "192.168.1.70"
  val clientPort: Int = 8888

  val conf = ConfigFactory.parseString(
    s"""|akka.actor.provider="akka.remote.RemoteActorRefProvider"
        |akka.remote.netty.tcp.hostname=$host
        |akka.remote.netty.tcp.port=$port
     """.stripMargin
  )
  val serverSystem = ActorSystem("server-hochoy",conf)
  val actorRef = serverSystem.actorOf(Props(new MSRServer(clientHost,clientPort)),"server-test")
  actorRef ! ("start")
  while (true){
    print("服务端： ")
    val str = StdIn.readLine()
    actorRef.!(str)
  }

}
class MSRClient(host:String,port:Int)extends Actor{
  var serverActorRef:ActorSelection = _

  override def preStart(): Unit = {
    serverActorRef = context.actorSelection(s"akka.tcp://server-hochoy@${host}:${port}/user/server-test")
  }

  override def receive: Receive = {
    case "start" ⇒ println("客户端已启动....")
    case msg:String⇒{
      serverActorRef.!(MSRClientMessage(msg))
    }
    case MSRServerMessage(msg)⇒ println(s"收到服务端消息：$msg")
  }
}

object MSRClient extends App{
  val host :String = "192.168.1.70"
  val port:Int = 8888

  val serverHost :String = "192.168.1.70"
  val serverPort:Int = 9999

  val conf = ConfigFactory.parseString(
    s"""|akka.actor.provider="akka.remote.RemoteActorRefProvider"
        |akka.remote.netty.tcp.hostname=$host
        |akka.remote.netty.tcp.port=$port
      """.stripMargin
  )

  val clientSystem = ActorSystem("client-hochoy",conf)
  val actorRef = clientSystem.actorOf(Props(new MSRClient(serverHost,serverPort)),"client-test")
  actorRef.!("start")
  while (true){
    val str = StdIn.readLine()
    actorRef.!(str)
  }

}