package com.hochoy.scalatest.other

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by Cobub on 2018/8/11.
  */
object RegexTest  {

  def main(args: Array[String]) {
    println(StringIsNumerical("12342 323"))
    println(DateFormat(0))
  }
  def StringIsNumerical(str:String ):Boolean =
  {
    var flag = false
    val regex = """^\d+$""".r
    flag = regex.findFirstMatchIn(str) != None
    println(".........."+flag)
    flag
  }

  def DateFormat(time :Long):String ={
    val sdf:SimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm")
    sdf.format(new Date((time)))
  }


}
