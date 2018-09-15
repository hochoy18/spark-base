package com.hochoy.scalatest.io.file

import scala.io.Source

/**
  * Created by Cobub on 2018/8/29.
  */
object SourceTest {
  def main(args: Array[String]) {
    readLine
    readChar
    readWord
    readFromURL
    source.close()
  }
  val path = System.getProperty("user.dir")
  val source = Source.fromFile(path+"\\src\\main\\scala\\com\\hochoy\\scalatest\\io\\file\\story.txt","UTF-8")
  val sourceCet6 = Source.fromFile(path+"\\src\\main\\scala\\com\\hochoy\\scalatest\\io\\file\\cet6.txt","UTF-8")

  def readFromURL(): Unit ={
    val sourceUrl = Source.fromURL("https://www.baidu.com/","UTF-8")
    println(sourceUrl)
    val lines = sourceUrl.getLines()
    for (l<-lines){
      print(l)
      Thread.sleep(100)
    }
    println
    val source1 = Source.fromString("hello,world")
    println(source1.mkString)
    val source2 = Source.stdin
    println(source2.mkString)

  }


  def readWord(): Unit ={
    val lines = sourceCet6.getLines()

    for(l<-lines){
      val tokens = l.split("\\s+")//将每行数据以空格分开
      for (i<-tokens){
        print(i+ " , ")
      };println
//      val numbers = tokens.map(_.toDouble)
//      for (n<- numbers){
//        print(n+"|")
//      };println()

    }
  }

  def readChar(): Unit ={

    for (c <- source){
      print(c)
      Thread.sleep(100)
    }

    // //使用bufferd方法，用head获取字符
    val it = source.buffered
    while (it.hasNext){
      if (it.head.equals("重")){
        it.next()
        it.next()
        print(it.head)
      }
      else it.next()
    }

  }


  /**
    * 读取文件这里不能重复读取，只显示一遍文本内容
    * 以下只会print一次文件内容
    */
  def readLine(){

    val lines = source.getLines()


    val arr = lines.toArray
    for(i<-arr){
      println(i)
      Thread.sleep(800)
    }
    for(l<-lines){
      println(l)
      Thread.sleep(500)
    }
    val ctx = source.mkString
    println(ctx)

  }

}
