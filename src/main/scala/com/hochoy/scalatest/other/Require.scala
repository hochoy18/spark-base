package com.hochoy.scalatest.other

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月15日 17:42
  * @version :  V1.0
  */
object Require {

  def main(args: Array[String]) {
    require(args.length > 2 ,"args not allowed null")
    println(s"hehe......${args(0)}")
  }
}