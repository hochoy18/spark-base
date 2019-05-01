package com.hochoy.spark.design.pattern.singleton

/** Describe:
  * Created by IntelliJ IDEA.
  * Time: 10:39
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
class WorkJob {
  def main(args: Array[String]): Unit = {
    Worker.getInstance.work()
  }
}