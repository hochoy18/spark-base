package com.hochoy.spark.rdd

import com.hochoy.spark.utils.SparkUtils.createSparkContext
import com.hochoy.spark.utils.Constants._
import org.apache.spark.HashPartitioner
import org.apache.spark.rdd.{CoGroupedRDD, RDD}


object AssociationRDD {

  val sc = createSparkContext("AssociationRDD")

  var rdd1 = sc.makeRDD(Array(("A","a1"),("C","c1"),("D","d1"),("F","f1"),("F","f2")),2)
  var rdd2 = sc.makeRDD(Array(("A","a2"),("C","c2"),("C","c3"),("E","e1")),2)

  def main(args: Array[String]): Unit = {
//    join
//    leftOuterJoin
//    rightOuterJoin
//    fullOuterJoin
    joinTest
  }

  def join:Unit={
    val r = rdd1.join(rdd2)
    val c = r.collect()
    println(c.mkString(" == "))
    // (A,(a1,a2)) == (C,(c1,c2)) == (C,(c1,c3))
  }
  def leftOuterJoin :Unit = {
    val r = rdd1.leftOuterJoin(rdd2)
    val c = r.collect()
    println(c.mkString(" == "))
    //(F,(f1,None)) == (F,(f2,None)) == (D,(d1,None)) == (A,(a1,Some(a2))) == (C,(c1,Some(c2))) == (C,(c1,Some(c3)))
  }
  def rightOuterJoin :Unit ={
    val r = rdd1.rightOuterJoin(rdd2)
    val c = r.collect()
    println(c.mkString(" == "))
    // (A,(Some(a1),a2)) == (C,(Some(c1),c2)) == (C,(Some(c1),c3)) == (E,(None,e1))
  }
  def fullOuterJoin:Unit ={
    val r =  rdd1.fullOuterJoin(rdd2)
    val c = r.collect()
    println(c.mkString(" == "))
    //(F,(Some(f1),None)) == (F,(Some(f2),None)) == (D,(Some(d1),None)) == (A,(Some(a1),Some(a2))) == (C,(Some(c1),Some(c2))) == (C,(Some(c1),Some(c3))) == (E,(None,Some(e1)))
    val r1 = rdd1.cogroup(rdd2)
    val c1 = r1.collect()
    println(c1.mkString(" == "))
    //(F,(CompactBuffer(f1, f2),CompactBuffer())) == (D,(CompactBuffer(d1),CompactBuffer())) == (A,(CompactBuffer(a1),CompactBuffer(a2))) == (C,(CompactBuffer(c1),CompactBuffer(c2, c3))) == (E,(CompactBuffer(),CompactBuffer(e1)))
  }

  def joinTest:Unit={
    val video: RDD[String] = sc.textFile(USER_SPARK_PATH+"rdd\\data\\joinTest.txt")
    val area: RDD[String] =  sc.textFile(USER_SPARK_PATH+"rdd\\data\\joinTest1.txt")

    val SPLIT = " "
    val VideoPair = video.map(s => {
      val ss = s.split(SPLIT)
      (ss(0), ss(1))
    })
    VideoPair.partitions
    val AreaPair= area.map(s=>{
      val ss = s.split(SPLIT)
      (ss(0),ss(1))
    })
    val joinRdd1 = VideoPair.map(t1=>t1.swap).join(AreaPair)
//    joinRdd1.partitioner
//    joinRdd1.dependencies

    val cogoupRdd = VideoPair.map(_.swap)
      .cogroup(AreaPair,new HashPartitioner(2))

//    cogoupRdd.partitioner
//    cogoupRdd.dependencies

    cogoupRdd.flatMapValues(p =>{
      for(v<- p._1.iterator;w<-p._2.iterator)
        yield (v,w)
    })
    val cg = new CoGroupedRDD[String](Seq(VideoPair.map {
      t2 => t2.swap
    }, AreaPair), new HashPartitioner(2))

    cg.dependencies
  }


}
