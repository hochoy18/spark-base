package com.hochoy.sparktest.spark.job

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by hochoy on 2018/6/2.
  */
object PesdoOperation {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(List("tom", "tom", "tomas", "tomasLees"))
    val rdd2 = sc.parallelize(List("tomas", "tomasLees", "bob", "coco"))
    val dist = rdd1.distinct() //distince :去重

    for (elem <- dist.collect()) {
      print(elem + " , ")
    }
    println("distinct............") //tom , tomas , tomasLees , distinct............

    val union = rdd1.union(rdd2) // union：并集 操作
    for (elem <- union.collect()) {
      print(elem + " , ")
    }
    println("union............") //tom , tom , tomas , tomasLees , tomas , tomasLees , bob , coco , union............

    val intersection = rdd1.intersection(rdd2) //intersection：交集 操作
    for (elem <- intersection.collect()) {
      print(elem + " , ")
    }
    println("intersection............") //tomas , tomasLees , intersection............

    val substract = rdd1.subtract(rdd2) //subtract：差集 操作
    print(substract.collect().foreach(x => print(x + "   ")))
    println("subtract............") //tom   tom   ()subtract............

    rdd1.cartesian(rdd2).collect().foreach(x => print(x + "   ")) // cartesian:笛卡尔积
    println("cartesian............")
    //(tom,tomas)   (tom,tomasLees)   (tom,tomas)   (tom,tomasLees)   (tom,bob)   (tom,coco)   (tom,bob)   (tom,coco)   (tomas,tomas)   (tomas,tomasLees)   (tomasLees,tomas)   (tomasLees,tomasLees)   (tomas,bob)   (tomas,coco)   (tomasLees,bob)   (tomasLees,coco)   cartesian............

    rdd1.zip(rdd2).collect().foreach(x => print(x + "   ")) // zip操作
    println("zip............") //(tom,tomas)   (tom,tomasLees)   (tomas,bob)   (tomasLees,coco)   zip............

    rdd1.sample(false, 0.6).collect().foreach(x => print(x + "   ")) //simple 操作，以下三个输出结果均有可能不同
    println("sample............") //tom   tomasLees   sample............
    rdd1.sample(false, 0.6).collect().foreach(x => print(x + "   "))
    println("sample............") //tom   tom   tomasLees   sample............
    rdd1.sample(false, 0.6).collect().foreach(x => print(x + "   "))
    println("sample............") //tom   tom   sample............
  }

}

object sample {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
    val sc = new SparkContext(conf)
    val rdd1 = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8, 9))
    rdd1.sample(false, 0.6).collect().mkString(",").map(print) // 输出 2,3,6,8,9
    rdd1.sample(true, 0.6).collect().mkString(".").map(print) // 输出 2.4.4.6
  }
}

object fold {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName(this.getClass.getName)
    val sc = new SparkContext(conf)
    val rdd1 = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8, 9))
    print("----------------" + rdd1.fold(2)(_ + _))
    //parallelize
    //numSlices=2
    // 1+2+3+4+5+2 =17
    // 6+7+8+9+2=32
    // 17+32+2= 51

    //numSlices=1
    // 1+2+3+4+5+6+7+8+9+2 = 47
    // 47+2=49

  }
}

class Foo(val name: String, val age: Int, val sex: Symbol)

object Foo {
  def apply(name: String, age: Int, sex: Symbol) = new Foo(name, age, sex)
}

//object foldLeft{
//  def main(args: Array[String]) {
//    val conf = new SparkConf().setMaster("local").setAppName(this.getClass.getName)
//    val sc = new SparkContext(conf)
//    val fooList = Foo("Hugh Jass", 25, 'male) ::
//      Foo("Biggus Dickus", 43, 'male) ::
//      Foo("Incontinentia Buttocks", 37, 'female) ::
//      Nil
//    fooList.foldLeft(List[String]){(x,y)=>}
//    val rdd1 = sc.parallelize(fooList)
//
//
//  }
//
//}

object testMap {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName(this.getClass.getName)
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(List(1,2,3,4,5,6))
    val r1 = rdd.map(x =>x*x)
    val r2 = r1.collect().mkString(" , ")
    r2.map(println)
  }
}
