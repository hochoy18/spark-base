package com.hochoy.sparktest.spark.job.json

import java.io.{IOException, File}

import net.sf.json.{JSONException, JSONObject}
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSON
import scala.util.control.Breaks._


/**
  * Created by Cobub on 2018/7/14.
  */
object jsontest1 {

  val conf = new SparkConf().setMaster("local").setAppName("json")
  val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {
    test3()
  }

  def test3(): Unit = {
    val path = System.getProperty("user.dir")
    val rdd = sc.textFile(path + "\\src\\main\\scala\\com\\hochoy\\sparktest\\spark\\job\\json\\productInfo.json")

    println("length1111:...................." + rdd.collect().length)

    val maprdd = rdd.map(x => {
      try {
        val op = JSON.parseFull(x)
        op match {
          case Some(map: Map[String, Any]) => {
            val info = map.get("Querystorebyproductid")

            val li = info.toList
            println("..........." + li)

            if (!li.isEmpty) li
          }
//          case None => {
//            break;
//          }
        }
      } catch {
        case ex: JSONException => {
          println("je.........【【【【【【【【............." + ex)
        }
        case ex: Throwable => {
          println("ta....【【【【【【【【【【.........." + ex)
          println("dddddddddd      ")
        }
      }
    }).filter(xx => xx.!=(null))


    //
    println("length:...................." + maprdd.collect().length)
    //    val maprdd = rdd.map(x=>{
    //      val  op  = JSON.parseFull(x)
    //      op match {
    //        case Some(map : Map[String, Any])=>{
    //          val info = map.get("Querystorebyproductid")
    //
    //          var li = info.toList
    //          println("..........."+li)
    //
    //          li
    //
    //        }
    //        case None =>{
    //
    //          println("none111111111111111")
    //          println(x)
    //        }
    //        case other => println("other")
    //      }
    //
    //    })

    println("############")
    maprdd.collect().foreach(x => println("kkkkkkkkkkkkkkk......." + x))
    //    list.foreach(println)
    //    println("============")
    //    maprdd.collect().foreach(println)
    //
    //    maprdd.map(println)
    //    rdd.foreach(println)
  }

  def test2(): Unit = {
    val str2 = "{\"et\":\"kanqiu_client_join\",\"vtm\":1435898329434,\"body\":{\"client\":\"866963024862254\",\"client_type\":\"android\",\"room\":\"NBA_HOME\",\"gid\":\"\",\"type\":\"\",\"roomid\":\"\"},\"time\":1435898329}"
    val data = JSONObject.fromObject(str2)
    println(data)

    val et = data.optString("et", "");
    println(et)

    val et1 = data.getString("et")
    println(et1)

    val vtm = data.optInt("vtm")
    println(vtm)

    val body = data.getJSONObject("body")
    println(body)


  }

  def test() {
    val str2 = "{\"et\":\"kanqiu_client_join\",\"vtm\":1435898329434,\"body\":{\"client\":\"866963024862254\",\"client_type\":\"android\",\"room\":\"NBA_HOME\",\"gid\":\"\",\"type\":\"\",\"roomid\":\"\"},\"time\":1435898329}"

    val b = JSON.parseFull(str2)
    b match {
      case Some(map: Map[String, Any]) => {
        println(map)
        println(b)

      }
      case None => println("parsing failed")
      case other => println("Unknown data structure:" + other)
    }

  }


}
