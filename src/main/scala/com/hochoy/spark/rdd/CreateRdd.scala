package com.hochoy.spark.rdd

import java.io.File
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.SparkUtils._
import org.apache.spark.{Partitioner, SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

import scala.util.Random

/**
  * Created by hochoy on 2018/9/15.
  */
object CreateRdd extends App {


  private val createRdd = new CreateRdd()
//  createRdd.test
  createRdd.testConfig

}
class CreateRdd{

  def test:Unit = {

    val sc = createSparkContext("createRdd")

    /**
      * 设置executor的日志级别
      */
    sc.setLogLevel("WARN")
    val rdd = sc.textFile(USER_SPARK_PATH+"rdd/README.md")

    val rdd1 = rdd.flatMap(x=>{x.split(" ")}).map((_,1))
    rdd1.foreach(x=>println(x._1,x._2))
    val rdd2 = sc.parallelize(List("hello","world","good","morning"))
    rdd2.map(x=>(x,1)).foreach(y=>println(y._1,y._2))

    val rdd3 = sc.makeRDD(List("hello","world","good","morning"))
    rdd3.map((_,3)).foreach(x=>(print(x._1,x._2)))

    val rdd4 = sc.makeRDD(List(1,2,3,4,5,6,7))
    println(rdd4.map(Math.pow(_,2)).collect().mkString(","))
  }

  def testConfig :Unit= {
    val conf = new SparkConf().setAppName("createRdd")
    conf.setMaster(s"local[2]")
    conf.set("spark.default.parallelism", "2")

    conf.set("spark.driver.cores", "2")
    conf.set("spark.driver.memory", "2g")
    conf.set("spark.executor.memory", "2g")

    conf.set("spark.memory.offHeap.enabled", "true")
    conf.set("spark.memory.offHeap.size", (1024 * 1024 * 10).toString)
    conf.set("spark.scheduler.mode", "FAIR")

    val fairScheduler = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"+ File.separator + "resources" + File.separator +"fairscheduler.xml"
    conf.set("spark.scheduler.allocation.file", fairScheduler)

    val sc = new SparkContext(conf)
    sc.setLocalProperty("spark.scheduler.pool", "production")

    /**
      * 设置executor的日志级别
      */
    //  sc.setLogLevel("WARN")

    val file1 = "file:///D:\\hello*.txt"
    val file2 = "file:///D:\\kkkk.txt"

     val value: RDD[(Int, String, Iterable[Int])] = sc.textFile(file1).persist(StorageLevel.MEMORY_ONLY)
      .flatMap(_.split(Pattern.compile("\\s+").pattern()))
      .map((_, 1))
      .groupByKey
      .mapPartitionsWithIndex((i, p) => {
        p.map(e => {
          println(s">>>>>>${i} >> ${e._1} >>${e._2.sum} ")
          (i, e._1, e._2)
        })
      }).persist(StorageLevel.OFF_HEAP)
    val rdd0 = value.map(e => (e._1, e._2, e._3.sum))
    rdd0.foreach(println)
    println(" --------------------------------")
    rdd0.collect().foreach(println)

    println(" --------------------------------")
    sc.makeRDD(Array(("A", "a1"), ("C", "c1"), ("D", "d1"), ("F", "f1"), ("F", "f2")), 2).countByKey().foreach(println)

//    Thread.sleep(10 * 1000)
//
//    value.unpersist()

    Thread.sleep(3000 * 1000)
  }
}
class ExamplePartitioner( num :Int) extends Partitioner{
  override def numPartitions: Int = num

  def getPartition(key: Any): Int = key match {
    case null => 0
    case _ => nonNegativeMod(key.hashCode + Random.nextInt(num), numPartitions)
  }

  def nonNegativeMod(x: Int, mod: Int): Int = {
    val rawMod = x % mod
    rawMod + (if (rawMod < 0) mod else 0)
  }
}
object sample{
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("createRdd")
    conf.setMaster(s"local[2]")
    conf.set("spark.default.parallelism", "2")

    val sc = new SparkContext(conf)
    sc.setLogLevel("ALL")
    val file1 = "file:///D:\\hello1.txt"

    val rdd1: RDD[String] = sc.textFile(file1).persist(StorageLevel.MEMORY_ONLY)
      .flatMap(_.split(Pattern.compile("\\s+").pattern()))

    val sampleRdd: RDD[String] = rdd1.sample(true,0.1)
    val count = sampleRdd.count()
    val sample: Array[(String, Double)] = sampleRdd.map((_, 1)).reduceByKey(_ + _).map(e => (e._1, e._2.toDouble / count.toDouble))
      .map(e =>e.swap).sortByKey(ascending = true).map(e => e.swap).top(10)
    sample.foreach(e=>println(e._1,e._2.toString))

    TimeUnit.SECONDS.sleep(30)


  }
}