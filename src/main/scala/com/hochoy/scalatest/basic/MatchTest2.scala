package com.hochoy.scalatest.basic

/** Describe:
  * Created by IntelliJ IDEA.
  * Time: 17:12
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
object MatchTest2 {



  def mapArray(arr:Any): Unit ={
    arr match {
      case Array(0) ⇒ println(0,"  匹配只有0元素 的数组")  // Array(0)
      case Array(x,y) ⇒ println("Array(_, _) ,  两个元素的数组(即 元素个数为2)，并绑定值到x，y")
      //Array((0,1),(0,1)), Array(1,2)
      case Array((x,y),_*) ⇒ println("匹配元组数组")//Array((0,1),(0,1),(0,1))
      case Array(0,_*) ⇒println("0,........ , 0 开始的数组")
      case _ ⇒ println("none")
    }
  }
  def mapTuple(t:Any):Unit={
    t match {
      case (1,2)⇒ println("匹配元素为(1,2)的元组")
      case c:(_,_) ⇒ println(s"匹配有两个元素的元组${c._1}   ${c._2}")
      case (_,_,_) ⇒ println("匹配有三个元素的元组")
      case _ ⇒ print("other")
    }
  }
  def mapMap(m:Any):Unit={
    /**
      * 匹配发生在运行期，java虚拟机中泛型的类型信息是被擦掉的，因此，你不能用
      * 类型来匹配特定的Map类型
      * case v: Map[String,Int] => ... // 不能这样匹配
      * 但可以匹配一个通用的映射：
      * case c:Map[_,_]⇒println("Map[_,_]") // ok
      */
    m match {
      case c:Map[_,_]⇒println("match succ .....   Map[_,_]")
      case _⇒println("other")
    }
  }
  def mapList(l:Any):Unit={
    l match {
      case 0:: Nil ⇒ println("match the list of List(0)")
      case _::_:: Nil⇒println("match the list of List.size() = 2")
      case 0:: tail⇒println("match the list of its first element is 0")
      case head::0::tail ⇒println("match the list of its mid-element contains 0 ")
      case _ ⇒ println("other")

    }
  }
  def mapRegex(s:String):Unit={
    val str_num = "([a-z]+) ([0-9]+)".r
    val num_str = "([0-9]+) ([a-zA-Z]+)".r
    s match {
      case str_num(item,num)⇒println(s"str_num .....:  $item   $num")
      case num_str(num,item)⇒println(s"num_str .....:  $num   $item .....")
      case _⇒println("other")
    }
  }

  abstract class Account
  case class Dollar(value:Double) extends Account
  case class Currency(value:Double,unit:String) extends Account
  def mapCaseClass(cc:Any): Unit ={
    cc match {
      case Dollar(v)⇒println(s"Dollar value is $v")
      case Currency(x,y)⇒println(s"Currency  value : $x, unit : $y")
      case _⇒println("other")
    }
  }



}
