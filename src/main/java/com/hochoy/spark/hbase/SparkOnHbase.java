package com.hochoy.spark.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;


public class SparkOnHbase {

    public static void main(String[] args) throws Exception {

        System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer");

        SparkSession spark=SparkSession.builder()
                .appName("lcc_java_read_hbase_register_to_table")
                .master("local[*]")
                .getOrCreate();

        JavaSparkContext context = new JavaSparkContext(spark.sparkContext());

        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "192.168.18.228,192.168.18.229,192.168.18.232");
        configuration.set("hbase.zookeeper.property.clientPort","2181");
        configuration.set("hbase.master", "192.168.18.228:60000");

        Scan scan = new Scan();
        String tableName = "tableTest";
        configuration.set(TableInputFormat.INPUT_TABLE, tableName);

        ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
        String ScanToString = Base64.encodeBytes(proto.toByteArray());
        configuration.set(TableInputFormat.SCAN, ScanToString);

        JavaPairRDD<ImmutableBytesWritable, Result> myRDD = context.newAPIHadoopRDD(configuration,TableInputFormat.class, ImmutableBytesWritable.class, Result.class);

        JavaRDD<Row> personsRDD = myRDD.map(new Function<Tuple2<ImmutableBytesWritable,Result>,Row>() {

            @Override
            public Row call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
                // TODO Auto-generated method stub
                System.out.println("====tuple=========="+tuple);
                Result result = tuple._2();
                String rowkey = Bytes.toString(result.getRow());
                String id = Bytes.toString(result.getValue(Bytes.toBytes("info1"), Bytes.toBytes("id")));
                String name = Bytes.toString(result.getValue(Bytes.toBytes("info1"), Bytes.toBytes("name")));
                String password = Bytes.toString(result.getValue(Bytes.toBytes("info1"), Bytes.toBytes("password")));

                //这一点可以直接转化为row类型
                return RowFactory.create(rowkey,id,name,password);
            }

        });

        List<StructField> structFields=new ArrayList<StructField>();
        structFields.add(DataTypes.createStructField("rowkey", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("id", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("name", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("password", DataTypes.StringType, true));
//        structFields.add(DataTypes.createStructField("age", DataTypes.StringType, true));

        StructType schema=DataTypes.createStructType(structFields);

        Dataset stuDf=spark.createDataFrame(personsRDD, schema);
        //stuDf.select("id","name","age").write().mode(SaveMode.Append).parquet("par");
        stuDf.printSchema();
        stuDf.createOrReplaceTempView("Person");
        Dataset<Row> nameDf=spark.sql("select * from Person ");
        nameDf.show();

    }

}