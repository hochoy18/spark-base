package com.hochoy.spark.rdd

import java.io.File
import java.util.regex.Pattern

import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.SparkUtils._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

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
