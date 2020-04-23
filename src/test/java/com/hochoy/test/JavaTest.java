package com.hochoy.test;

import org.junit.Test;

import java.util.*;

public class JavaTest {

    public static void main(String[] args) {
        List<String> cmd = new ArrayList<>();
        cmd.add("java");
        cmd.add("-cp");
        cmd.add("xxx/*:xxx/yyy.jar:${HADOOP_HOME}/etc/hadoop/");
        cmd.add("-Xmx1024m");
        cmd.add("org.apache.spark.deploy.SparkSubmit");

        List<String> list = prepareBashCommand(cmd, new HashMap<>());
        for (String c : list) {
            System.out.print(c);
            System.out.print('\0');
        }
        System.out.println(list);
    }


    private static List<String> prepareBashCommand(List<String> cmd, Map<String, String> childEnv) {
        if (childEnv.isEmpty()) {
            return cmd;
        }

        List<String> newCmd = new ArrayList<>();
        newCmd.add("env");

        for (Map.Entry<String, String> e : childEnv.entrySet()) {
            newCmd.add(String.format("%s=%s", e.getKey(), e.getValue()));
        }
        newCmd.addAll(cmd);
        return newCmd;
    }


    @Test
    public void  arrayListTest(){

        List<String> list = new ArrayList();
        System.out.println(list);
        list.add(null);
        list.add(null);
        list.add(null);
        System.out.println(list);

        String[] arr = {null,null,null};
        for (String s : arr) {
            System.out.println(s);
        }

        List<String> list1 = new LinkedList<>();
        list1.add(null);
        list1.add(null);
        System.out.println(list1);

        Vector<String> vector = new Vector<>();
        vector.add(null);
        vector.add(null);
        System.out.println(vector);
    }
}
