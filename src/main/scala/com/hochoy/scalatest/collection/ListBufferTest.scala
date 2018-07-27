package com.hochoy.scalatest.collection

/**
  * Created by Hochoy on 2018/7/26.
  */
object ListBufferTest {


  def listbuffer1() {

    import collection.mutable.ListBuffer
    val buffer = ListBuffer


  }

}


object ListTest {

  def main(args: Array[String]) {
        list1
    println("-----------------------------------------------------------------------------------------------")
    list2
  }

  def list2() {

    val twoThree = List(2, 3)
    //::是右操作数，列表twoThree的方法  添加单个元素

    val oneTwoThree = 1 :: twoThree
    println("添加单个元素，源list不变........." + oneTwoThree)
    println("源list：  twoThree ......."+twoThree)
    val one = List('A', 'B')
    val two = List('C', 'd')
    val one_two = one ::: two
    println("添加一个list，源list不变........" + one_two)
    println("源list：  one .......      "+one +"two......"+two)

    val l = List(1,'2',"232","fads",3.1415926)

    println("List元素可包含不同数据类型，其类型为List[AnyRef]..............."+l)
    println("l  type ......"+l.getClass)

  }

  def list1() {

    val list = List(1, 2, 3, 4, 5)
    println(list)

    val listStr: List[Object] = List("this ", "is ", "Covariant", "example")
    //     listStr = list()
    println(listStr)

    println("count........"+listStr.count(x=>{x.toString.length >4}))
    println("exit......."+listStr.exists(s=>{s.toString.equals("this ")}))

    val nums = 1 :: (2 :: (3 :: (4 :: Nil)))
    val nums1 = 1 :: 2 :: 3 :: 4 :: Nil
    println("判断两个list是否相等。。。。。。。。。。 。。。。。。。  "+nums.equals(nums1))

    println("nums's head:   " + nums.head)
    println("nums's head of tail  " + nums.tail.head)

    println("nums's tail  " + nums.tail)
    println("====================================================")

    val list1 = List(1, 2, 3) ::: List(4, 5, 6)
    println("list1........" + list1)

    val drop = list1.drop(2)
    println("drop......." + drop)

    val take = list1.take(4)
    println("take........" + take)

    val split = list1.splitAt(4)
    println("split....." + split)


    val init = list1.init
    println("init.....了最后一个元素之外的所有元素........."+init)
  }


}
