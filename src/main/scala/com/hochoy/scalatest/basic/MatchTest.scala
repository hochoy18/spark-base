package com.hochoy.scalatest.basic

/**
  * Created by Cobub on 2018/10/4.
  */
class MatchTest {

}

object MatchTest {
  /**
    * 2.守卫
    * 我们修改一下例一,可以添加if这样的守卫，来匹配所有数字，守卫可以是任何Boolean条件
    *
    * @param c
    * @return
    */
  def judgeGrade(c: Char) = c match {
    case '+' => 1
    case '-' => -1
    case c if (Character.isDigit(c)) => Character.digit(c, 10)
    case _ => 0
  }

  def judgeGrade1(name: String, grade: String): Unit = {
    grade match {
      case "A" => println(name + ", you are excellent")
      case "B" => println(name + ",you are good")
      case "C" => println(name + ",you are just so so ")
      case o if name == "hochoy" => println(name + ", you are a good boy ,come on")
      case _ => println("you need to work harder")

    }
  }

  /*  import math._

    def varMatch(d: String) = d match {
      case `Pi` => 3.14
      case n => Integer.parseInt(d)
      case _ => 0
    }*/

  def anyMatch(obj: Any) = obj match {
    case i: Int => i
    case s: String => Integer.parseInt(s)
    case _: BigInt => Int.MaxValue
    case m: Map[_, _] => 1
    case _ => 0
  }

  def main(args: Array[String]) {
    val t = judgeGrade('8')
    println(t)
    judgeGrade1("hochoy", "D")
    //    varMatch("Pi")

    val map = Map("hel", 2)
    println(anyMatch(map))
  }
}

