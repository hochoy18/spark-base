package com.hochoy.sparktest.spark.job.pairrdd

import org.apache.spark.{Partitioner, SparkContext, SparkConf}

/**
  * Created by Cobub on 2018/6/21.
  */
object PairRDDTest {

  val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local")

  val sc = new SparkContext(conf)

  def main(args: Array[String]) {
    //    create()
    //    combineByKey1()
    //    reducebykey
    groupby1
  }

  def groupbykey(): Unit = {
    val rdd = sc.parallelize(Array(("tom", 12), ("tomas", 10), ("july", 18), ("marry", 18), ("mark", 12)))
    rdd.collect()
    val g = rdd.groupByKey()
    val re = rdd.map(x => (x._2, x._1))
    re.collect().foreach(x => print(x._1 + " : " + x._2 + "    ，      "))
    val g1 = re.groupByKey()
    g1.collect().foreach(x => print(x._1 + " : " + x._2 + "     ，     "))

  }

  def groupby() {
    val rdd = sc.parallelize(1 to 20)
    rdd.collect()
    val r = rdd.groupBy(x => {
      if (x % 2 == 0) "even"; else "odd"
    })
    //groupBy算子接收一个函数，这个函数返回的值作为key，然后通过这个key来对里面的元素进行分组

    r.collect(
    )
    r.collect().foreach(x => println(x))

  }

  def create() {
    val lines = sc.textFile("E:\\work\\sparktest\\src\\main\\scala\\com\\hochoy\\sparktest\\spark\\job\\pairrdd\\pair.txt")

    val words = lines.flatMap(x => x.split("\\s+"))
    val pairRdd = words.map(x => (x, 1))
    pairRdd.collect().foreach(x => print(x + "   "))
    val rbk = pairRdd.reduceByKey(_ + _)

    rbk.collect().foreach(x => print(x + "   "))

    val flatRDD = lines.flatMap(x => x.split("\\s+"))
    val pairs = flatRDD.map(x => (x, 1))
    pairs.collect().foreach(print)
    pairs.reduceByKey(_ + _).collect.foreach(x => print(x + "   "))

    pairs.groupByKey().collect.foreach(x => print("groupByyKey   " + x + "   "))

    pairs.mapValues(x => x + 0.25).collect.foreach(x => print(x + "*****"))
    pairs.mapValues(x => x + 0.25).reduceByKey(_ + _).collect.foreach(x => print(x + "....."))
    pairs.reduceByKey(_ + _).flatMapValues(x => (x to 4)).collect.foreach(x => print(x + "^^^^^^^"))

    pairs.reduceByKey(_ + _).sortByKey().collect().foreach(x => print(x + "sssssssss"))

  }

  def combineByKey(): Unit = {
    val people = List(("male", "zhangsan"), ("female", "lisisi"), ("male", "lisi"), ("female", "yangmi"), ("male", "mayun"), ("female", "liuyifei"), ("female", "songqian"));
    val rdd = sc.parallelize(people, 2)
    val result = rdd.combineByKey(
      (x: String) => (List(x), 1), //createCombiner
      (peo: (List[String], Int), x: String) => (x :: peo._1, peo._2 + 1), //mergeValue
      (sex1: (List[String], Int), sex2: (List[String], Int)) => (sex1._1 ::: sex2._1, sex1._2 + sex2._2)) //mergeCombiners
    result.foreach(println)
  }


  def combineByKey1() {
    //https://blog.csdn.net/jiangpeng59/article/details/52538254
    val initialScores = Array(("Fred", 88.0), ("Fred", 95.0), ("Fred", 91.0), ("Wilma", 93.0), ("Wilma", 95.0), ("Wilma", 98.0))
    val rdd = sc.parallelize(initialScores, 5)

    type MVType = (Int, Double)
    val cbk = rdd.combineByKey(
      score => (1, score),
      (c1: MVType, newScore) => (c1._1 + 1, c1._2 + newScore),
      (c1: MVType, c2: MVType) => (c1._1 + c2._1, c1._2 + c2._2)
    )
    cbk.collect.foreach(x => print(x + "......"))
    cbk.map {
      case (name, (num, score)) => (name, score / num)
    }.collect.map(println)

  }

  def reducebykey(): Unit = {
    val rdd = sc.textFile("E:\\work\\sparktest\\src\\main\\scala\\com\\hochoy\\sparktest\\spark\\job\\pairrdd\\pair.txt")
    val r1 = rdd.flatMap(x => x.split(" "))
    val r2 = r1.map(x => (x, 1))
    r2.collect()
    println("2222222222222222222222222222")
    r2.reduceByKey((x, y) => {
      val s = x + y
      println("...................." + Thread.currentThread().getName + ":   " + " x:   " + x + "    y :   " + y + "   =   x+y=" + s);
      s
    }).collect()
    println("33333333333333333333333333333")
  }

  def groupby1(): Unit ={
    val a = sc.parallelize(1 to 9, 3)
    val p = new MyPartitioner
    val b = a.groupBy((x: Int) => {
      x
    }, p)
//    val c = b.mapWith(i=>i)((a,b)=>(b,a))
//    c.collect().foreach(x=>println(x))
  }
  class MyPartitioner extends Partitioner {
    def numPartitions: Int = 3

    @Override
    def getPartition(key: Any): Int = {
      key match {
        case null => 0
        case key: Int => key % numPartitions
        case _ => key.hashCode() % numPartitions
      }
    }

    override def equals(other: Any): Boolean = {
      other match {
        case h: MyPartitioner => true
        case _ => false
      }
    }
  }

}
