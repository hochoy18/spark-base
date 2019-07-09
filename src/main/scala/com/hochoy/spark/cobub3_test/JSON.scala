package com.hochoy.spark.cobub3_test

import com.fasterxml.jackson.core.JsonParseException
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable

/**
 * Created by IntelliJ IDEA.
 * Time:   6/10/15 4:28 PM
 *
 * @author jianghe.cao
 */
object JSON {
  protected final val logger : Logger= LoggerFactory.getLogger(this.getClass())

  implicit val formats = DefaultFormats

  /**
   * Convert a one-level JSON String to a mutable.Map.
   *
   * @param j JSON String
   * @return if j is a valid JSON String, return a mutable.Map[String, String]. Otherwise, return
   *         an empty Map.
   */
  def toMutableMap(j: String,defaultValue:String = "unknown"): mutable.Map[String, String] = {
    try {
      val map = parse(j).values.asInstanceOf[Map[String, AnyRef]].mapValues(Some(_).mkString.trim)
          .withDefaultValue(defaultValue)
      mutable.HashMap(map.toSeq: _*)
    } catch {
      case ex: JsonParseException => mutable.Map.empty
    }
  }

  /**
    * String to Map
    * [[ notice:  for only one level  map's string  ]]
    * @param j
    * @param defaultValue
    * @return
    */
  def toKVMap(j: String,defaultValue:String = "UNKNOWN"): mutable.Map[String,String] ={


    if ( !(j.endsWith("}") && j.startsWith("{")))  mutable.Map.empty

    if(JSON.toMutableMap(j,defaultValue = defaultValue).isEmpty && "{}" !=(j.trim) && j.length>2){
      val result = collection.mutable.Map[String,String]()
      j.substring(1,j.length-1).split(",").map(x⇒ {
        val strings: Array[String] = x.split(":",2)
        if (strings.length == 2)  result += (strings(0)→strings(1))
      })
      result
    }else {
      JSON.toMutableMap(j,defaultValue = defaultValue)
    }
  }

  /**
   * Convert a one-level JSON String to a Map.
   *
   * @param j JSON String
   * @return if j is a valid JSON String, return a Map[String, String]. Otherwise, return an
   *         empty Map.
   */
  def toMap(j: String): Map[String, String] = {
    try {
      parse(j).values.asInstanceOf[Map[String, AnyRef]].mapValues(Some(_).mkString.trim)
          .withDefaultValue("UNKNOWN")
    } catch {
      case ex: JsonParseException => Map.empty
      case ex: Exception => Map.empty
    }
  }

  def toListMap(j: String): List[Map[String, _]] = {
    try {
      (parse(j) \ "data").children.map { jValue =>
        val properties = jValue \ "properties"
        val removed = jValue.removeField({
          case("properties",_) => true
          case _ => false
        })
        properties.merge(removed).values.asInstanceOf[Map[String,_]]
      }
    } catch {
      case ex: JsonParseException => {List.empty}
      case ex: Exception => {List.empty}
    }
  }




  def fromMap(map: Map[String, Any]): String = {
    write(map)
  }





}
