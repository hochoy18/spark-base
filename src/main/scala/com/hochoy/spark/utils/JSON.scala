package com.hochoy.spark.utils

import com.fasterxml.jackson.core.JsonParseException

import scala.collection.mutable
import org.json4s.jackson.JsonMethods._

object JSON {
  /**
    * Convert a one-level JSON String to a mutable.Map.
    *
    * @param j JSON String
    * @return if j is a valid JSON String, return a mutable.Map[String, String]. Otherwise, return
    *         an empty Map.
    */
  def toMutableMap(j: String,defaultValue:String = Constants.Unknown): mutable.Map[String, String] = {
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
  def toKVMap(j: String,defaultValue:String = Constants.Unknown): mutable.Map[String,String] ={


    if ( !(j.endsWith("}") && j.startsWith("{")))  mutable.Map[String,String]()

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


  def toListMap(j: String): List[Map[String, _]] = {
    try {
      (parse(j) \ "data").children.map { jValue =>
        val properties = jValue \ "properties"
        val removed = jValue.removeField {
          case ("properties", _) => true
          case _ => false
        }
        properties.merge(removed).values.asInstanceOf[Map[String,_]]
      }
    } catch {
      case e: Exception ⇒ List.empty
    }
  }

  def main(args: Array[String]): Unit = {
    //{"data":[{"appkey":"10afd440920111e6ac2374dfbf1bfb16","properties":{"utm_campaign":"双十一剁手节","is_update":"false","network":"EDGE","language":"ca_ES_EURO","utm_content":"0814-tool","channelid":"360","screen_width":256,"utm_source":"今日头条","mccmnc":"45400","pagetitle":"PageTitle_1444538903572","platform":"android","version":"1.1","utm_medium":"BottomBanner","screen_height":800,"duration":15912,"is_new_device":"false","model":"华为Mate20 Pro","osversion":"2.3.7","page":"RegistActivity","manufacturer":"nokia","refer":""},"userid":"20000459021","longitude":"121.5111137000","lib_version":"1.0.0","clienttime":"2019-01-04 00:01:18.653","latitude":"31.1676971000","sessionid":"28b85cafcbea492284dadb2c412e1272","deviceid":"iOS_DEVICEID_1535066713776476","action":"e_sys_transaction","actionattach":{}}]}
    val str1 = "{\"data\":[{\"appkey\":\"10afd440920111e6ac2374dfbf1bfb16\",\"properties\":{\"utm_campaign\":\"双十一剁手节\",\"is_update\":\"false\",\"network\":\"EDGE\",\"language\":\"ca_ES_EURO\",\"utm_content\":\"0814-tool\",\"channelid\":\"360\",\"screen_width\":256,\"utm_source\":\"今日头条\",\"mccmnc\":\"45400\",\"pagetitle\":\"PageTitle_1444538903572\",\"platform\":\"android\",\"version\":\"1.1\",\"utm_medium\":\"BottomBanner\",\"screen_height\":800,\"duration\":15912,\"is_new_device\":\"false\",\"model\":\"华为Mate20 Pro\",\"osversion\":\"2.3.7\",\"page\":\"RegistActivity\",\"manufacturer\":\"nokia\",\"refer\":\"\"},\"userid\":\"20000459021\",\"longitude\":\"121.5111137000\",\"lib_version\":\"1.0.0\",\"clienttime\":\"2019-01-04 00:01:18.653\",\"latitude\":\"31.1676971000\",\"sessionid\":\"28b85cafcbea492284dadb2c412e1272\",\"deviceid\":\"iOS_DEVICEID_1535066713776476\",\"action\":\"e_sys_transaction\",\"actionattach\":{}}]}"

    val v = toListMap(str1)
    println(v)
    val str = "{name:string,age:int,sex:string}"

    println(str.substring(1,str.length-1))
    println(str.substring(1,str.length-2))
    val map = toKVMap(str,defaultValue = "string")
    map.toList.foreach(println(_))

  }


}
