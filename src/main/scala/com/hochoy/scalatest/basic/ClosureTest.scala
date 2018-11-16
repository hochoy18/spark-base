package com.hochoy.scalatest.basic

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月16日 11:54
  * @version :  V1.0
  */
object ClosureTest {


  def main(args: Array[String]) {
    val addO = makeAdd(1)
    val addT = makeAdd(2)
    println(addO(1))
    println(addT(2))
  }

  def makeAdd(more:Int) = (x:Int) => x + more
  def normalAdd(a:Int ,b:Int) = a + b
}