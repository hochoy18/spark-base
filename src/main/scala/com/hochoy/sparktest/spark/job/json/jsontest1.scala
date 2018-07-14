package com.hochoy.sparktest.spark.job.json

import scala.util.parsing.json.JSON

/**
  * Created by Cobub on 2018/7/14.
  */
object jsontest1 {

  def main(args: Array[String]) {
    val str2 = "{\"et\":\"kanqiu_client_join\",\"vtm\":1435898329434,\"body\":{\"client\":\"866963024862254\",\"client_type\":\"android\",\"room\":\"NBA_HOME\",\"gid\":\"\",\"type\":\"\",\"roomid\":\"\"},\"time\":1435898329}"

    val b = JSON.parseFull(str2)
    b match {
      case Some(map:Map[String,Any])=>{
        println(map)
        println(b)

      }
      case None => println("parsing failed")
      case other => println("Unknown data structure:"+other)
    }

  }


}
