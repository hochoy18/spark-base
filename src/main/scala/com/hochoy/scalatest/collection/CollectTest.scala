package com.hochoy.scalatest.collection

import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by Cobub on 2018/10/1.
  */
object ListTest1 {

  def main(args: Array[String]) {
    val l = List(1,3,4,2,6,8,2,5,8,6)


    println(l.sorted)
    val l2 = List("fdad","323","daf","23dw")

    println(l2.sorted)
    println(l2.reverse)
    println(l.grouped(3).toBuffer)
    val l1 = List(List(2,21,312,21,33),List(4,5,455,654,44),List(798,87,7,89))
    println("ListTest...."+l1.flatten)


    println("reverse.....s       "+l1.reverse)

  }

}

object ArrarTest1{
  def main(args: Array[String]) {
    val arr = 1 to 5 //Array(1,2,3,4,5,6,7,8,9)

    println(arr.foldRight(1)(_-_))

    exit(1)
    println(arr.par.fold(1000000)(_+_))
    println(arr.par.fold(1000000)(_+_))
    println(arr.par.fold(1000000)(_+_))
    println(arr.reduce(_+_))
    exit()
    val t1 = System.currentTimeMillis()
    println(+arr.sum+"....")
    println("t1:   %l",System.currentTimeMillis() - t1)

    val t2 = System.currentTimeMillis()
    println(arr.par.sum+"....")
    println("t2:   %l",System.currentTimeMillis()-t2)



  }



}

class Foo1(val name:String,val age:Int, val sex:Symbol)
object Foo1{
  def apply(name:String,age:Int,sex:Symbol) =new Foo1(name,age,sex)
}
object CollectTest{
  def main(args: Array[String]) {
    val fl = Foo1("AAAA aaa",21,'male ) ::
      Foo1("BBBB bbb",20,'female)::
      Foo1("CCCC ccc",18,'male)::
      Nil

    /**
      * Scala:fold,foldLeft和foldRight区别与联系
      * https://www.iteblog.com/archives/1228.html
      */
    val strList = fl.foldLeft(List[String]())({(z,f)=>
      val title =f.sex match {
        case 'male => "Mr."
        case 'female =>"Ms."
      }
      z:+s"${title}${f.name}:${f.age}"
    })
    println(strList)
  }

  val l = 1 to(3)
  println("foldLeft...."+(100/:l)(_-_))
  println("foldRight..."+(l:\100)(_-_))
}

object aggregate{
  /**
    * 作用：一个比fold、reduce更加抽象的聚集操作，对集合分块，块中每一个元素执行一个操作，
    * 得到一个容器，在将这些容器进行聚集。
    * @param args
    */
  def main(args: Array[String]) {
    val l = List(List(1,2,3),List(2,3,4),List(3,4,5))
    val s = l.aggregate(1)(_+_.sum,_+_)
    println(s"sum =  ${s}_")

    val list = List(1,2,3,4,5,6,7,8,9)
    val sc = new SparkContext(new SparkConf().setMaster("local").setAppName("aggregate"))

    /**
      * spark 中
      *
      * def aggregate[U](zeroValue : U)(seqOp : scala.Function2[U, T, U], combOp : scala.Function2[U, U, U])
      *
      * aggregate先对每个分区的元素做聚集，然后对所有分区的结果做聚集，聚集过程中，使用的是给定的聚集函数以及初始值”zero value”。
      * 这个函数能返回一个与原始RDD不同的类型U，因此，需要一个合并RDD类型T到结果类型U的函数，还需要一个合并类型U的函数。
      * 这两个函数都可以修改和返回他们的第一个参数，而不是重新新建一个U类型的参数以避免重新分配内存。
      *
      * 参数zeroValue：seqOp运算符的每个分区的累积结果的初始值以及combOp运算符的不同分区的组合结果的初始值 - 这通常将是初始元素（例如“Nil”表的列表 连接或“0”表示求和）
      * 参数seqOp： 每个分区累积结果的聚集函数。
      * 参数cmbOp： 一个关联运算符用于组合不同分区的结果
      *
      */
    val (mul, sum, count) = sc.parallelize(list, 2).aggregate((1, 0, 0))(
      (acc, number) => (acc._1 * number, acc._2 + number, acc._3 + 1),
      (x, y) => (x._1 * y._1, x._2 + y._2, x._3 + y._3)
    )
    println(s"multiply is $mul , and average is $sum/$count")


    val raw = List("a","b","c","d","f","g","h","o","p","x","y")
    val (bigger,less) = sc.parallelize(raw,2).aggregate((0,0))(
      (cc , param)=> {
        var bigger = cc._1
        var less = cc._2
        if (param.compareTo("f") >= 0) bigger = bigger + 1
        else if (param.compareTo("f") < 0) less = less + 1
        (bigger,less)
      },
      (x,y)=>(x._1 + y._1,x._2 + y._2)
    )
    println(s"bigger is  $bigger  and less is $less")

  }
}