package com.hochoy.spark.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

/**
 * Created by Cobub on 2018/7/15.
 */
public class HbaseTest {

    private static final String TABLE_NAME = "scores";
    public static Configuration conf = null;
    public HTable table = null;
    public HBaseAdmin admin = null;

    static {
        conf= HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_CLIENT_PORT,"2181");
        conf.set(HConstants.ZOOKEEPER_QUORUM , "cdhtest01,cdhtest02" );
        conf.set("hbase.master", "10.14.66.215:60000");
        System.out.println(conf.get("hbase.zookeeper.quorum"));
    }

}
