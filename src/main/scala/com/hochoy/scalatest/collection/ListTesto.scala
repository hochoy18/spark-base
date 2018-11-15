package com.hochoy.scalatest.collection

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月15日 15:25
  * @version :  V1.0
  */
object ListTestO {

  //http://www.importnew.com/3673.html#drop
  def main(args: Array[String]) {
    val list = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val p = list.partition(_ % 2 == 0)
    println(p)

    val find = list.find(_ > 6)
    println(find.get)
    println(List("a", "fasd", "adfk d", "ass").find(_.length > 4).get)

    //dropWhile移除前几个匹配断言函数的元素。例如，如果我们从 list 列表里dropWhile奇数的话，1会被移除（3则不会，因为它被2所“保护”）。
    val l = list.dropWhile( _ % 2 != 0)
    println(l)
    println(list.drop(4))
    println(list)

    val v = list.fold(0)((m,n)=>m+n)
    println(s"sum is $v")

    val b = list.foldLeft(0)(_ min _)
    println(s"min is $b")
    println(s"min is ${list.min}")
    //返回不可变数字集合中数字元素的积
    println(s"list.product is ${list.product}")
    println(s"list fact is ${1*2*3*4*5*6*7*8*9}")
//    println(s"list product is  ${List("123","1234","111",12,213).product}")
  }

}