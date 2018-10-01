package com.hochoy.scalatest.other

/**
  * Created by Cobub on 2018/10/1.
  */
class LazyTest {



}
object lazyOps{
  def init():String = {
    println("call init()...")
    return "ini...././././"
  }

  def main(args: Array[String]) {
     val prop = init()
    println("after init()...")
    println(prop)
  }
}
