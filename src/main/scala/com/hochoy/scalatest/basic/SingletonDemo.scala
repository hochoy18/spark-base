package com.hochoy.scalatest.basic

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Cobub on 2018/10/2.
  *
  * scala 中的object可以实现：
  *  1. 作为存放工具函数或常量的地方
  *  2. 高效的共享单个不可变实例
  *  3. 需要用单个实例来协调某个服务时（参考单例模式）
  *
  */
object SingletonDemo {

  def main(args: Array[String]) {
    val session = SessionFactory
    println(session)
    println(session.getSession)
    println(session.getSession.size)
    println(session.removeSession)
    println(session.getSession)
  }

}
object SessionFactory{
  println("SessionFactory executed.......")
  var i = 5
  private  val session = new ArrayBuffer[Session]

  while (i>0){
    session += new Session
    i -=1
  }
  def getSession = session
  def removeSession:Unit={
    val s = session(0)
    session.remove(0)
    println(s"session ${s} removed")
  }
}
class Session{}