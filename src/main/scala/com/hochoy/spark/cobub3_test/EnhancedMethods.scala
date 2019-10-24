package com.hochoy.spark.cobub3_test

/**
 * Created by IntelliJ IDEA.
 * Time:   11/5/15 5:19 PM
 *
 * @author jianghe.cao
 */
object EnhancedMethods {


  implicit class EnhancedString(val s: String) {

    import scala.util.control.Exception._

    def toLongOrElse(l: Long) = catching(classOf[NumberFormatException]) opt s.toLong match {
      case Some(n: Long) => n
      case _ => l
    }

    def toLongOrZero = catching(classOf[NumberFormatException]) opt s.toLong match {
      case Some(n: Long) => n
      case _ => 0L
    }
  }


  implicit class EnhancedMap(map: Map[String, Any]) {
    /**
     * Whether the Map contains all the keys.
     *
     * @param keys List of keys
     * @return If the Map contains all the keys, return true. Otherwise return false
     */
    def containsKeys(keys: String*): Boolean = keys.map(map.contains).reduceLeft(_ && _)
    /**
     * Whether the Map contains all the keys.
     * And the key's value is not empty
     * @param keys List of keys
     * @return If the Map contains all the keys, return true. Otherwise return false
     */
    def containsKeysAndNotEmpty(keys: String*): Boolean = keys.map(key=>{map.contains(key) && !map(key).equals("")}).reduceLeft(_ && _)

    /**
     * Whether the Map contains all the keys.
     *
     * @param keys List of keys
     * @return If the Map contains all the keys, return true. Otherwise return false
     */
    def containsKeys(keys: Set[String]): Boolean = keys.map(map.contains).reduceLeft(_ && _)

  }


}
