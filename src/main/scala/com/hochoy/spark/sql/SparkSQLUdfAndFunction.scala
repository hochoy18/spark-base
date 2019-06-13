package com.hochoy.spark.sql

import com.hochoy.spark.utils.Constants.{FILE_PATH, USER_SPARK_PATH}
import com.hochoy.spark.utils.SparkUtils._
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._


/** Describe:
  * Created by IntelliJ IDEA.
  * Time: 9:40
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
object SparkSQLUdfAndFunction {

  val spark = createSparkSession("SparkSQLUdfAndFunction")

  def main(args: Array[String]): Unit = {

    udf_test1
    avgUdaf
    countUDAF
    group_max_UDAF
    group_sum_count_avg_UDAF
    spark.stop()
  }

  def udf_test1: Unit = {

    val sales = Array("2018-01-01,50,Teacher",
      "2018-01-01,60,Hello",
      "2018-01-01,70,world",
      "2018-01-01",
      "2018-01-02,150,good",
      "2018-01-02,250,Morning")

    import spark.implicits._

    val saleDf = spark.sparkContext.parallelize(sales)
      .filter(x ⇒ x.split(",").length == 3)
      .map(x ⇒ x.split(","))
      .map(x ⇒ Sale(x(0), x(1).toInt, x(2)))
      .toDF

    import org.apache.spark.sql.functions._
    saleDf.groupBy("date")
      .agg(sum("price").as("money")).show

    spark.udf.register("strLen", (s: String) ⇒ s.length + 100)

    saleDf.createOrReplaceTempView("hochoy_sales")

    val frame = spark.sql(
      "select date,strLen(date) as date_len,price,strLen(price) as price_len ,name from hochoy_sales")
    frame.show()

    val strUpper = udf((str: String) ⇒ {
      str.toUpperCase()
    })

    frame.withColumn("name_upper", strUpper($"name")).show()

  }

  case class Sale(date: String, price: Int, name: String)

  def avgUdaf: Unit = {
    spark.read.json(USER_SPARK_PATH + s"${FILE_PATH}salary.json")
      .createOrReplaceTempView("employee")
    spark.udf.register("salary_avg", new avgUDAF)
    spark.sql("select salary_avg(salary) from employee").show()


  }

  def countUDAF: Unit = {
    spark.read.json(USER_SPARK_PATH + s"${FILE_PATH}salary.json").createOrReplaceTempView("employee")
    spark.udf.register("count_udaf", new countUDAF)
    val df0 = spark.sql("select name ,salary from employee")
    df0.explain(true)
    df0.show()
    val df = spark.sql("select count_udaf(1) as c_1,count_udaf(name) as c_name,count_udaf(salary) as c_salary from  employee")
    df.explain(true)
    df.show

  }

  def group_max_UDAF: Unit = {
    spark.read.json(USER_SPARK_PATH + s"${FILE_PATH}salary_withgender.json").createOrReplaceTempView("employee")
    spark.udf.register("group_max_UDAF", new group_max_UDAF)
    val df0 = spark.sql("select * from employee")
    df0.explain(true)
    println("====================true========================")
    df0.explain()
    df0.show()

    val df1 = spark.sql("select gender, group_max_UDAF(salary) from employee group by gender")
    df1.explain(true)
    df1.show()
  }

  def group_sum_count_avg_UDAF: Unit = {
    spark.read.json(USER_SPARK_PATH + s"${FILE_PATH}salary_withgender.json").createOrReplaceTempView("employee")
    spark.udf.register("group_sum_count_avg_UDAF", new group_sum_count_avg_UDAF)
    val frame = spark.sql("select * from employee")
    frame.explain(true)
    Thread.sleep(1000 * 3)
    frame.show()
    Thread.sleep(1000 * 10)
    val frame1 = spark.sql("select gender,group_sum_count_avg_UDAF(salary) from employee group by gender")
    frame1.explain(true)
    Thread.sleep(1000 * 3)
    frame1.show()
    Thread.sleep(1000 * 60 *5)
  }
}

class avgUDAF extends UserDefinedAggregateFunction {
  // 输入数据的类型
  override def inputSchema: StructType =
    StructType(StructField("salary", DataTypes.LongType) :: Nil)


  // 产生中间结果的数据类型
  // 每一个分区中 的 共享变量
  override def bufferSchema: StructType =
    StructType(
      StructField("sum", DataTypes.LongType) ::
        StructField("count", DataTypes.IntegerType) :: Nil)

  // 最终返回的结果类型
  override def dataType: DataType = DataTypes.LongType

  //  确保一致性， 一般用true
  override def deterministic: Boolean = true

  // 指定初始值
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L // sum
    buffer(1) = 0 // count
  }

  // 每一个分区中的每一条数据，聚合时需要调用该方法
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    // sum : 获取这一行中的工资，然后将工资加入到sum 中
    buffer(0) = buffer.getLong(0) + input.getLong(0)
    // 将 工资的 count + 1
    buffer(1) = buffer.getInt(1) + 1
  }

  // 将每一个分区的输出合并，形成最后的数据
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getInt(1) + buffer2.getInt(1)
  }

  override def evaluate(buffer: Row): Any = {
    buffer.getLong(0) / buffer.getInt(1)
  }
}

class groupUDAF extends UserDefinedAggregateFunction {
  override def inputSchema: StructType =
    StructType(
      StructField("field1", DataTypes.IntegerType) ::
        StructField("field2", DataTypes.StringType) :: Nil)


  override def bufferSchema: StructType =
    StructType(StructField("field", DataTypes.StringType) :: Nil)

  override def dataType: DataType =
    DataTypes.StringType

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit =
    buffer.update(0, "")

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    val bs = input.getInt(0)
    val in = input.getString(1)

    var field = buffer.getString(0)
    if (bs > 0 && "" != in && !field.contains(in)) {
      field += "," + in
    }
    buffer.update(0, field)
  }


  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    var f1 = buffer1.getString(0)
    val f2 = buffer2.getString(0)
    if (!"".equals(f2)) {
      f1 += "," + f2
    }
    buffer1.update(0, f1)

  }

  override def evaluate(buffer: Row): Any = {

  }
}

class countUDAF extends UserDefinedAggregateFunction {
  //输入参数类型
  override def inputSchema: StructType =
    StructType(StructField("inputColum", DataTypes.StringType) :: Nil)

  // 中间缓存的数据类型
  override def bufferSchema: StructType =
    StructType(StructField("sum", DataTypes.LongType) :: Nil)

  // 最终输出结果的数据类型
  override def dataType: DataType = DataTypes.LongType

  override def deterministic: Boolean = true

  // 初始值：要是DataSet 无数据，就返回该值
  override def initialize(buffer: MutableAggregationBuffer): Unit = buffer(0) = 0L

  /**
    *
    * @param buffer : 相当于把当前分区的每行数据都需要进行计算，计算的结果保存到buffer 中
    * @param input
    */
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if (!input.isNullAt(0)) {
      buffer(0) = buffer.getLong(0) + 1L
    }
  }

  /**
    * 相当于把每个分区的数据进行汇总, 将buffer2 （各个分区） 的数据汇总到总汇总数据（buffer1) 上
    *
    * @param buffer1 : 汇总的数据
    * @param buffer2 ：各个分区 的数据
    */
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
  }

  override def evaluate(buffer: Row): Any = buffer.getLong(0)
}

