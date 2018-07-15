package com.hochoy.sparktest.spark.job.json

import java.io.File

import net.sf.json.JSONObject
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable
import scala.util.parsing.json.JSON


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
    val  rdd = sc.textFile(path +"\\src\\main\\scala\\com\\hochoy\\sparktest\\spark\\job\\json\\productInfo.json")
    val list = List.empty
    val maprdd = rdd.map(x=>{
      val  op  = JSON.parseFull(x)
      op match {
        case Some(map : Map[String, Any])=>{
          op.map(println)
          val info = map.get("Querystorebyproductid")

          var li = info.toList
          println("..........."+li)




          li


//          val  productId = if( info.getOrElse("productId","").toString=="") "000"
//          val userId = if (info.getOrElse("userId","") =="") "u_000"
//          val  longitude = Integer.parseInt(info.getOrElse("longitude",0).toString)+0.0008
//          val storeId = info.getOrElse("storeId","")
//          val latitude =Integer.parseInt(info.getOrElse("latitude",0).toString)+0.00009
//
//
//          val tuple = (userId,longitude,latitude,storeId)
//          var m1=scala.collection.mutable.Map[String,Any]()
//
//          m1+=("userId"->userId)
//          var m = scala.collection.mutable.Map[String,(_,_,_,_)]()
//          m+=(productId.toString->tuple)
//
//          list.+:(m)
//
//          list

        }
        case None => println("none")
        case other => println("other")
      }

    })

    println("############")
    list.foreach(println)
    println("============")
    maprdd.collect().foreach(println)

    maprdd.map(println)
    rdd.foreach(println)
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
