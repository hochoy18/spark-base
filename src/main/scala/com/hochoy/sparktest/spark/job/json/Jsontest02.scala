package com.hochoy.sparktest.spark.job.json

import com.fasterxml.jackson.core.JsonParseException
import org.apache.spark.{SparkContext, SparkConf}
import org.json4s.jackson.JsonMethods._

/**
  * Created by Cobub on 2018/8/26.
  */
object Jsontest02 extends Serializable{
  val conf = new SparkConf().setMaster("local").setAppName("json")
  val sc = new SparkContext(conf)

  val path = System.getProperty("user.dir")

  def main(args: Array[String]) {
    test01()
  }

  def test01(): Unit = {

    val rdd = sc.textFile(path + "\\src\\main\\scala\\com\\hochoy\\sparktest\\spark\\job\\json\\Razor.event.log")
    val r = rdd.map(Jsontest02.jsonParse(_))
    r.collect().foreach(x=>{println("lllllllll            "+x) })
    println(r.collect().length)

  }

  def jsonParse(j: String): List[Map[String, AnyRef]] = {
    var yy = List[Map[String, AnyRef]]()
    try {
       yy = (parse(j) \ "data").children.map { s =>
//        println(s)
        val appkey = (s \ "appkey").values.toString
        val v =s.merge(parse(s"""{ "productid":"___________$appkey"}""")).values.asInstanceOf[Map[String,AnyRef]].mapValues(Some(_).mkString.trim)
          .withDefaultValue("unknown")
//        println("v----"+v)
        v
      }
//      println("yy.............."+yy)
      yy


    } catch {
      case e: JsonParseException => {
        println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh")
        List.empty
      }
      case e: Exception =>
        println("'''''''''''''''''''hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        List.empty
    }

  }

}
