package com.hochoy.job.persist;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hochoy on 2018/6/2.
 */
public class SparkCacheDemo {
    private static JavaSparkContext sc;
    private static SparkConf conf;
    static {
        conf = new SparkConf().setMaster("local[2]").setAppName("SparkCacheDemo");
        sc = new JavaSparkContext(conf);
//        sc.setLogLevel("error");
    }

    public static void main(String[] args) throws Exception{
        persist();

        noCache();
        cache();
    }

    public static void noCache(){
        JavaRDD rdd = sc.textFile("E:\\work\\sparktest\\src\\main\\java\\com\\hochoy\\sparktest\\spark\\job\\persist\\test.txt");
        rdd.count();
        Long t1=System.currentTimeMillis();
        System.out.println("noCache()=rdd.count()= "+ rdd.count());
        Long t2= System.currentTimeMillis();
        Long t12 = t2 - t1;
        System.out.println("noCache takes time :  " + t12);
        //noCache()=rdd.count()= 582249
        //noCache takes time :  198
    }

    public static void cache(){
        JavaRDD rdd = sc.textFile("E:\\work\\sparktest\\src\\main\\java\\com\\hochoy\\sparktest\\spark\\job\\persist\\test.txt");
        rdd.persist(StorageLevel.MEMORY_ONLY());
        rdd.count();
        Long t1=System.currentTimeMillis();
        System.out.println("Cache()=rdd.count()= "+ rdd.count());
        Long t2= System.currentTimeMillis();
        Long t12 = t2 - t1;
        System.out.println("Cache takes time :  " + t12);
        //Cache()=rdd.count()= 582249
        //Cache takes time :  70
    }


    public static void persist() throws Exception{
        List list = Arrays.asList(5, 4, 3, 2, 1, 6, 9);

        JavaRDD rdd = sc.parallelize(list);
//         rdd.persist(StorageLevel.DISK_ONLY()); //磁盘存储
//        rdd.persist(StorageLevel.MEMORY_ONLY());//内存
        rdd.persist(StorageLevel.MEMORY_ONLY_2()); //内存存储两份
        Thread.sleep( 5000l );
        rdd.collect();
        Thread.sleep( 5000l );
        rdd.collect();   //查看SparkUI的storage
        Thread.sleep( 5000l );
        rdd.unpersist(); //清除缓存
        System.out.println("===============unpersisit===================");
        Thread.sleep( 5000l );
        rdd.collect();  //这里也可以设置debug断点便于查看
    }
}
