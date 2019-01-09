package com.hochoy.spark.utils

import org.apache.hadoop.hbase.util.Bytes

object EnhancedMethods {

  /**
    * String to Long or Int or Float or Double
    * @param s
    */
  implicit class EnhancedString(val s: String) {

    import util.control.Exception._

    def toLongOrOne : Long = catching(classOf[NumberFormatException]) opt s.toLong match {
      case Some(n: Long) => n
      case _ => 1
    }

    def toLongOrZero: Long = catching(classOf[NumberFormatException]) opt s.toLong match {
      case Some(n: Long) => n
      case _ => 0
    }

    def toIntOne:Int = catching(classOf[NumberFormatException]) opt s.toInt match {
      case Some(x:Int) =>x
      case _ => 1
    }
    def toIntZero:Int = catching(classOf[NumberFormatException]) opt s.toInt match {
      case Some(x:Int) =>x
      case _ => 0
    }
    def toDoubleZero :Double = catching(classOf[NumberFormatException]) opt s.toDouble match {
      case Some(x:Double) => x
      case _=> 0.0d
    }
    def toDoubleOne :Double = catching(classOf[NumberFormatException]) opt s.toDouble match {
      case Some(x:Double) => x
      case _=> 1.0d
    }
    def toFloatZero:Double = catching(classOf[NumberFormatException]) opt s.toFloat match {
      case Some(n:Float) => n
      case _=> 0.0d
    }
    def toByteArray: Array[Byte] = {
      Bytes.toBytes(s)
    }
  }

  implicit class EnhancedMap(omap: Map[String, Any]) {
    def containsKeys(keys: String*): Boolean = keys.map(omap.contains).reduceLeft(_ && _)

    def containsKeysAndNotEmpty(keys: String*): Boolean = {
      keys.map(key => {
        omap.contains(key) && !"".equals(omap.get(key))
      }).reduceLeft(_ && _)
    }
    def containsKeysAndNotEmpty(keys:Set[String]):Boolean = {
      keys.map(key ⇒{
        omap.contains(key) && !"".equals(omap.get(key))
      }).reduceLeft(_&&_)
    }

    def containsKeys(keys: Set[String]): Boolean = keys.map(omap.contains).reduceLeft(_ && _)

  }

}
