package com.hochoy.sparktest.spark.job.accumulator

import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by Cobub on 2018/6/1.
  * https://blog.csdn.net/baolibin528/article/details/54406049
  */
object AccumulatorDemo {

  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("Spark-Accumulator").setMaster("local")
    val sc = new SparkContext(sparkConf)
    val acc = sc.longAccumulator("accumulator")
    val sum = sc.parallelize(Array(1,2,3,4,5,6,7,8,9),2).filter(x=>{
      if (x % 2 !=0) acc.add(1l)
      x % 2==0
    }).reduce(_+_)

    println("sum: "+sum)
    println("accum:   "+acc.value)

    println("==========================")
    val a = sc.longAccumulator("longAcc")
    val numRdd = sc.parallelize(Array(1,2,3,4,5,6,7,8,9),3).map(x=>{
      a.add(1L)
      x+1
    })

    /**
      * 执行完毕，打印的值是 0，因为累加器不会改变spark的lazy的计算模型，
      * 即在打印的时候像map这样的transformation还没有真正的执行，
      * 从而累加器的值也就不会更新。
      */
    println(".... a:   "+a.value)
    numRdd.collect()/*.foreach(x=>print(x+ "    "))*/

    /**
      * 打印的值是 9
      */
    println("-----------  a:   "+a.value)

    numRdd.filter(x=>{
      x % 2 == 0
    }).collect()/*.foreach(x=>println("filter ....  "+x + "   "))*/

    /**
      * 打印的值是 18
      * 虽然只在map里进行了累加器加1的操作，但是两次得到的累加器的值却不一样
      * 这是由于 numRdd 的 两次collect()操作，触发了两次作业的提交，
      * 所以map算子实际上被执行了了两次，
      * 在reduce操作提交作业后累加器又完成了一轮计数，所以最终累加器的值为18。
      *
      * 究其原因是因为count虽然促使numberRDD被计出来，但是由于没有对其进行缓存，
      * 所以下次再次需要使用numberRDD这个数据集是，还需要从并行化数据集的部分开始执行计算。
      */
    println("-----------  a:   "+a.value)




  }
}
