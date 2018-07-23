package com.hochoy.scalatest

/**
  * Created by Hochoy on 2018/07/23.
  * https://blog.csdn.net/u010454030/article/details/53899587
  */
object MapTest {

    def main(args: Array[String]) {
        val scores = scala.collection.mutable.Map("Alice"->10,"Bob"->20)
        val bobS = scores("Bob")
        val bs = if (scores.contains("Bob")) scores("Bob") else 0

        var bs2 = scores.getOrElse("Bob",0)

        println(bs)
        println(bs2)
        scores("Bob") = 90
        bs2 = scores.getOrElse("Bob",0)
        println(bs2)

        scores("Fred") = 92
        scores.+=("Marry"->88)
        scores.+=("Cobub"->87,"Hochoy"->90)
        println(scores)

    }
}
