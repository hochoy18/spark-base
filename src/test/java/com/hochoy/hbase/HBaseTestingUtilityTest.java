package com.hochoy.hbase;

import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HBaseTestingUtilityTest {

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @BeforeClass
    public static void beforeClass() throws Exception {
//        System.setProperty("hbase.tests.use.shortcircuit.reads", "false");
//        TEST_UTIL.enableDebug(TableInputFormat.class);
//        TEST_UTIL.enableDebug(TableInputFormatBase.class);
        System.setProperty("hadoop.home.dir", System.getProperty("user.dir") + File.separator + "hadoop-common-2.2.0-bin");
//        System.load( System.getProperty("user.dir") + File.separator + "hadoop-common-2.2.0-bin"+ File.separator  + "bin" + File.separator + "hadoop.dll");
        System.setProperty("HADOOP_USER_NAME","hdfs");

        TEST_UTIL.startMiniCluster(1);
    }
    @AfterClass
    public static void afterClass() throws Exception {
        TEST_UTIL.shutdownMiniCluster();
    }

    @Test
    public void testPutRowToTable() throws IOException {
        Admin admin = TEST_UTIL.getHBaseAdmin();
            admin.createNamespace(NamespaceDescriptor.create(HelloHBase.MY_NAMESPACE_NAME).build());
        Table table
                = TEST_UTIL.createTable(HelloHBase.MY_TABLE_NAME, HelloHBase.MY_COLUMN_FAMILY_NAME);

        HelloHBase.putRowToTable(table);
        Result row = table.get(new Get(HelloHBase.MY_ROW_ID));
        assertEquals("#putRowToTable failed to store row.", false, row.isEmpty());

        TEST_UTIL.deleteTable(HelloHBase.MY_TABLE_NAME);
        admin.deleteNamespace(HelloHBase.MY_NAMESPACE_NAME);
    }
}
