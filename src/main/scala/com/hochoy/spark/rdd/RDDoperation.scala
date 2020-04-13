package com.hochoy.spark.rdd

import com.hochoy.spark.utils.SparkUtils
import com.hochoy.utils.BitmapUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.roaringbitmap.RoaringBitmap

import scala.collection.mutable.ArrayBuffer

/** Describe:
  * Created by IntelliJ IDEA.
  * Time: 11:07
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
object RDDoperation {

  val sc = SparkUtils.createSparkContext(this.getClass.getName)
  //  sc.getConf.set("spark.io.compression.codec", "snappy")
//  sc.setLogLevel("WARN")

  def main(args: Array[String]): Unit = {
    joinDependency1
    //    sumdef
//    aggregateTest
    //    groupByKeyTest
    //    mapPartitionWithIndexTest
    //    joinDependency
//    combineByKeyTest
//    sortBy
//    unionTest
  }

  val score = List(
    ("math", 100), ("Chinese", 100),
    ("math", 90), ("Chinese", 98),
    ("hbase", 100), ("hadoop", 56),
    ("math", 100), ("hadoop", 34),
    ("hbase", 200))
  val rdd1 = sc.makeRDD(score)

  def aggregateTest(): Unit = {
    /**
      * aggregate：针对单个元素rdd 的操作
      * aggregateByKey ：针对key-value 类型 的rdd的操作
      * aggregateByKey = groupByKey + aggregate,只对每一组中values进行聚合，
      *
      *
      * zeroValue: U        ：初始值
      * seqOp: (U, V) => U  ：迭代操作，作用于rdd中的每个分区，在每个分区中的每个元素跟初始值合并
      * combOp: (U, U) => U ：分区结果数据最终聚合
      */
    val rdd = sc.parallelize(1 to 10)

    /**
      * zeroValue: U        ：初始值
      * seqOp: (U, V) => U  ：迭代操作，作用于rdd中的每个分区，在每个分区中的每个元素跟初始值合并
      * combOp: (U, U) => U ：分区结果数据最终聚合
      */
    val sum = rdd.aggregate(zeroValue = 0)( // 0: 初始值
      seqOp = (U: Int, V: Int) ⇒ U + V, // U:
      combOp = (U1: Int, U2: Int) ⇒ U1 + U2
    )
    println(s"sum........................$sum")


    /**
      * zeroValue:U ：  (Int:总和,Int：总个数)：初始值
      * seqOp: (U, V)：  U:(Int,Int),V:Int)：分区内的值与初始值合并
      * combOp: (U, U)：  UU:(Int,Int),U:(Int,Int)：分区之间的值合并
      */
      println(".....................aggregate only for common rdd ....................")
    val sum_count = rdd.aggregate(zeroValue= (0, 0))(
      seqOp = (U: (Int, Int), V: Int) ⇒ (U._1 + V, U._2 + 1),
      combOp = (UU: (Int, Int), U: (Int, Int)) ⇒ (UU._1 + U._1, UU._2 + U._2)
    )
    println(s"sum................${sum_count._1}......count...${sum_count._2}")
    val avg: Double = sum_count._1.toDouble / sum_count._2
    println("avg........................ " + avg)

