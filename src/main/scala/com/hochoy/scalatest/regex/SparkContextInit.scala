package com.hochoy.scalatest.regex

;

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月14日 17:07
  * @version :  V1.0
  */
object SparkContextInit {

  def main(args: Array[String]) {
    import SparkMasterRegex._

    var master: String = "local[2,3]"
    master = "spark://babylon.com"
    master = "local-cluster[21,3,4]"
    master match {
      case "local" => println(master)
      case LOCAL_N_REGEX(threads) => {
        val reg = """local\[([0-9]+|\*)\]"""

        /**
          * [  :标记一个中括号表达式的开始。要匹配 [，请使用 \[
          * |  :指明两项之间的一个选择。要匹配 |，请使用 \|。
          * () :标记一个子表达式的开始和结束位置。子表达式可以获取供以后使用。要匹配这些字符，请使用 \( 和 \)。
          * +  :匹配前面的子表达式一次或多次。要匹配 + 字符，请使用 \+
          */
        println(s"---------threads is  $threads")
      }
      case LOCAL_N_FAILURES_REGEX(threads, maxFailures) => {
        /**
          * \s  :匹配任何空白字符，包括空格、制表符、换页符等等。等价于 [ \f\n\r\t\v]。注意 Unicode 正则表达式会匹配全角空格符。
          * *  :匹配前面的子表达式零次或多次。要匹配 * 字符，请使用 \*。
          */
        val reg = """local\[([0-9]+|\*)\s*,\s*([0-9]+)\]""".r
        println(s"--------threads is $threads , maxFailures is $maxFailures")
      }
      case SPARK_REGEX(sparkUrl) =>
        println(s"-------sparkUrl is $sparkUrl")
      case LOCAL_CLUSTER_REGEX(numSlaves,coresPerSlave,memoryPerSlave)=>
        println(s"--------numSlaves is $numSlaves, coresPerSlave  is $coresPerSlave, memoryPerSlave is  $memoryPerSlave")
    }


  }
}

private object SparkMasterRegex {
  // Regular expression used for local[N] and local[*] master formats
  val LOCAL_N_REGEX =
    """local\[([0-9]+|\*)\]""".r
  // Regular expression for local[N, maxRetries], used in tests with failing tasks
  val LOCAL_N_FAILURES_REGEX =
    """local\[([0-9]+|\*)\s*,\s*([0-9]+)\]""".r
  // Regular expression for simulating a Spark cluster of [N, cores, memory] locally
  val LOCAL_CLUSTER_REGEX =
    """local-cluster\[\s*([0-9]+)\s*,\s*([0-9]+)\s*,\s*([0-9]+)\s*\]""".r
  // Regular expression for connecting to Spark deploy clusters
  val SPARK_REGEX =
    """spark://(.*)""".r
}
object test1001{
  def main(args: Array[String]) {
    println(Runtime.getRuntime.availableProcessors())
  }
}