package com.hochoy.spark.utils

import scala.collection.mutable
import org.json4s.jackson.JsonMethods._

object JSON {

  def toMutableMap(j: String): mutable.Map[String, String] = {
    try {
      val a = parse(j)
      println("a    " + a)
      val b = a.values
      println("b   " + b)
      val c = b.asInstanceOf[Map[String, AnyRef]]
      println("c     " + c)

      val map = parse(j).values.asInstanceOf[Map[String, AnyRef]].mapValues(Some(_).mkString.trim).withDefaultValue(Constants.Unknown)
      mutable.HashMap(map.toSeq: _*)
    }
  }

  def toListMap(j: String): List[Map[String, _]] = {
    try {
//      val v = parse(j)
//      println(v)
//      val q = v.children
//      println(q)
      (parse(j) \ "data").children.map { jValue =>
        val properties = jValue \ "properties"
        val removed = jValue.removeField {
          case ("properties", _) => true
          case _ => false
        }
//        val vv = properties.merge(removed)
//        println(vv)
//        println(vv.values)
//        val vvv = vv.values.asInstanceOf[Map[String, _]]
//        vvv
        properties.merge(removed).values.asInstanceOf[Map[String,_]]
      }
    } catch {
      case e: Exception ⇒ List.empty
    }
  }

  def main(args: Array[String]): Unit = {
    //{"data":[{"appkey":"10afd440920111e6ac2374dfbf1bfb16","properties":{"utm_campaign":"双十一剁手节","is_update":"false","network":"EDGE","language":"ca_ES_EURO","utm_content":"0814-tool","channelid":"360","screen_width":256,"utm_source":"今日头条","mccmnc":"45400","pagetitle":"PageTitle_1444538903572","platform":"android","version":"1.1","utm_medium":"BottomBanner","screen_height":800,"duration":15912,"is_new_device":"false","model":"华为Mate20 Pro","osversion":"2.3.7","page":"RegistActivity","manufacturer":"nokia","refer":""},"userid":"20000459021","longitude":"121.5111137000","lib_version":"1.0.0","clienttime":"2019-01-04 00:01:18.653","latitude":"31.1676971000","sessionid":"28b85cafcbea492284dadb2c412e1272","deviceid":"iOS_DEVICEID_1535066713776476","action":"e_sys_transaction","actionattach":{}}]}
    val str = "{\"data\":[{\"appkey\":\"10afd440920111e6ac2374dfbf1bfb16\",\"properties\":{\"utm_campaign\":\"双十一剁手节\",\"is_update\":\"false\",\"network\":\"EDGE\",\"language\":\"ca_ES_EURO\",\"utm_content\":\"0814-tool\",\"channelid\":\"360\",\"screen_width\":256,\"utm_source\":\"今日头条\",\"mccmnc\":\"45400\",\"pagetitle\":\"PageTitle_1444538903572\",\"platform\":\"android\",\"version\":\"1.1\",\"utm_medium\":\"BottomBanner\",\"screen_height\":800,\"duration\":15912,\"is_new_device\":\"false\",\"model\":\"华为Mate20 Pro\",\"osversion\":\"2.3.7\",\"page\":\"RegistActivity\",\"manufacturer\":\"nokia\",\"refer\":\"\"},\"userid\":\"20000459021\",\"longitude\":\"121.5111137000\",\"lib_version\":\"1.0.0\",\"clienttime\":\"2019-01-04 00:01:18.653\",\"latitude\":\"31.1676971000\",\"sessionid\":\"28b85cafcbea492284dadb2c412e1272\",\"deviceid\":\"iOS_DEVICEID_1535066713776476\",\"action\":\"e_sys_transaction\",\"actionattach\":{}}]}"

    val v = toListMap(str)
    println(v)
    //⇒
    //=>
  }


}
