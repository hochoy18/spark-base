package com.hochoy.spark.design.pattern.singleton

/** Describe:
  * Created by IntelliJ IDEA.
  * Time: 10:35
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
object Worker {

  val instance = new Worker
  def getInstance:Worker = getInstance
}
 class Worker {
  def work()=println("I am the only worker!!!")
}


