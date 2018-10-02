package com.hochoy.scalatest.basic

/**
  * Created by Cobub on 2018/10/2.
  *
  * apply 方法通常称为注入方法，在伴生对象里做一些初始化的操作
  * apply 方法的参数列表不需要和构造器的参数列表一致
  * unapply 方法通常称为提取方法,使用该方法提取固定数量对象
  * unapply 方法返回一个序列（Option），内部产生了一个Some对象来存放一些值
  * unapply方法主要用于模式匹配中。
  * apply 方法和 unapply方法会被隐式调用
  */
class ApplyTest {
}

class ApplyFoo(foo: String) {
  println("class ...")
}

object ApplyFoo {
  def apply(foo: String): ApplyFoo = {
    println("obj....")
    new ApplyFoo(foo)
  }
}

object client {
  def main(args: Array[String]) {
    val foo = new ApplyFoo("hahaha")
    println(foo)
    val foo1 = ApplyFoo("hohohoho ")
    println(foo1)
  }
}

class applyDemo(val name: String, var age: Int, var faceValue: Int) {

}

object applyDemo {
  var gender : Int =_
  def apply(name: String,age:Int,faceValue:Int, gender:Int):applyDemo =
    {
      this.gender = gender
      new applyDemo(name,age,faceValue)
    }

  def unapply(applyDemo :applyDemo):Option[(String,Int,Int)] = {
    if (applyDemo == null){
      None
    }else {
      Some(applyDemo.name,applyDemo.age,applyDemo.faceValue)
    }
  }
}
object applyDemoTest{
  def main(args: Array[String]) {
    val applyDemo_ = applyDemo("jingjing",19,99,0)
    applyDemo_ match {
      case applyDemo(name1,age,faceValue) =>
        println(s"name is ${name1}, age is $age, faceValue is $faceValue")
      case _ =>println("nothing  match ")
    }
  }
}
