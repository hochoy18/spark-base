package com.hochoy.scalatest.basic.implicitetest2

import java.awt.Point

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月13日 17:22
  * @version :  V1.0
  */
class Exercise21 {
}

class Fraction(val n: Int, val m: Int) {

  def *(that: Fraction): Fraction = {
    new Fraction(this.n * that.n, this.m * that.m)
  }

  override def toString = s"Fraction($n, $m)"

}

object Fraction {
  def apply(n: Int, m: Int) = new Fraction(n, m)
}


object exercise21_5 {
  //https://github.com/junyalu/scala-hello/blob/master/src/charpter21.scala
  def smaller(a: Fraction, b: Fraction)
             (implicit ordered: Fraction => Ordered[Fraction]): Fraction = if (ordered(a) < b) a else b

  implicit val sod: Fraction => Ordered[Fraction] = (f: Fraction) => new Ordered[Fraction] {
    override def compare(t: Fraction) =
      if (f.n / f.m < t.n / t.m) -1
      else if (f.n / f.m > t.n / t.m) 1
      else if (f.n % f.m != 0 || t.n % t.m != 0) {
        if (f.n % f.m < t.n % t.m) -1
        else if (f.n % f.m > t.n % t.m) 1
        else 0
      }
      else 0
  }
}

object exercise21_6_7 {

  class PointOrder6(point: Point) extends java.awt.Point with Ordered[java.awt.Point] {
    override def compare(that: Point): Int = {
      if (this.x > that.x || (this.x == that.x && this.y > that.y)) 1
      else if (this.x == that.x && this.y == that.y) 0
      else -1
    }
  }

  class PointOrder7(point: Point) extends Point with Ordered[Point] {
    override def compare(that: Point): Int = {
      val thisLen = this.x * this.x + this.y * this.y
      val thatLen = that.x * that.x + that.y * that.y
      if (thisLen > thatLen) 1
      else if (thisLen == thatLen) 0
      else 0
    }
  }

  object PointOrder {
    implicit val point6: Point => PointOrder6 = (point: Point) => new PointOrder6(point)
    implicit val point7: Point => PointOrder7 = (point: Point) => new PointOrder7(point)
  }


  def main(args: Array[String]) {
    import PointOrder.point7
    println(new Point(2, 3) < new Point(1, 9))
  }

  type d = Int =:= AnyVal

  def getFirstChar[T](arg: T)(implicit ev: T =:= String) = ev(arg).head
}



