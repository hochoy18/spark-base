package com.hochoy.scalatest.basic

import scala.util.Random

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
    case l: List[String] => {
      println(s"it's type is List[String], and size is  ${l.size}")
      List.getClass
    }
    case mt: MatchType => {
      println(s"it's type is MatchType,")
    }
    case _: Any => 0
  }

  //  def matchType(o:Any) = o match {
  //    case
  //  }

  def main(args: Array[String]) {
    val t = judgeGrade('8')
    println(t)
    judgeGrade1("hochoy", "D")
    //    varMatch("Pi")

    val map = Map("hel" -> 2)
    println(anyMatch(map))
    val list = List("haha", "hoho", "soso")
    println(anyMatch(list))
    val mt = new MatchType
    println(anyMatch(mt))

  }
}

class MatchType {}

/**
  * 匹配数组、元祖、集合
  */
object MatchList {
  def main(args: Array[String]) {

    val list1 = List(0, 1, 2, 3, 4)
    list1 match {
      case 0 :: Nil => println(s"case1 : 0")
      case a :: b :: c :: d :: e :: Nil => println(s"case2 : $a, $b, $c ,$d ")
      case a :: b :: c :: d => println(s"case2 : $a, $b, $c ,$d ")
      case _ => println("NONE...")
    }

    val arr = Array(3, 2, 5, 7)
    arr match {
      case Array(4, a, b, c) => {
        println(println(s"case $a , $b,  $c"))
      }
      case Array(_, x, y, z) => {
        println(println(s"case $x , $y,  $z"))
      }
      case _ => {
        println("Not matched ")
      }
    }

    val tup = (2, 3, 4)
    tup match {
      case (4, _, _) => println(s"the first is ${tup._1}")
      case (_, 3, 2) => println(s"the second is ${tup._2}")
      case (_, _, 2) => println(s"the third is ${tup._3}")
      case (_, _, _) => println("No matched")
    }

  }
}

object CaseClassDemo {
  def main(args: Array[String]) {
    val arr = Array(CheckTimeOutTask, SubmitTask("1000", "200"),
      HeartBeat(100l))
    arr(Random.nextInt(arr.length)) match {
      case CheckTimeOutTask => println("CheckTimeOutTask")
      case SubmitTask(port, task) => println("SubmitTask")
      case HeartBeat(time) => println("HeartBeat ")
    }
  }
}

case class HeartBeat(time: Long)

case class SubmitTask(id: String, taskName: String)

case object CheckTimeOutTask


object PartialFunctionDemo {
  def m1: PartialFunction[String, Int] = {
    case "one" => {
      println("case 1 ")
      1
    }
    case "two" => {
      println("case 2")
      2
    }
  }

  def m2(num: String): Int = num match {
    case "one" => {
      println("case 1")
      1
    }
    case "two" => {
      println("case 2")
      2
    }
    case _ => {
      println("other .")
      0
    }
  }

  def main(args: Array[String]) {
    println(m1("two"))
    println(m2("one"))
  }
}


