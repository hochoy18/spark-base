package com.hochoy.mapreduce;
import com.google.common.collect.Lists;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.util.List;
/**
 * Created by Cobub on 2018/6/21.
 */
public class MaxTemperatureTest {
    private MapDriver mapDriver;
    private ReduceDriver reduceDriver;
    private MapReduceDriver mapReduceDriver;
    @Before
    public void setUp(){
        MaxTemperatureMapper mapper = new MaxTemperatureMapper();
        mapDriver = MapDriver.newMapDriver(mapper);
        MaxTemperatureReducer reducer = new MaxTemperatureReducer();
        reduceDriver = ReduceDriver.newReduceDriver();
        reduceDriver.withReducer(reducer);
        mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
    }
    @Test
    public void testMapper() throws IOException {
        Text text = new Text("0096007026999992016062218244+00000+000000FM-15+702699999V0209999C000019999999N999999999+03401+01801999999ADDMA1101731999999REMMET069MOBOB0 METAR 7026 //008 000000 221824Z AUTO 00000KT //// 34/18 A3004=");
        mapDriver.withInput(new LongWritable(), text);
        mapDriver.withOutput(new Text("2016"), new IntWritable(340));
        mapDriver.runTest();
        // 输出
        List<Pair> expectedOutputList = mapDriver.getExpectedOutputs();
        for(Pair pair : expectedOutputList){
            System.out.println(pair.getFirst() + " --- " + pair.getSecond()); // 2016 --- 340
        }
    }
    @Test
    public void testReducer() throws IOException {
        List<IntWritable> IntWritableList = Lists.newArrayList();
        IntWritableList.add(new IntWritable(340));
        IntWritableList.add(new IntWritable(240));
        IntWritableList.add(new IntWritable(320));
        IntWritableList.add(new IntWritable(330));
        IntWritableList.add(new IntWritable(310));
        reduceDriver.withInput(new Text("2016"), IntWritableList);
        reduceDriver.withOutput(new Text("2016"), new IntWritable(340));
        reduceDriver.runTest();
        // 输出
        List<Pair> expectedOutputList = reduceDriver.getExpectedOutputs();
        for(Pair pair : expectedOutputList){
            System.out.println(pair.getFirst() + " --- " + pair.getSecond());
        }
    }
    @Test
    public void testMapperAndReducer() throws IOException {
        Text text = new Text("0089010010999992014010114004+70933-008667FM-12+000999999V0201201N006019999999N999999999+00121-00361100681ADDMA1999990100561MD1810171+9990REMSYN04801001 46/// /1206 10012 21036 30056 40068 58017=");
        mapReduceDriver.withInput(new LongWritable(), text);
        mapReduceDriver.withOutput(new Text("2014"), new IntWritable(12));
        mapReduceDriver.runTest();
        // 输出
        List<Pair> expectedOutputList = mapReduceDriver.getExpectedOutputs();
        for(Pair pair : expectedOutputList){
            System.out.println(pair.getFirst() + " --- " + pair.getSecond()); // 2014 --- 12
        }
    }
}