//    rdd1.aggregate()


    val count_ : RDD[(String, Int)] = rdd1.aggregateByKey(
      zeroValue = 0 )(
      seqOp = (U, V) ⇒ U + V,
      combOp = (U1, U2) ⇒ U1 + U2
    )
    println("---------------aggregateByKey----------------")
    rdd1.foreach(println)
    println
    count_.foreach(println)
    val s_c: RDD[(String, (Int, Int))] = rdd1.aggregateByKey(
      zeroValue = (0, 0)
    )(
      seqOp = (U, V) => (U._1 + V, U._2 + 1),
      combOp = (U1, U2) ⇒ (U1._1 + U2._1, U1._2 + U2._2)
    )
    println("------------------sum & count ...................")
    s_c.foreach(println)
    println("------------------avg ...................")
    s_c.map(f ⇒ (f._1, f._2._1.toDouble / f._2._2)).foreach(println)

  }

  def sumdef: Int = {

    println("........." + (1 to 5).fold(0)((x, y) ⇒ x + y))
    val sum_count_ = (1 to 5).map((_, 1)).fold((0, 0))((x, y) ⇒ (x._1 + y._1, x._2 + 1))
    println(s"sum_count_._1   $sum_count_    ${sum_count_._1 / sum_count_._2}")

    type B = (Int, Int)
    val foldLeft1 = (1 to 5).foldLeft(
      z = (0, 0)
    )(op = (x: B, y) ⇒ (x._1 + y, x._2 + 1))
    //      op)
    //    def op(b:B,i:Int):B = (b._1  + i, b._2 +1)
    //      (op: B, b: Int) ⇒ (op._1 + b, op._1 + 1))
    println(s"foldLeft.........$foldLeft1")


    // 假设data 是由 (1 to 5), (2 to 6), (3 to 7) 三个分区组成，
    val data = Array((1 to 5), (2 to 6), (3 to 7))


    var sum_ = 0;
    var count_ = 0
    data.foreach(partition___ ⇒ {
      var sum_partition_ = 0
      var count_partition = 0
      partition___.foreach(element_in_partition ⇒ {
        sum_partition_ = sum_partition_ + element_in_partition
        count_partition += 1

      })
      sum_ += sum_partition_
      count_ += count_partition
      (sum_, count_)
    })
    println(s"sum_ ....... $sum_     ; count....... $count_     ; avg..........${Math.floorDiv(sum_.toLong, count_)}")


    var sum = 0;
    data.foreach(partition___ ⇒ {
      var sum_partition = 0;
      partition___.foreach(element_in_partition ⇒
        //每个partition中求和
        sum_partition = sum_partition + element_in_partition
      )
      println(s"sum_partition   $sum_partition")
      sum += sum_partition
    })
    println(s"sum。。。。。。。   $sum")
    sum
  }

  def singleSum (data :Array[Range.Inclusive] ):Int ={
    var sum = 0;
    data.foreach(partition___ ⇒ {
      var sum_partition = 0;
      partition___.foreach(element_in_partition ⇒
        //每个partition中求和
        sum_partition = sum_partition + element_in_partition
      )
      println(s"sum_partition   $sum_partition")
      sum += sum_partition
    })
    println(s"sum。。。。。。。   $sum")
    sum
  }




  def groupByKeyTest(): Unit = {
    //  RDD[(K, Iterable[V])]
    println("================groupByKey=====================")
    val groupRdd = rdd1.groupByKey()
    groupRdd.foreach(x ⇒ println(x))
    val avg = groupRdd.map { v ⇒
      //      val subject = v._1
      //      val avg = v._2.sum / v._2.toList.size
      (v._1, v._2.sum / v._2.toList.size)
    }
    avg.foreach(println)

    println("================sortByKey=====================")
    avg.sortByKey(false, 2).foreach(println)
    println("================sortByKey   按照分数高->低排序=====================")
    avg.map(_.swap).sortByKey(false).map(_.swap).foreach(println)
    println("================sortBy 实现按value排序=====================")
    avg.sortBy((x: (String, Int)) ⇒ x._2, false).foreach(println)


  }

  def mapPartitionWithIndexTest(): Unit = {
    val rdd1 = sc.parallelize(1 to 40, 5)
    val rdd2 = rdd1.mapPartitions(it ⇒ {
      println("执行了一次处理。。。。。。。。")
      it.map((x: Int) ⇒ x * x)
    })
    rdd2.foreach(println(_))

    val rdd3 = rdd1.mapPartitionsWithIndex((index, it) ⇒ {
      println(s"partition num is $index  ..............")
      it.map(x ⇒ x * x)
    })
    rdd3.foreach(println)
    rdd3.foreachPartition(it ⇒ println(it.mkString("[[[  ", "  ,  ", "]]]")))

  }

  def joinDependency(): Unit = {
    val rdd1: RDD[(Int, String)] = sc.parallelize(Array(
      (1, "zhangsan-1"),
      (1, "zhangsan-2"),
      (2, "lisi"),
      (3, "wangwu"),
      (4, "Tom"),
      (5, "Gerry"),
      (6, "莉莉")
    ), 1)
      //      .map(x ⇒ (x, null)).reduceByKey((x, y) ⇒ x, 1)
      //      .mapPartitions(iter ⇒ iter.map(tuple ⇒ tuple._1), true
      //      )
      .cache()
    rdd1.collect().foreach(println)
    rdd1.foreachPartition(it ⇒ it.foreach(x ⇒ println(x._1, x._2)))
    val rdd2 = sc.parallelize(Array(
      (1, "shanghai...."),
      (2, "北京1"),
      (2, "北京2"),
      (3, "nanjing")
      , (4, "纽约"),
      (5, "New York"),
      (6, "深圳"),
      (7, "香港")), 1)
      //      .map(x=>(x,null))
      //      .reduceByKey((x,y)⇒x,1).mapPartitions(it ⇒
      //      it.map(t⇒t._1),true
      //    )
      .cache()
    rdd2.collect().foreach(println)
    println("内连接+++++++++++++++++++++++++++++")
    val joinRes = rdd1.join(rdd2).map {
      case (id, (name, address)) ⇒ {
        (id, name, address)
      }
    }
    println("----------------------")
    joinRes.foreachPartition(it ⇒ {
      it.foreach(println)
    })
    Thread.sleep(5 * 1000)
    println("左外连接+++++++++++++++++++++++++++++")
    val leftJoin = rdd1.leftOuterJoin(rdd2).map {
      case (id, (name, add)) ⇒ {
        (id, name, add.getOrElse("NULL"))
      }
    }
    leftJoin.foreachPartition(it ⇒ it.foreach(println))

    rdd1.leftOuterJoin(rdd2).filter(_._2._2.isEmpty).map {
      case (id, (name, _)) ⇒ (id, name)
    }.foreachPartition(it ⇒ it.foreach(println))

    Thread.sleep(5 * 1000)
    println("右外连接+++++++++++++++++++++++++++++")
    rdd1.rightOuterJoin(rdd2).map {
      case (id, (name, add)) ⇒ (id, name.getOrElse("NULL"), add)
    }.foreachPartition(it ⇒ it.foreach(println))
    Thread.sleep(5 * 1000)
    println("全外连接+++++++++++++++++++++++++++++")
    rdd1.fullOuterJoin(rdd2).map {
      case (id, (name, add)) ⇒ (id, name.getOrElse("NULL"), add.getOrElse("NULL"))
    }.foreachPartition(it ⇒ it.foreach(println))
    Thread.sleep(500 * 1000)
    rdd1


  }

  def joinDependency1(): Unit = {

    //部分人信息(身份证,姓名)
    val people_rdd = sc.parallelize(Array(("110","lsw"),("222","yyy"))).cache()
//    val people_info: collection.Map[String, String] = people_rdd.collectAsMap()

    //全国的学生详细信息(身份证,学校名称,学号...)
    val student_all: RDD[(String, (String, String))] = sc.parallelize(
      Array(("110",( "s1", "211")),
      ("111", ("s2", "222")),
      ("112", ("s3", "233")),
      ("113", ("s2", "244"))))

    val join: RDD[(String, ((String, String), String))] = student_all.join(people_rdd)
    join.foreach(println)

    people_rdd.join(student_all).foreach(println)


    // broadcast

    val people_info: collection.Map[String, String] = people_rdd.collectAsMap()

    val bd: Broadcast[collection.Map[String, String]] = sc.broadcast(people_info)
    /**
      * 使用mapPartition而不是用map，减少创建broadCastMap.value的空间消耗
      * 同时匹配不到的数据也不需要返回（）
      * */
    val join1: RDD[(String, (String, String), String)] = student_all.mapPartitions(p => {
      val people: collection.Map[String, String] = bd.value
      val arrayBuffer = ArrayBuffer[(String, (String, String), String)]()

      p.foreach(e => {
        if (people.contains(e._1)) {
          arrayBuffer.+=((e._1, e._2, people(e._1)))
        }
      })
      arrayBuffer.iterator
    })
    println("-----------------------")
    join1.foreach(println)
    println("-----------------------")

    val res1 =  student_all.mapPartitions(it =>{
      val people: collection.Map[String, String] = bd.value
      for{
        e <- it
        if(people.contains(e._1))
      } yield (e._1, e._2,people(e._1))
    })


    res1.foreach(println)


    Thread.sleep(1000 * 60 )

  }
  def combineByKeyTest():Unit={
    val a = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
    val b = sc.parallelize(List(1,1,2,2,2,1,2,2,2), 3)
    val c = b.zip(a)
    println(c)




    val initialScores = Array(
      (("1", "011"), 1),
      (("1", "012"), 1),
      (("2", "011"), 1),
      (("2", "013"),1),
      (("2", "014"),1))
    val rdd1: RDD[((String, String), Int)] = sc.parallelize(initialScores)
    val rdd2: RDD[(String, (String, Int))] = rdd1.map(λ=> (λ._1._1,(λ._1._2,λ._2)))
//    rdd2.combineByKey((v) => (v),)





  }

  def sortBy():Unit = {

//    val studentsScore  = sc.textFile("file:///D:/advance/bigdata/spark/sparktest01/src/main/scala/com/hochoy/spark/rdd/data/scores.txt")
//      .map(_.split(","))
//    val groups=studentsScore.map(scoreInfo=>(scoreInfo(1),scoreInfo(2).toInt,scoreInfo(3).toInt,scoreInfo(4).toInt,scoreInfo(5),scoreInfo(6)))
//
//    val topK=groups.groupBy(item=>(item._6,item._5)).map(subG=>{
//      val (departmentId,classId) = subG._1
//      //语文前3
//      val languageTopK=subG._2.toList.sortBy(_._2)(Ordering.Int.reverse).take(3).map(item=>item._2+"分:学号"+item._1)
//      //数学前3
//      val mathTopK=subG._2.toList.sortBy(_._3)(Ordering.Int.reverse).take(3).map(item=>item._3+"分:学号"+item._1)
//      //外语前3
//      val englishTopK=subG._2.toList.sortBy(_._4)(Ordering.Int.reverse).take(3).map(item=>item._4+"分:学号"+item._1)
//      (departmentId,classId,Map("语文前3"->languageTopK,"数学前3"->mathTopK,"外语前3"->englishTopK))
//    })
//    topK.foreach(println)




    val list = List(
      ("20190808",null,"afsda fdasd fas",1),
      ("20190808",null,"afsda ad fafadf",2),
      ("20190809",null,"afsda fdasd sfa",1),
      ("20190809",null,"afsda ad fasswf",5)
    )
    val rdd: RDD[(String, Null, String, Int)] = sc.makeRDD(list)
//    rdd.map(row => ((row._1,row._2),row._3)).groupByKey()
//    val value: RDD[((String, Null), List[(String, Null, String, Int)])] = rdd.groupBy(r =>(r._1,r._2)).map(v => (v._1,v._2.toList.sortBy(_._4).take(0)))
//    value.collect().foreach(println(_))

    val value: RDD[(String, Null, String, Int)] = rdd.groupBy(item => (item._1, item._2))
      .map(data => {
        val head: (String, Null, String, Int) = data._2.toList.sortBy(it => it._4)(Ordering.Int.reverse).head
        head
      })
    val res0: RDD[(String, Null, String, Int)] = value.map(v=>(v._1,v._2,v._3,v._4))
    res0.foreach(v=>println(v))



  }

  def unionTest(): Unit ={
    val res0 = List((100,1),(101,1),(102,1),(103,1),(201,1))
    val res1 = List((200,1),(201,1),(202,1),(203,1))
    val rdd0: RDD[(Int, Int)] = sc.makeRDD(res0)
    val rdd1: RDD[(Int, Int)] = sc.makeRDD(res1)
    val rdd: RDD[(Int, Int)] = rdd0.union(rdd1)
    rdd.collect().foreach(println(_))
    Thread.sleep(1000 * 100)
  }



}
object test {
  def main(args: Array[String]): Unit = {
//    listTest

    test1

  }

  def test1={
    val set1 = Set(1,2,3,4,5,6,7,8,20)
    val set2:Set[Int] = Set(1,2,3,4,5,6,7,11111111)

    val bitmap1: RoaringBitmap = RoaringBitmap.bitmapOf()
    val bitmap2: RoaringBitmap = RoaringBitmap.bitmapOf()
    set1.foreach(bitmap1.add(_))
    set2.foreach(bitmap2.add(_))
    val str1 = new String(BitmapUtils.serializeBitMapToByteArray(bitmap1),"iso8859-1")
    val str2 = new String(BitmapUtils.serializeBitMapToByteArray(bitmap2),"iso8859-1")
    println(str1.size)
    println(str2.size)
  }
  def listTest(): Unit ={
    val list = List(
      ("20190808",null,"afsda fdasd fas",1),
      ("20190808",null,"afsda ad fafadf",2),
      ("20190809",null,"afsda fdasd sfa",1),
      ("20190809",null,"afsda ad fasswf",5)
    )
    val resHead: (String, Null, String, Int) = list.head
    println(resHead)
    val head: (String, Null, String, Int) = list.sortBy(it => it._4)(Ordering.Int.reverse).head
    println(head)
  }
}