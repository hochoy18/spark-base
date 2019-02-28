package com.hochoy.clickhouse;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/2/26
 */
public class ClickHouseJDBC {
    private static String address = "jdbc:clickhouse://192.168.1.240:8123/default";
    private static String driver = "ru.yandex.clickhouse.ClickHouseDriver";

    private static String allUser = "select count(1) as ALL_USER_COUNT from (" +
            "select distinct deviceid from action_data_c4j_all  " +
            "where appkey = '33645ea6eda746b3be4cfc5fa23ec6a6'" +
            ")";// 特定应用下所有 用户
    private static String activeUser = "select count(*) as ACTIVE_USER_OF_DAY from (" +
            "select distinct deviceid  from action_data_c4j_all  " +
            "where appkey = '33645ea6eda746b3be4cfc5fa23ec6a6' " +
            "and time >= '2018-09-03 00:00:00' and time <'2018-09-04 00:00:00'" +
            ");"; // 特定应用下 2018-09-03 的活跃用户

    private static String sql_nanjing = "select count(1) as ALL_USER_COUNT_Nanjing from (" +
            "select distinct deviceid from action_data_c4j_all  " +
            "where appkey = '33645ea6eda746b3be4cfc5fa23ec6a6' " +
            "and  country = '中国' and region = '江苏' and city = '南京'" +
            ")"; //应用下 南京 所有 用户

    private static String launch_count = "select count(*) as LAUNCH_COUNT from action_data_c4j_all  " +
            "where appkey = '33645ea6eda746b3be4cfc5fa23ec6a6' " +
            "and  action = '$launch' " +
            "and time >= '2018-09-03 00:00:00' and time <'2018-09-04 00:00:00';"; // 特定应用下 2018-09-03 的启动次数

    private static String login_count = "select count(*) as LOGIN_COUNT from action_data_c4j_all  " +
            "where appkey = '33645ea6eda746b3be4cfc5fa23ec6a6' " +
            "and  action like  'login%' " +
            "and time >= '2018-09-03 00:00:00' and time <'2018-09-04 00:00:00'";
    private static String group_by_version_channelid = "select count(*) as GROUP_BY_VERSION_CHANNELID , version ,channelid   from action_data_c4j_all  " +
            "where appkey = '33645ea6eda746b3be4cfc5fa23ec6a6' " +
            "and  action = '$launch'  and platform = 'android' " +
            "and time >= '2018-09-03 00:00:00' and time <'2018-09-04 00:00:00' " +
            "group by  version ,channelid";


    private static String group_by_module = "select count(*) as GROUP_BY_MODULE, module  from action_data_c4j  " +
            "where appkey = '33645ea6eda746b3be4cfc5fa23ec6a6'  " +
            "and time >= '2018-09-03 00:00:00' and time <'2018-10-04 00:00:00'  " +
            "group by module";

    private static String avg_duration = " select round(avg(duration)) as ROUND_AVG  from action_data_c4j  " +
            "where appkey = '33645ea6eda746b3be4cfc5fa23ec6a6'  " +
            "and time >= '2018-09-03 00:00:00' and time <'2018-10-04 00:00:00'";

    private static String group_by_version_channelid_having = "select count(*) as GROUP_BY_VERSION_CHANNELID_HAVING, version ,channelid   from action_data_c4j_all  " +
            "where appkey = '33645ea6eda746b3be4cfc5fa23ec6a6' and  action = '$launch'  and platform = 'android' " +
            "and time >= '2018-09-03 00:00:00' and time <'2018-09-04 00:00:00' " +
            "group by  version ,channelid having GROUP_BY_VERSION_CHANNELID_HAVING > 50 and channelid like '1000%'";
    public static void main(String[] args) {
        connection = init();
        exeSql(allUser);
        exeSql(activeUser);
        exeSql(sql_nanjing);
        exeSql(launch_count);
        exeSql(login_count);
        exeSql(avg_duration);
        exeSql(group_by_version_channelid);
        exeSql(group_by_version_channelid_having);
        exeSql(group_by_module);
        close();
    }

    private static Connection connection = null;

    private  static void exeSql(String sql) {
        Statement statement = null;
        ResultSet results = null;
        try {
            statement = connection.createStatement();
            long begin = System.currentTimeMillis();
            results = statement.executeQuery(sql);
            long end = System.currentTimeMillis();
            System.out.println("耗时："+(end - begin) + "ms" + "  执行（" + sql + "）");
//            ResultSetMetaData rsmd = results.getMetaData();
//            List<Map> list = new ArrayList();
//            while (results.next()) {
//                Map map = new HashMap();
//                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//                    map.put(rsmd.getColumnName(i), results.getString(rsmd.getColumnName(i)));
//                }
//                list.add(map);
//            }
//            for (Map map : list) {
//                System.err.println(map);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {//关闭连接
            try {
                if (results != null) {
                    results.close();
                }
                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
//        try {
//            Thread.sleep(1000);
//        }catch (Exception e){
//
//        }
    }

    private static Connection init() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
