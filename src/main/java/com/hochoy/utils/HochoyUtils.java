package com.hochoy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class HochoyUtils {
    private HochoyUtils() {
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties(String fileName) {
        Properties prop = new Properties();
        InputStream in;
        try {
            in = HochoyUtils.class.getClassLoader().getResourceAsStream(fileName);

            prop.load(in);
            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }


    public static Connection getMySQLConn(String fileName) {
        Properties properties = getProperties(fileName);
        Connection conn = null;
        try {
            Class.forName(properties.getProperty("driver.class")).newInstance();
            conn = DriverManager.getConnection(properties.getProperty("jdbc.url"), properties.getProperty("jdbc.username"), properties.getProperty("jdbc.password"));
//
//            PreparedStatement preparedStatement = conn.prepareStatement(sql,args);
//            for (int i = 1; i <= args.length; i++) {
//                preparedStatement.setString(i,args[i-1]);
//            }
//
//            preparedStatement.executeUpdate();
//
//            ResultSet resultSet = preparedStatement.executeQuery();
//            resultSet.get
//            while (resultSet.next()){
//
//            }

        } catch (InstantiationException | SQLException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static int update(Connection conn, String sql, Object... args) {

        PreparedStatement statement = null;
        int execute = 0;
        try {
            statement = conn.prepareStatement(sql);
            for (int i = 1; i <= args.length; i++) {
                statement.setObject(i, args[i - 1]);
            }
            execute = statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return execute;
    }

    public static long getNums(Connection conn, String sql, Object... args) {

        List<Map<String, Object>> query = query(conn, sql, args);
        if (query == null || query.isEmpty())
            return 0;

        Object[] values = query.get(0).values().toArray();
        return Long.parseLong(values[0].toString());
    }
    public static Map<String, Object> findOne(Connection conn, String sql, Object... args) {

        List<Map<String, Object>> query = query(conn, sql, args);
        if (query == null || query.isEmpty())
            return new HashMap<>();
        return query.get(0);
    }
    public static List<Map<String,Object>> query(Connection conn ,String sql,Object... args) {
        List<Map<String,Object>> res = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            for (int i = 1; i <= args.length; i++) {
                preparedStatement.setObject(i,args[i-1]);
            }

              resultSet = preparedStatement.executeQuery();


            while (resultSet.next()){
                Map<String,Object> map = new HashMap<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
//                    String columnClassName = metaData.getColumnClassName(i);
//                    System.out.println(columnClassName);
                    Object object = resultSet.getObject(i);
                    map.put(columnName,object);
                }
                res.add(map);
            }


        } catch (SQLException   e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement !=null ){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

        return res;

    }

    public static void close(Connection conn){
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        Connection mySQLConn = getMySQLConn("mysql.properties");

        List<Map<String, Object>> query = query(mySQLConn, "select * from mysql_offset");
        for (Map<String, Object> map : query) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                System.out.printf(" %s : %s", entry.getKey(),entry.getValue());
            }
            System.out.println();
        }

        int update = update(mySQLConn, "insert into mysql_offset values ('111','11',1,1,1)");
        System.out.println(update);

        query = query(mySQLConn, "select * from mysql_offset");
        for (Map<String, Object> map : query) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                System.out.printf(" %s : %s", entry.getKey(),entry.getValue());
            }
            System.out.println();
        }

        close(mySQLConn);
    }


}

