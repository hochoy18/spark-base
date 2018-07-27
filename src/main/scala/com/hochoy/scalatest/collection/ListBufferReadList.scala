package com.hochoy.scalatest.collection

/**
  * Created by Hochoy on 2018/7/27.
  */


case object ListOp {

  def incrementWithFor(list: List[Int]): List[Int] = {
    var result = List[Int]()
    for (ele <- list) {
      result = ele+1 :: result
    }
    result.reverse
  }

  def increment(list: List[Int]): List[Int] = list match {
    case List() => List()
    case head :: tail => head + 1 :: increment(tail)
  }

  def increment_MoreEffective(list: List[Int]): List[Int] = {
    var result = List[Int]()
    for (ele <- list) {
      result = result ::: List(ele + 1)
    }
    result
  }

  def incrementWithListBuffer(list: List[Int]): List[Int] = {
    import collection.mutable.ListBuffer
    var listBuffer = new ListBuffer[Int]

    for (ele <- list) listBuffer.+=(ele + 1)
    listBuffer.toList

  }
}


object ListBufferReadList extends App {

  val list = List.range(1, 50)
  var starttime = System.currentTimeMillis()
  println(ListOp.increment(list))
  var stoptime = System.currentTimeMillis()
  println("increment takes " + (stoptime - starttime))

  starttime = System.currentTimeMillis()
  println(ListOp.increment_MoreEffective(list))
  stoptime = System.currentTimeMillis()
  println("increment_MoreEffective takes " + (stoptime - starttime))

  starttime = System.currentTimeMillis()
  println(ListOp.incrementWithListBuffer(list))
  stoptime = System.currentTimeMillis()
  println("incrementWithListBuffer takes " + (stoptime - starttime))

  starttime = System.currentTimeMillis()
  println(ListOp.incrementWithFor(list))
  stoptime = System.currentTimeMillis()
  println("incrementWithFor takes " + (stoptime - starttime))

}
