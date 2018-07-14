package com.hochoy.mapreduce;

/**
 * Created by Cobub on 2018/6/21.
 */
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
/**
 * Created by xiaosi on 16-7-27.
 */
public class MaxTemperatureReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        // 一年最高气温
        int maxValue = Integer.MIN_VALUE;
        for(IntWritable value : values){
            maxValue = Math.max(maxValue, value.get());
        }//for
        // 输出
        context.write(key, new IntWritable(maxValue));
    }
}
