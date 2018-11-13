package com.hochoy.scalatest.basic.implicitetest2

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