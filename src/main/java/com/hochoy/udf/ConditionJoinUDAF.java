package com.hochoy.udf;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.expressions.MutableAggregationBuffer;
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/6/6
 */
public class ConditionJoinUDAF extends UserDefinedAggregateFunction {

    @Override
    public StructType inputSchema() {
        List<StructField> structFields = new ArrayList<>();
        structFields.add(DataTypes.createStructField("field1", DataTypes.IntegerType, true));
        structFields.add(DataTypes.createStructField("field2", DataTypes.StringType, true));
        return DataTypes.createStructType(structFields);
    }

    @Override
    public StructType bufferSchema() {
        List<StructField> structFields = new ArrayList<>();
        structFields.add(DataTypes.createStructField("field", DataTypes.StringType, true));
        return DataTypes.createStructType(structFields);
    }

    @Override
    public DataType dataType() {
        return DataTypes.StringType;
    }

    @Override
    public boolean deterministic() {
        return false;
    }

    @Override
    public void initialize(MutableAggregationBuffer buffer) {
        buffer.update(0, "");
    }

    @Override
    public void update(MutableAggregationBuffer buffer, Row input) {
        Integer bs = input.getInt(0);
        String field = buffer.getString(0);
        String in = input.getString(1);
        if (bs > 0 && !"".equals(in) && !field.contains(in)) {
            field += "," + in;
        }
        buffer.update(0, field);
    }

    @Override
    public void merge(MutableAggregationBuffer buffer1, Row buffer2) {
        String field1 = buffer1.getString(0);
        String field2 = buffer2.getString(0);
        if (!"".equals(field2)) {
            field1 += "," + field2;
        }
        buffer1.update(0, field1);
    }

    @Override
    public Object evaluate(Row buffer) {
        return StringUtils.join(Arrays.stream(buffer.getString(0).split(",")).filter(line -> !line.equals("")).toArray(), ",");
    }
}

class test2 {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local[2]");
        sparkConf.setAppName("test");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        SQLContext sqlContext = new SQLContext(sc);

        sqlContext.udf().register("con_join", new ConditionJoinUDAF());

        JavaRDD<String> lines = sc.textFile("E:/advance/bigdata/spark/sparktest01/src/main/java/com/hochoy/udf/example.txt");
        JavaRDD<Row> rows = lines.map(line -> RowFactory.create(line.split("\\^")));
        rows.foreach(v -> {
            System.out.println(v.get(0).toString()+"\t"+v.get(1).toString()+"\t"+v.get(2).toString());
        });

        List<StructField> structFields = new ArrayList<>();
        structFields.add(DataTypes.createStructField("a", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("b", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("c", DataTypes.StringType, true));
        StructType structType = DataTypes.createStructType(structFields);

        Dataset<Row> test = sqlContext.createDataFrame(rows, structType);
        test.registerTempTable("test");
        sqlContext.sql("SELECT a,b,c   FROM test ").show();
        sqlContext.sql("SELECT con_join(c,b)  as join_ FROM test GROUP BY a").show();

        sc.stop();
    }
}