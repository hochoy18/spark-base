package com.hochoy.scalatest.basic


/**
  * @since Date : 2018年11月06日 21:09
  * @note TODO
  * @author : hochoy
  * @version V1.0
  *
  */



object CurryingTest {


  def currying(str:String)(implicit name:String = "Tomas lee") = {
    val v = str + "'s name is  "+name
    v
  }

  def main(args: Array[String]) {
    import Constant.scala
    val v = currying("Tom")
    println(v)
  }
}
object Constant{
  implicit val java = "java"
  implicit val scala = "scala"
}