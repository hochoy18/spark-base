package com.hochoy.spark.sql

import com.hochoy.spark.utils.SparkUtils
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/** Describe:
  * Created by IntelliJ IDEA.
  * Time: 10:18
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
object SparkSQLOnHbase {


  def main(args: Array[String]): Unit = {

    System.setProperty("spark.serializer","org.apache.spark.serializer.KryoSerializer")

    val spark: SparkSession = SparkUtils.createSparkSession("SparkSQLOnHBase")
    val sc = spark.sparkContext
    val configuration = HBaseConfiguration.create()
    configuration.set("hbase.zookeeper.quorum", "192.168.1.211,192.168.1.212,192.168.1.213")
    configuration.set("hbase.zookeeper.property.clientPort","2181")
    configuration.set("hbase.master", "192.168.1.211:60000")

    val scan = new Scan();
    val tableName = "cobub3:cobub_users";
    configuration.set(TableInputFormat.INPUT_TABLE, tableName);

    val proto : ClientProtos.Scan = ProtobufUtil.toScan(scan)
    val scan2String = Base64.encodeBytes(proto.toByteArray)

    configuration.set(TableInputFormat.SCAN,scan2String)
    val hbaseRdd: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(configuration
      , Class[TableInputFormat]
      , Class[ImmutableBytesWritable]
      , Class[Result]
    )

    hbaseRdd.map(x ⇒{
      val result = x._2
      val rowkey = Bytes.toString(result.getRow());
    })

    s"""{
       |"table":{"namespace":"default", "name":"table1"},
       |"rowkey":"key",
       |"columns":{
       |"col0":{"cf":"rowkey", "col":"key", "type":"string"},
       |"col1":{"cf":"cf1", "col":"col1", "type":"boolean"},
       |"col2":{"cf":"cf2", "col":"col2", "type":"double"},
       |"col3":{"cf":"cf3", "col":"col3", "type":"float"},
       |"col4":{"cf":"cf4", "col":"col4", "type":"int"},
       |"col5":{"cf":"cf5", "col":"col5", "type":"bigint"},
       |"col6":{"cf":"cf6", "col":"col6", "type":"smallint"},
       |"col7":{"cf":"cf7", "col":"col7", "type":"string"},
       |"col8":{"cf":"cf8", "col":"col8", "type":"tinyint"}
       |}
       |}""".stripMargin
  }
}
