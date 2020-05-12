package com.hochoy.spark

package object rdd {
  def dir2Path(separator:String,  dirs :String*): String = dirs.mkString( separator)
}
