package com.hochoy.scalatest.basic.implicitetest

import scala.io.Source

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月07日 14:24
  * @version :  V1.0
  */
class RichFile(val filepath: String) {

  def read(): String = Source.fromFile(filepath).mkString
}

object RichFile {
  def main(args: Array[String]) {
    import RichFIleImplicite.fileToRichFile
    val path = System.getProperty("user.dir")
    val file = path + "\\src\\main\\scala\\com\\hochoy\\sparktest\\spark\\job\\json\\productInfo.json"
    val content = file.read()
    println(content)
  }
}