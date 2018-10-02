package com.hochoy.scalatest.other

/**
  * Created by Cobub on 2018/10/1.
  */
class LazyTest {


}

/**
  * Scala中使用关键字lazy来定义惰性变量，实现延迟加载(懒加载)。
  * 惰性变量只能是不可变变量，并且只有在调用惰性变量时，才会去实例化这个变量
  */
object lazyOps {
  def init(): String = {
    println("call init()...")
    return "ini...././././"
  }

  def main(args: Array[String]) {
     val prop = init()
    println("after init()...")
    println(prop)
//    加了lazy关键字print的顺序：
//    after init()...
//    call init()...
//    ini...././././
//
//    去掉lazy关键字print的顺序：
//    call init()...
//    after init()...
//    ini...././././

  }
}
