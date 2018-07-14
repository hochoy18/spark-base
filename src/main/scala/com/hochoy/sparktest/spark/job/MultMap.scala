package com.hochoy.sparktest.spark.job

/**
  * Created by Cobub on 2018/6/26.
  */
object MultMap {


  def main(args: Array[String]) {
    val v =List( (123,234,345,456,567), (123,234,345,456,567), (123,234,345,456,567))
    v.map{
      x=>{
        println(x._1+1000)
        println(x._2+1000)
        println(x._3+1000)
        println("---------")
      }

    }
    println("---------=========")
    v.map{
      x=>{
        println(x._4+5000)
        println(x._5+5000)
        println("---------")
      }
    }



  }

}
