package com.hochoy.scalatest.collection

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/**
  * Created by Cobub on 2018/7/25.
  */
object Buffer_Builder {

  def main(args: Array[String]) {
//    arrayBufferMade
    arrayBulderMade
  }
  def arrayBufferMade(): Unit ={
    var aBuffer = ArrayBuffer[Int]()
    for (i <- 0 until 1000000){
      aBuffer += i
    }
    println(aBuffer.toArray.length)
  }
  def arrayBulderMade(): Unit ={
    var aBuilder = new mutable.ArrayBuilder.ofInt
    for (i<- 0 until 1000000){
      aBuilder += i
    }
    println(aBuilder.result().length)
  }

}
