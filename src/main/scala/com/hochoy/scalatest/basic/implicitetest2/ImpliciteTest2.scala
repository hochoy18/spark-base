package com.hochoy.scalatest.basic.implicitetest2


import com.hochoy.scalatest.basic.implicitetest2.implicitConvert.Percent

import scala.io.Source
import scala.math.Fractional
;

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月13日 14:40
  * @version :  V1.0
  */
object implicitConvert {
  implicit def file2RichFile2(filePath: String) = new RichFile2(filePath)

  implicit val delimiter: Delimiters = new Delimiters("{", "}")

  case class Percent(val p: Int)

  implicit def +%(x: Percent)(implicit y: Int): String = (x.p + y).toString + "%"

  implicit def factInt(i: BigInt) = new RichMultiPly(i)
}

case class RichMultiPly(val i: BigInt) {

  def !(): BigInt = {
    if (i == 1) 1
    else i * (RichMultiPly(i - 1) !)
  }
}

//object FactorialFunc {
//  implicit def fact(i: Int) = new RichMultiPly(i)
//}

object ImpliciteTest2 extends App {

  import implicitConvert.file2RichFile2

  val path = System.getProperty("user.dir")
  val file = path + "\\src\\main\\scala\\com\\hochoy\\scalatest\\basic\\implicitetest2\\ImpliciteTest2.scala"
  println("file content is : \n" + file.read)

  implicit def int2Fraction(x: Int) = Fractional.Implicits

}

class RichFile2(val from: String) {
  def read = Source.fromFile(from).mkString

}

case class Delimiters(left: String, right: String)

object DelimitersMain {
  def quote(what: String)(implicit delimiter: Delimiters) = delimiter.left + what + delimiter.right

  def main(args: Array[String]) {
    import implicitConvert.delimiter
    println(quote(" hello world ")(new Delimiters("<<", ">>")))
    println(quote(" ni hao a boy  "))
  }
}

object implParam2implConvert extends App {
  def smaller[T](x: T, y: T)(implicit ordered: T => Ordered[T]) = {
    if (x < y) x else y
  }

  println(smaller(2000, 1222))
  println(smaller("y100", "x2010"))
}

object exercise extends App {

  //21.2
  import implicitConvert.+%

  implicit val y: Int = 12
  val x: String = new Percent(100)
  println(x)


  // 21.3
  import implicitConvert.factInt

  val i: BigInt = 30
  val fact: BigInt = i !

  println(fact)

}