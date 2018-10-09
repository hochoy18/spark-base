package com.hochoy.scalatest.basic

/**
  * Created by Cobub on 2018/10/9.
  */

class A{
  class B
}
object CurryTest {

  /**
    * 定义为柯里化函数时没有问题的，b的类型可以由a参与得出
    */
  def method(a: A)(b: a.B) {

  }
  /**
  参数列表的第二个参数，a没有定义，不能参与b的类型推断
  def method2(a: A, b: a.B /*a is not defined in a.B*/) {

  }
  */

  case class Email(subject:String,text:String,sender:String,recipient:String)
  type EmailFilter = Email => Boolean

}

