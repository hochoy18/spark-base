package com.hochoy.spark.utils

object CaseClasses {




  case class Event(//productid: String,
                   global_user_id: String,
                   sessionid: String,
                   action: String,
                   clienttime: Long,
                   //channelid: String,
                   platform: String,
                   version: String
                  )

}