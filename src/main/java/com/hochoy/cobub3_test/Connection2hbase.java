package com.hochoy.cobub3_test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;


/**
 * 配置信息
 *
 * @Author: hongbing.li
 * @Date: 10/12/2018 10:35 AM
 */
public class Connection2hbase {
    static Configuration hbaseConfig = null;
    static Connection hConn = null;
    private static final Logger logger = LoggerFactory.getLogger(Connection2hbase.class);

    static {

        Map<String, String> maps = new HashMap<>();
        Properties propFlies = new Properties();
        try {
            propFlies.load(Connection2hbase.class.getResourceAsStream("/application.properties"));
            Set<String> sets = propFlies.stringPropertyNames();
            maps = (Map) propFlies;
            System.getProperties().setProperty("HADOOP_USER_NAME", maps.get("hbase.username"));
        } catch (Exception e) {
            logger.error("HBase初始化配置失败:",e);
        }


        hbaseConfig = HBaseConfiguration.create();
        try {
            if (maps.get("env") != null && !"".equals(maps.get("env").trim())) {
                InputStream inputStream = Connection2hbase.class.getResourceAsStream("/hbase-site.xml");
                hbaseConfig.addResource(inputStream);
            } else {
                hbaseConfig.set("hbase.zookeeper.property.clientPort", maps.get("hbase.zookeeper.property.clientPort"));
                hbaseConfig.set("hbase.zookeeper.quorum", maps.get("hbase.zookeeper.quorum"));
                if(!"".equals( maps.get("hbase.master").trim())){
                    hbaseConfig.set("hbase.master", maps.get("hbase.master"));
                }
                if(!"".equals(maps.get("zookeeper.znode.parent").trim())){
                    hbaseConfig.set("zookeeper.znode.parent",maps.get("zookeeper.znode.parent"));
                }

            }

            //init kerberos before
//            KerberosUtil.kinit(hbaseConfig);
            hConn = ConnectionFactory.createConnection(hbaseConfig);
        } catch (IOException e) {
            logger.error("io exception", e);
        }
    }

    private Connection2hbase() {
    }

    public static Configuration getHbaseConfig() {
        return hbaseConfig;
    }

    public static Connection getHbaseConn() {
        return hConn;
    }


    public static Table getTable(String tableName) {
        Table table = null;
        try {
            table = hConn.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            logger.error("Connection2hbase", e);
        }

        return table;
    }

    public static void closeTable(Table table, ResultScanner rs) {
        if (null != table) {
            try {
                table.close();
            } catch (IOException e) {
                logger.error("关闭Table失败", e);
            }
        }

        if (null != rs) {
            rs.close();
        }
    }

    public static void closeTableOnly(Table table) {
        if (null != table) {
            try {
                table.close();
            } catch (IOException e) {
                logger.error("关闭Table失败", e);
            }
        }
    }

    public static boolean createTable(String tableName) {
        Admin admin = null;
        try {
            admin = hConn.getAdmin();
            if (admin.tableExists(TableName.valueOf(tableName))) {
                admin.close();
                return true;
            } else {
                HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
                tableDesc.addFamily(new HColumnDescriptor("f".getBytes()));
                admin.createTable(tableDesc);
                admin.close();
                return true;
            }
        } catch (Exception e) {
            logger.info("Connection2hbase", e);
            return false;
        } finally {
            if (admin != null) {
                try {
                    admin.close();
                } catch (IOException e) {
                    logger.error("IOException was caught ", e);
                }
            }
        }
    }

    public static void deleteTable(String tableName) throws IOException {
        TableName tn = TableName.valueOf(tableName);
        Admin admin = hConn.getAdmin();
        if (admin.tableExists(tn)) {
            admin.disableTable(tn);
            admin.deleteTable(tn);
        }
        admin.close();
    }

    public static String[][] getData(String taskId, String startDate, String endDate, String fields) {   //获取某个表，task_id/datestart/dateend的所有数据

        List<String[]> list = new ArrayList<>();


        String tableName = "cobub3:fix_report";
        Table fixTable = getTable(tableName);
        Scan scan = new Scan();

        scan.setCaching(Constants.SCANCACHING);
        scan.setCacheBlocks(Constants.CACHEBLOCKS);
        String startRow = taskId + "_" + startDate;
        scan.setStartRow(startRow.getBytes());
        String stopRow = taskId + "_" + endDate;
        scan.setStopRow(stopRow.getBytes());
        ResultScanner scanner = null;
        try {
            scanner = fixTable.getScanner(scan);
            for (Result re : scanner) {
                String[] row;
                row = new String[1 + fields.split(",").length]; //第一列是日期,后面具体的列
                int j = 0;
                row[j] = Bytes.toString(re.getRow()).split("_", 2)[1];
                for (Cell cell : re.rawCells()) {
                    j++;
                    row[j] = Bytes.toString(CellUtil.cloneValue(cell));
                }
                list.add(row);
            }
        } catch (IOException e) {
            logger.info("Table.getScanner(scan)------", e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return list.toArray(new String[][]{});
    }

    /**
     * Generate prefix of row key for HTables: *_Product_Device.
     *
     * @param key
     * @param length
     * @return
     */
    public static String genPrefix(MessageDigest md, String key, int length) {
        String rowKey = "";
        String md5Key;
        try {
            md5Key = OptimizeUtil.md5Hashing(md, key);
            rowKey = md5Key.substring(0, length - 1) + "_" + key;
        } catch (Exception e) {
            logger.error("HBase获得前缀发生异常", e);
        }
        return rowKey;
    }


    public static String genMD5Rk(String productId, String other)  {
        String alSplit = "\001";
        String rowkey = null;
        String body = productId + alSplit + other;
        try {
            rowkey = genPrefix(MessageDigest.getInstance("MD5"), body, 4).substring(0, 3) + alSplit + body;
        }catch (Exception e){
            logger.error("HBase获得MD5值发生异常",e);
        }
        return rowkey;
    }

    /**
     * 插入多条记录
     * @param tableName
     */
    public static void batchPut(String tableName, List<Put> putList){
        Table table = getTable(tableName);
        try {
            table.put(putList);
        } catch (IOException e) {
            logger.error("批量插入HBase表"+tableName+",发生异常"+e);
        }finally {
                try {
                    table.close();
                } catch (IOException e) {
                    logger.error("HBase表"+tableName+",关闭异常"+e);
                }
        }

    }

    /**
     * 批量删除一列数据
     * @param table
     * @param deleteList
     * @throws IOException
     */
    public static void batchDeleteColumn(Table table, List<Delete> deleteList) throws IOException {
        table.delete(deleteList);
    }




}
