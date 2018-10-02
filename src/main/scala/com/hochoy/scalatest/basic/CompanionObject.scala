package com.hochoy.scalatest.basic

/**
  * Created by Cobub on 2018/10/2.
  *
  * 与类名同名并且用object修饰的对象称为该类的半生对象
  * 类和其半生对象可以相互访问私有属性和方法，他们必须存在于同一个源文件中
  */
class CompanionObject {

}
class Dog{
  private var name = "二哈。。"
  private def printName():Unit={
    println(Dog.constant+name)
  }
}
object Dog{
  private val constant = "ho hoho..."
  def main(args: Array[String]) {
    val dog = new Dog
    println(s"${dog.name}")

    dog.name = "  大黄  "

    dog.printName()
  }
}
