package com.hochoy.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

public class DBUtils {

    static Queue<Connection> connectionQueue = null;

    static Properties properties = HochoyUtils.getProperties("mysql.properties");


    static {
        try {
            Class.forName(properties.getProperty("driver.class"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection(){
        if (connectionQueue == null || connectionQueue.isEmpty()){
            connectionQueue = new LinkedList<>();
            try {

                for (int i = 0; i < 3; i++) {
                    Connection conn = DriverManager.getConnection(
                            properties.getProperty("jdbc.url"),
                            properties.getProperty("jdbc.username"),
                            properties.getProperty("jdbc.password"));
                    connectionQueue.offer(conn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connectionQueue.poll();
    }
    public static void returnConnection(Connection conn){
        connectionQueue.offer(conn);
    }
    public static void closeConnections(){
        while (connectionQueue != null && ! connectionQueue.isEmpty()){
            Connection conn = connectionQueue.poll();
            try {
                if (conn!= null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connectionQueue = null;

    }

}
