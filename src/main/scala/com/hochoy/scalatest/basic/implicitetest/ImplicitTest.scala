package com.hochoy.scalatest.basic.implicitetest

;

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月07日 10:11
  * @version :  V1.0
  */
object ImplicitTest {

  implicit def float2Int(x: Float): Int = x.toInt

  //implicit def float2Int2(x: Float): Int = x.toInt


  def main(args: Array[String]) {
    /**
      * 如果没有上面的implicit def  float2Int方法，以下定义会报错:类型不匹配
      *
      * 1） 隐式转换函数的函数名可以是任意的，与函数名称无关，只与函数签名（函数参数和返回值类型）有关。
      *
      * 2）如果当前作用域中存在函数签名相同但函数名称不同的两个隐式转换函数，则在进行隐式转换时会报错。
      * 如果把上面的float2Int2方法的注释放开，也会同样报错
      */
    val x: Int = 2.22f
    print(x)
  }

}

/**
  * implicit modifier cannot be used for top-level objects
  * 隐式修饰符不能用于顶级对象
  * 不能使用以下形式定义隐式类
  */
//implicit class Dog(val name:String){
//  def bark = println(s"$name is barking")
//}
//implicit object Dog {
//  def bark = println(s"$name is barking")
//}
object ImplicitTest2 {

  implicit class Dog(val name: String) {
    def bark = println(s"$name is barking")
  }

  /**
    * implicit class must have a primary constructor with exactly one argument in first parameter list
    * 隐式类必须有一个主构造函数，在第一个参数列表中只有一个参数
    * 之所以只能有一个参数，是因为隐式转换是将一种类型转换为另外一种类型，源类型与目标类型是一一对应的
    */
  //  implicit class Dog2(val name:String,val age:Int ){
  //    def bark=println(s"$name is $age years old")
  //  }

  def main(args: Array[String]) {
    "er ha ...".bark
  }

}

object ImplicitTest3 {

  trait Multiplicable[T] {
    def multply(x: T): T
  }

  /**
    * 隐式对象
    */
  implicit object MultiplicableInt extends Multiplicable[Int] {
    override def multply(x: Int): Int = x * x
  }

  /**
    * 隐式对象
    */
  implicit object MultiplicableString extends Multiplicable[String] {
    override def multply(x: String): String = x * 3
  }

  def multiply[T: Multiplicable](x: T): T = {
    val ev = implicitly[Multiplicable[T]]
    ev.multply(x)
  }

  /**
    * 隐式参数 ,在函数的定义时，在参数前添加implicit关键字。
    *
    * @param x
    * @param ev
    * @tparam T
    * @return
    */
  def multply1[T: Multiplicable](x: T)(implicit ev: Multiplicable[T]): T = {
    ev.multply(x)
  }

  def main(args: Array[String]) {

    println(multiply(5))

    println(multiply("333"))
    import ImplicitTest.float2Int
    val x: Int = 4.5f
    println(multiply(x))

    println(multply1("fsafsdfa "))
    /**
      * 隐式值
      */
    implicit val vv: Double = 6

    /**
      * 同类型的隐式值只能在作用域内出现一次，即不能在同一个作用域中定义多个相同类型的隐式值
      */
    //implicit val vv1: Double = 8
    println(sqrt)

    /**
      * 调用定义的sqrt函数，它将自行调用定义好的隐式值
      */
    println(sqrt(3, 4))
  }

  /**
    * https://blog.csdn.net/m0_37138008/article/details/78120210
    * 1）当函数没有柯里化时，implicit关键字会作用于函数列表中的的所有参数,
    * 且implicit 关键字只能放在第一个参数前面（不能同时放在多个参数前或
    * 只放在除第一个参数外的参数前），调用该隐式函数时，不需要传递参数
    * 或传递全部参数，即
    * 2) 隐式参数使用时要么全部不指定，要么全不指定，不能只指定部分。
    * 3) 同类型的隐式值只能在作用域内出现一次，即不能在同一个作用域中定义多个相同类型的隐式值
    * 4) 在指定隐式参数时，implicit 关键字只能出现在参数开头。
    * 5) 如果想要实现参数的部分隐式参数，只能使用函数的柯里化，
    * 如要实现这种形式的函数，def test(x:Int, implicit  y: Double)的形式，必须使用柯里化实现：def test(x: Int)(implicit y: Double).
    * 6) 柯里化的函数， implicit 关键字只能作用于最后一个参数。否则，不合法。
    *
    *
    * @param x
    * @param y
    * @return
    */

  def sqrt(implicit x: Double, y: Double): Double = {
    Math.sqrt(x * x + y * y)
  }

  /**
    * 7) implicit 关键字在隐式参数中只能出现一次，柯里化的函数也不例外！
    *
    */
//      def product(implicit x: Double)(implicit y: Double) = x * y

  /**
    *  8）匿名函数不能使用隐式参数
    */
  //  val product = (implicit x:Double,y:Double)=>x * y
}
