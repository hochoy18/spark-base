package com.hochoy.scalatest.collection

/**
  * Created by Cobub on 2018/7/24.
  */
object ArrayTest {

  def main(args: Array[String]) {

//    arrayTest1
//    arrayTest2
    arrayTest3
  }

  def arrayTest3() {
    val arr = Array(1 to 10 :_*)
    arr.foreach(x=>print(x + "   "))
    println(arr(1))
    arr(4)= 200
    arr.foreach(x=>print(x + "   "))
    val  a = Array(1,2,3)
    val  b = Array(9,7,8)
    val c = a ++ b ++ a
    println()

    c.foreach(x=> print(x +"   "))
    println(c.distinct.mkString(" , "))
    println()
    println(c.mkString("{{{","  >>>  ","}}}"))
    println()
println("max  "+c.max+"   ; sum  "+c.sum +"   ;  min "+c.min)
  }
  def arrayTest2(){
    var myList1 = Array(1.9,2.9,3.9,4.9)
    var myList2 = Array(0.01,0.03,0.04,0.002)
    val myList3 = Array.concat(myList1,myList2,myList1,myList2)
    myList3.foreach(println)
    println("....................")
    val myList4 = Array.range(10,30,3)
    val myList5 = Array.range(90,200,20)
    myList4.foreach(x=>print(x+" ,   "))
    myList5.foreach(x=>print(x+" ,   "))
  }
  def arrayTest1(): Unit ={
    val myList = Array(1.9,2.9,3.9,4.9,5.3)
    for(x<- myList){println(x)}
    myList.foreach(println)

    println("=========")
    myList(2)=3000
    myList(4)=200
    myList.foreach(println)
    var sum:Double =0
    myList.foreach(x=>{sum=sum+x})
    println("sum......"+sum)
    var max :Double= 0;
    myList.foreach(x=>{max=Math.max(max,x)})
    println("max......"+max)
  }
}


object ArrayBufferTest{
  def main(args: Array[String]) {

//    arrayBufferTest1
    arrayBufferTest2
  }

  def arrayBufferTest2(){
    import collection.mutable.ArrayBuffer
    var ab = ArrayBuffer[Int]()
    ab += 1
    ab +=(2,3,4,5)
    ab ++= ArrayBuffer(9,8,7)
    println(ab )
    ab.insert(2,3000)
    println(ab )

    ab.remove(1,2)
    println(ab )

    val a = ab.toArray
    println(a.mkString("(",",",")") )

    println(Array.isInstanceOf)
//    println(a.isInstanceOf[Array])
  }

  def arrayBufferTest1(){
    import collection.mutable.ArrayBuffer
    val b = ArrayBuffer[Int]()
    b += 1
    b += 3
    b.+=(5)
    b.+=(21)
    b.++= (Array(10,11,13,14))

    println(b)
  }
}
