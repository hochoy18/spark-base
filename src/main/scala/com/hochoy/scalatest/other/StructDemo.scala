package com.hochoy.scalatest.other

/**
  * Created by Cobub on 2018/10/2.
  * 1. scala编译器会自动为类中的字段添加getter方法和setter方法
  * 2. 可以自定义getter/setter方法来替换掉编译器自动产生的方法
  * 3. 每个类都有一个主要的构造器，这个构造器不是单独声明的构造函数，而是和类定义交织在一起。它的参数直接成为类的字段。主构造器执行类声明中所有的语句。
  * 4. 辅助构造器是可选的，它们叫做this。
  *
  *
  * 主构造器的参数列表要放在类名之后，
  * val修饰的构造参数不可变，var修饰的可改变
  * 此时声明的faceValue：Int只能在本类调用，半生对象也无法调用
  * 构造参数列表中没有用val或var修饰的默认为val
  *
  *
  */
class StructDemo(val name: String, var age: Int, faceValue: Int = 90) {
  //主构造器
  def getFaceValue(): Int ={
    /**
      * faceValue = 100 // 此值不可更改，默认为val
     */
    faceValue
  }

  var gender: String = _

  /**
    * 辅助构造器：
    * 辅助构造器第一行必须先调用主构造器
    *
    * @param name
    * @param age
    * @param faceValue
    */
  def this(name: String, age: Int, faceValue: Int, gender: String) {
    this(name, age, faceValue)
    this.gender = gender
  }
}

object StructDemo {//class StructDemo 的半生对象（object与class同名）
  def main(args: Array[String]) {
//    val s = new StructDemo("zhangsan", 20, 98)

    val s = new StructDemo("jingjing",20,98,"女")
    println(s)
    //    s.name = "lisi"
    println(s.name)
    s.age = 18
    println(s.age)

    //    s.faceValue()
    println(s.getFaceValue())
    println("gender:  "+s.gender)
  }
}
