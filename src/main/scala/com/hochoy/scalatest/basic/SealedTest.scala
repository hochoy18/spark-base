package com.hochoy.scalatest.basic

/**
  * Created by Cobub on 2018/10/9.
  * sealed 关键字有两个作用：
  * 1.  防止继承滥用
  *     其修饰的trait，class只能在当前文件里面被继承；
  *     即：​sealed关键字可以修饰类和特质（特质）。密封类提供了一种约束：不能在类定义的文件之外定义任何新的子类
  *
  * 2.  模式匹配
  *     在检查模式匹配的时候，用sealed修饰目的是让scala知道这些case的所有情况，
  * scala就能够在编译的时候进行检查，看你写的代码是否有没有漏掉什么没case到，减少编程的错误。
  */

sealed abstract class Person2

case class Student2(name: String, sno: Int) extends Person2

case class Teacher2(name: String, tno: Int) extends Person2


object SealedTest extends App {
  val p: Person2 = Student2("yy", 21)
  p match {
    /**
      * 在检查模式匹配的时候，用sealed修饰目的是让scala知道这些case的所有情况，scala就能够在编译的时候进行检查，
      * 看你写的代码是否有没有漏掉什么没case到，减少编程的错误。此时会报：
      *
      * Warning:(17, 3) match may not be exhaustive.
      * It would fail on the following input: Teacher2(_, _)
      * p match {
      *
      * 如果把
      * val p: Person2 = Student2("yy", 21)
      * 改成
      * val p: Person2 = Teacher2("yy", 21)
      * 会报错 scala.MatchError
      *
      */
    case Student2(name, sno) => println(name + " is a student.")
  }
  p match {
    case Teacher2(name, tno) => {
      println(name + " is a teacher.")
      println("teacher......")
    }
    case Student2(name, sno) => {
      println(name + " is a student.")
      println("student......")
    }
  }
  //  def main(args: Array[String]) {
  //
  //  }

}

sealed abstract  class People
case object American extends People
case object Japanese extends People
case object Chinese extends People
case object Russia extends People

object SealedTest01{
  def people(p:People) = p match {
    case American => println("American person....")
    case Japanese => println("Japanese person...")
    case Chinese => println("Chinese person ...")
  }

  def main(args: Array[String]) {
    people(Russia)
    people(Chinese)
  }
}





