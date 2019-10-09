package com.hochoy.spark.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.sql.types._
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success, Try}

/**
  * Created by IntelliJ IDEA.
  * Time:   11/23/15 4:18 PM
  *
  * @author hongbing.li
  */
object Util {
  val logger = LoggerFactory.getLogger(getClass)
  val SUPPORTED_TYPES: Set[DataType] =
    Set(IntegerType, LongType, StringType, DateType, TimestampType)

  /**
    *convert date string to java.sql.TimeStamp
    * @param s
    * @return
    */
  def getTimestamp(s: Any): Timestamp= {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    Try(new Timestamp(dateFormat.parse(s.toString).getTime)) match {
      case Success(t) => t
      case Failure(_) => new Timestamp(new Date().getTime)
    }
  }

  def getSchemaFields(schema: StructType): String = {
    schema.printTreeString()
    if (schema.isEmpty) {
      throw new UnsupportedOperationException(s"Empty schema $schema is not supported, please " +
        s"provide at least one column of a type ${SUPPORTED_TYPES.mkString("[", ", ", "]")}")
    }
    schema.fields.map { field =>
      field.name + ":" + field.dataType.simpleString
    }.reduce(_ + "," + _)
  }

  def positions(length: Long, numSlices: Int): Iterator[(Int, Int)] = {
    (0 until numSlices).iterator.map { i =>
      val start = ((i * length) / numSlices).toInt
      val end = (((i + 1) * length) / numSlices).toInt
      (start, end)
    }
  }

//  def extract(events: List[String], funDef: List[String]): Map[Int, Long] = {
//    // tmp event buffer
//    val eventBuffer = mutable.ArrayBuffer(events: _*)
//    // reached levels
//    val lvs = mutable.HashMap(funDef.indices.map(i => i -> 0L): _*)
//
//    var next = eventBuffer.indexWhere(_ == funDef.head, 0)
//    while (next != -1) {
//      // index of each event in a session
//      var i = next
//      // index of each event in a funnel defination
//      var j = 0
//      // extract event sequence defined in the funnel from event sequence in the session
//      while (i < eventBuffer.length && j < funDef.length) {
//        if (eventBuffer(i) == funDef(j)) {
//          eventBuffer.remove(i)
//          j += 1
//        }
//        else i += 1
//      }
//      if (j > 0) lvs(j - 1) += 1
//      next = eventBuffer.indexWhere(_ == funDef.head, next)
//    }
//    (lvs.size - 2 to 0 by -1).foreach { i =>
//      lvs(i) += lvs(i + 1)
//    }
//
//    lvs.toMap
//  }

  def mergeValuesToSetByKey[K, T](map1: mutable.HashMap[K, mutable.HashSet[T]],
                                  map2: Map[K, Option[T]]
                                 ): mutable.HashMap[K, mutable.HashSet[T]] = {
    map2.foreach { case (k, v) =>
      if (map1.isDefinedAt(k)) map1(k) ++= v
      else map1 += (k -> (mutable.HashSet.empty[T] ++= v))
    }
    map1
  }

  def unionSetsByKey[K, T](map1: mutable.HashMap[K, mutable.HashSet[T]],
                           map2: mutable.HashMap[K, mutable.HashSet[T]]
                          ): mutable.HashMap[K, mutable.HashSet[T]] = {
    (map1.keySet ++ map2.keySet).foreach { k =>
      if (map1.isDefinedAt(k) && map2.isDefinedAt(k)) map1(k) ++= map2(k)
      else if (!map1.isDefinedAt(k) && map2.isDefinedAt(k)) map1 += (k -> map2(k))
    }
    map1
  }

//  def getZookeeperConf(config: com.typesafe.config.Config ):Map[String,String]={
//    val map = collection.mutable.Map[String,String]()
//    if (config.hasPath("connectionMode") &&
//      "zookeeper".equalsIgnoreCase(config.getString("connectionMode"))){
//      map += (
//        "hbase.zookeeper.quorum" -> config.getString("quorum"),
//        "hbase.zookeeper.property.clientPort" -> config.getString("clientPort")
//      )
//      if(config.hasPath("znodeParent")){
//        map +=("zookeeper.znode.parent" → config.getString("znodeParent"))
//      }
//    }
//    map.toMap
//  }

  def main(args: Array[String]): Unit = {
    var intToLong = extract(List("e_sys_login", "ylhkh_jh_smxx","lc_sjc_xqy_wh", "sy001"),
      List("e_sys_login", "ylhkh_jh_smxx", "lc_sjc_xqy_wh", "sy001$$sy002"))
    println(intToLong)

    intToLong = extract1(List("e_sys_login", "ylhkh_jh_smxx","lc_sjc_xqy_wh", "sy001"),
      List("e_sys_login", "ylhkh_jh_smxx", "lc_sjc_xqy_wh", "sy001", "sy002"))
    println(intToLong)
  }

  def extract(events: List[String], funDef: List[String]): Map[Int, Long] = {
    val eventBuffer: ArrayBuffer[String] = mutable.ArrayBuffer(events: _*)
    val lvs = mutable.HashMap(funDef.indices.map(_ -> 0L):_*)

    var next: Int = eventBuffer.indexWhere(i=> funDef.head.split("$$").contains(i), 0)
    while (next != -1) {
      var i = next
      var j = 0
      while (i < eventBuffer.length && j < funDef.length){
        if (  funDef(j).split("$$").contains(eventBuffer(i))){
          eventBuffer.remove(i)
          j += 1
        }
        i += 1
      }
      if(j > 0) lvs(j-1) += 1
      next = eventBuffer.indexWhere(funDef.head.split("$$").contains(_),next)
    }
    (lvs.size -2 to 0 by -1).foreach(
      i =>lvs(i) += lvs(i + 1)
    )

    println(lvs)
    lvs.toMap
  }

  def extract1(events: List[String], funDef: List[String]): Map[Int, Long] = {
    // tmp event buffer
    val eventBuffer = mutable.ArrayBuffer(events: _*)
    // reached levels
    val lvs = mutable.HashMap(funDef.indices.map(i => i -> 0L): _*)

    var next = eventBuffer.indexWhere(_ == funDef.head, 0)
    while (next != -1) {
      // index of each event in a session
      var i = next
      // index of each event in a funnel defination
      var j = 0
      // extract event sequence defined in the funnel from event sequence in the session
      while (i < eventBuffer.length && j < funDef.length) {
        if (eventBuffer(i) == funDef(j)) {
          eventBuffer.remove(i)
          j += 1
        }
        else i += 1
      }
      if (j > 0) lvs(j - 1) += 1
      next = eventBuffer.indexWhere(_ == funDef.head, next)
    }
    (lvs.size - 2 to 0 by -1).foreach { i =>
      lvs(i) += lvs(i + 1)
    }

    println(lvs)
    lvs.toMap
  }





}
