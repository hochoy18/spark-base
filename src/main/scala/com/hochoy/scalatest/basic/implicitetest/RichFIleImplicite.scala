package com.hochoy.scalatest.basic.implicitetest


/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月07日 14:25
  * @version :  V1.0
  */
object RichFIleImplicite {


  implicit def fileToRichFile (filePath : String ) = new RichFile(filePath)
}