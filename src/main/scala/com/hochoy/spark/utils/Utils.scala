package com.hochoy.spark.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import org.joda.time.{DateTime, Days}
import org.joda.time.format.DateTimeFormat

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
  * Created by IntelliJ IDEA.
  * Time:   11/23/15 4:18 PM
  *
  * @author jianghe.cao
  */
object Utils {

  /**
    * Get hour number since the starting hour.
    *
    * @param time Time format: yyyy-MM-dd HH:mm:ss
    * @return Hour number since the starting hour
    */
  def getHourNum(time: String, format: String = "yyyy-MM-dd HH:mm:ss"): Int = {
    val dtf = DateTimeFormat.forPattern(format)
    // end-milliseconds
    val em = dtf.parseDateTime(time).getMillis
    // start-milliseconds
    val sm = new DateTime(2014, 1, 1, 0, 0).getMillis

    // Hour number since the starting hour
    ((em - sm) / (1000 * 3600)).toInt
  }


  /**
    * Get day number since the starting hour.
    *
    * @param startDay Time format: yyyyMMdd
    * @param endDay   Time format: yyyyMMdd
    * @return Hour number since the starting hour
    */
  def getDayNum(startDay: String, endDay: String, format: String = "yyyyMMdd"): Int = {
    val dtf = DateTimeFormat.forPattern(format)
    val s = dtf.parseDateTime(startDay)
    val e = dtf.parseDateTime(endDay)
    Days.daysBetween(s, e).getDays
  }

  /**
    * Check if date's format matches the given format.
    *
    * @param date   the date
    * @param format the format
    * @return if matches, return true, else return false
    */
  def isDateValid(date: String, format: String = "yyyy-MM-dd HH:mm:ss") = (try {
    val fmt = DateTimeFormat.forPattern(format)
    Some(fmt.parseDateTime(date))
  } catch {
    case e: IllegalArgumentException => None
  }).nonEmpty


  /**
    * Convert hex string to byte array.
    *
    * @param hex a hex string
    * @return a byte array, every byte stores 2 digits of the hex
    */
  def hexStr2Bytes(hex: String): Array[Byte] = {
    if ((hex.length & 0x01) != 0) {
      // if not even, pad with 0
      "0" + hex.toLowerCase
    } else hex.toLowerCase

    hex.grouped(2).map(Integer.parseInt(_, 16).toByte).toArray
  }

  /**
    * convert date string to java.sql.TimeStamp
    *
    * @param s
    * @return
    */
  def getTimestamp(s: Any): Timestamp = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    Try(new Timestamp(dateFormat.parse(s.toString).getTime)) match {
      case Success(t) => t
      case Failure(_) => new Timestamp(new Date().getTime)
    }
  }

  /**
    * convert Any  to Long
    *
    * @param s
    * @return
    */
  def toLongOrZero(s: Any): Long = {
    Try(s.toString.toLong) match {
      case Success(t) => t
      case Failure(_) => 0L
    }
  }

  def toShortOrZero(s: Any): Short = {
    Try(s.toString.toShort) match {
      case Success(t) => t
      case Failure(_) => 0
    }
  }

  /**
    * convert Any  to String
    *
    * @param s
    * @return
    */
  def toStringOrNull(s: Any): String = {
    if (s == null)
      null
    else {
      if (s.toString.length == 0)
        null
      else s.toString
    }
  }

  def toFloat(s: Any): Float = {
    try {
      s.toString.toFloat
    } catch {
      case _ => -1.0f
    }
  }


  def parseBoolean(s: Any): Boolean =
    if (s != null) s.toString.toLowerCase match {
      case "true" => true
      case _ => false
    }
    else
      return false

  /**
    * convert Any  to map
    *
    * @param s
    * @return
    */
  //  def toMapOrNull(s: Any): Map[String,String]= {
  //    try {
  //      var res = mutable.Map[String,String]()
  //      var tmp = s.asInstanceOf[Map[String,String]]
  //      if(tmp.isEmpty) null else {
  //        castToType
  ////        tmp.map( x ⇒ res+=(x._1 → String.valueOf(x._2)))
  ////        res.toMap
  //      }
  //    } catch {
  //      case _ => null
  //    }
  //
  //  }




  import shapeless._
  import syntax.typeable._

  def toMapOrNull(s: Any): Map[String, String] = try {
    val y = s.cast[Map[String, String]]
    y match {
      case Some(x) ⇒ x
      case None ⇒ null
    }
  }


//  def main(args: Array[String]): Unit = {
//    val m  = Map("111"→"111")
//    val mm1:Map[String,String] = m.clone()
//    val mm = toMapOrNull(m)
//    println(mm)
//  }

  def castToType[A <: AnyRef : Manifest](a: Any): A = manifest[A].erasure.cast(a).asInstanceOf[A]

}
