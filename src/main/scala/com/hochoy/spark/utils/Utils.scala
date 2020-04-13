package com.hochoy.spark.utils

import java.io.IOException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import org.joda.time.{DateTime, Days}
import org.joda.time.format.DateTimeFormat
import org.slf4j.LoggerFactory

import scala.util.control.{ControlThrowable, NonFatal}
import scala.util.{Failure, Success, Try}

/**
  * Created by IntelliJ IDEA.
  * Time:   11/23/15 4:18 PM
  *
  * @author jianghe.cao
  */
object Utils {

  val log = LoggerFactory.getLogger(this.getClass.getName.stripSuffix("$"))

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





  def toMapOrNull(s: Any): Map[String, String] = try {
    val y = s.asInstanceOf[Map[String, String]]
    if(y.isEmpty) null
    else     y
  }


//  def main(args: Array[String]): Unit = {
//    val m  = Map("111"→"111")
//    val mm1:Map[String,String] = m.clone()
//    val mm = toMapOrNull(m)
//    println(mm)
//  }

  def castToType[A <: AnyRef : Manifest](a: Any): A = manifest[A].erasure.cast(a).asInstanceOf[A]


  /**
    * Execute a block of code that evaluates to Unit, forwarding any uncaught exceptions to the
    * default UncaughtExceptionHandler
    *
    * NOTE: This method is to be called by the spark-started JVM process.
    */
  def tryOrExit(block: => Unit) {
    try {
      block
    } catch {
      case e: ControlThrowable => throw e
      case t: Throwable => //sparkUncaughtExceptionHandler.uncaughtException(t)
    }
  }

  /**
    * Execute a block of code that returns a value, re-throwing any non-fatal uncaught
    * exceptions as IOException. This is used when implementing Externalizable and Serializable's
    * read and write methods, since Java's serializer will not report non-IOExceptions properly;
    * see SPARK-4080 for more context.
    * @link org.apache.spark.util.Utils
    */
  def tryOrIOException[T](block: => T): T = {
    try {
      block
    } catch {
      case e: IOException =>

        log.error("Exception encountered", e)
        throw e
      case NonFatal(e) =>
        log.error("Exception encountered", e)
        throw new IOException(e)
    }
  }

  /**
    * Execute a block of code, then a finally block, but if exceptions happen in
    * the finally block, do not suppress the original exception.
    *
    * This is primarily an issue with `finally { out.close() }` blocks, where
    * close needs to be called to clean up `out`, but if an exception happened
    * in `out.write`, it's likely `out` may be corrupted and `out.close` will
    * fail as well. This would then suppress the original/likely more meaningful
    * exception from the original `out.write` call.
    */
  def tryWithSafeFinally[T](block: => T)(finallyBlock: => Unit): T = {
    var originalThrowable: Throwable = null
    try {
      block
    } catch {
      case t: Throwable =>
        // Purposefully not using NonFatal, because even fatal exceptions
        // we don't want to have our finallyBlock suppress
        originalThrowable = t
        throw originalThrowable
    } finally {
      try {
        finallyBlock
      } catch {
        case t: Throwable if (originalThrowable != null && originalThrowable != t) =>
          originalThrowable.addSuppressed(t)
          log.warn(s"Suppressing exception in finally: ${t.getMessage}", t)
          throw originalThrowable
      }
    }
  }
}