class group_max_UDAF extends UserDefinedAggregateFunction {
  override def inputSchema: StructType = StructType(StructField("inputColumn", DataTypes.LongType) :: Nil)

  override def bufferSchema: StructType =
    StructType(
      StructField("sum", DataTypes.LongType) ::
        StructField("count", DataTypes.LongType) :: Nil)

  override def dataType: DataType = DataTypes.LongType

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit = buffer(0) = 0L

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if (!input.isNullAt(0)) {
      if (buffer.getLong(0) < input.getLong(0)) {
        buffer(0) = input.getLong(0)
      }
    }
  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    if (buffer1.getLong(0) < buffer2.getLong(0)) {
      buffer1(0) = buffer2.getLong(0)
    }
  }

  override def evaluate(buffer: Row): Any = buffer.getLong(0)
}

class group_sum_count_avg_UDAF extends UserDefinedAggregateFunction {
  override def inputSchema: StructType = StructType(StructField("inputColumn", DataTypes.LongType) :: Nil)

  override def bufferSchema: StructType = StructType(StructField("sum", DataTypes.LongType)::StructField("count", DataTypes.LongType) :: Nil)

  override def dataType: DataType = DataTypes.LongType

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    //    buffer(0) = 0L
    //    buffer(1) = 0L
    buffer.update(0, 0L)
    buffer.update(1, 0L)
  }
  var i =0
  /**
    * 每个分区数据处理
    *
    * @param buffer
    * @param input
    */
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    i  =i+1
    buffer(0) = buffer.getLong(0) + input.getLong(0) // sum
    buffer(1) = buffer.getLong(1) + 1L // count
     input.getAs[Long]("input0")

    println(s" update=>  sum: ${buffer(0)}   count: ${buffer(1)}    i: $i" )
  }

  /**
    *
    * @param buffer1 : 汇总数据
    * @param buffer2 ：各分区要汇总的数据
    */
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0) // sum
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1) // count

  }

  override def evaluate(buffer: Row): Any = {
    (buffer.getLong(0), buffer.getLong(1), (buffer.getLong(0) / buffer.getLong(1)).toLong)
    (buffer.getLong(0) / buffer.getLong(1)).toLong
  }
}