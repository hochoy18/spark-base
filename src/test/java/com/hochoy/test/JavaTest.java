package com.hochoy.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
