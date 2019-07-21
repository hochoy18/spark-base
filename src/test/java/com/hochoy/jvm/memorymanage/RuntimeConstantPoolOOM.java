package com.hochoy.jvm.memorymanage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hochoy on 2019/07/21.
 *
 */
public class RuntimeConstantPoolOOM {
    public static void main(String[] args) {
        constantPoolOOMTest2();
    }
    static void constantPoolOOMTest1(){
        // VM Args: -XX:+PermSize=10M -XX:MaxPermSize=10M
        List<String > list = new ArrayList<>();
        int i =0;
        while (true){
            list.add(String.valueOf(i++));
        }
    }
    static void constantPoolOOMTest2(){
        String str1 = new StringBuilder("Str").append("ing").toString();
        System.out.println(str1+" compare : "+str1.intern() == str1);

        try {
            Thread.sleep(1000 * 20);
        }catch (Exception e ){
            e.printStackTrace();
        }
        String str2 = new StringBuilder("ja").append("va").toString();
        System.out.println(str2 + " compare:  "+str2.intern() == str2);
    }
}
