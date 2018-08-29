package com.hochoy.scalatest.exception

import scala.collection.mutable.ListBuffer

/**
  * Created by Cobub on 2018/8/26.
  */
object ExceptionExample {

  def main(args: Array[String]) {
//    val s = testDevice(Array(9.1,8,7,6,9),Array(3,2.2,0,2))
//    println(s)

    val t =test2(2,1)
    println(t)
  }


  def test2(a:Int,b:Int): Int ={
    try{
      val d = a / b
      val arr = Array(1,2,3)
      arr(5) +d
    }catch {
      case e:ArithmeticException => {
        println("kkkkkkkk...."+e)
        10001
      }
      case ex:Throwable =>{
        println("tttttttttt......"+ex)
        100009
      }
    }
  }

  def testDevice( a:Array[Double] , b:Array[Double]): ListBuffer[Double]={
    var l = new ListBuffer[Double]()
    for(i <- 0 to a.length-1){
      try {
        val s = a(i) / b(i)
        l.+=(s)
      }catch {
        case e: ArithmeticException => println(e)
        case ex: Throwable =>println(".........found a unknown exception:  "+ ex)
      }
    }
    println("Rest of the code is executing...")
    l
  }


}
