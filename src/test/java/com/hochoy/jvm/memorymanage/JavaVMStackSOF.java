package com.hochoy.jvm.memorymanage;

/**
 * Created by Hochoy on 2019/07/21.
 *
 *
 * VM args: -Xss128k
 */


public class JavaVMStackSOF {
    private int stackLenth = 1;
    public void stackLeak(){
        stackLenth ++;
        stackLeak();
    }

    public static void main(String[] args) {
        JavaVMStackSOF oom = new JavaVMStackSOF();
        try {
            oom.stackLeak();
        }catch (Throwable e){
            System.out.println("stack length: "+oom.stackLenth);
            throw e;
        }
    }

}
