package com.hochoy.jvm.classloader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class C {

    static class OOMObject {
    }

    public static void main(String[] args) {
        String arg = args[0];
        if (arg.equals("heapSpace")){
            heapSpace();
        }else if (arg.equals("methodOOM")){
            methodOOM();
        }else if (arg.equals("stackLeak")){
            stackLeak();
        }


    }

    static void heapSpace(){
        List<OOMObject> list = new ArrayList<OOMObject>();
        while (true) {
            list.add(new OOMObject());
        }
    }

    static void methodOOM() {
        Set<String> set  = new HashSet<String>();
        short i = 0;
        while (true)
            set.add(String.valueOf(i++).intern());
    }
    static int stackLength = 1;
    static void stackLeak(){
        stackLength ++;
        stackLeak();
    }


    static void stackSof(){


    }

}

