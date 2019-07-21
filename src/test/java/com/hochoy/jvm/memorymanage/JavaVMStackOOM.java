package com.hochoy.jvm.memorymanage;

/**
 * Created by Hochoy on 2019/07/21.
 */
public class JavaVMStackOOM {
    private void dontStop(){
        while (true){}
    }
    public void stackLeakByThread(){
        while (true){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dontStop();
                }
            }).start();
        }
    }

    // -Xss128k
    // 在Windows平台的虚拟机中，java的线程是映射到操作系统的内核线程上的，
    // 因此运行此main 方法有较大的风险，可能会导致操作系统假死
//    public static void main(String[] args) {
//        JavaVMStackOOM oom = new JavaVMStackOOM();
//        oom.stackLeakByThread();
//
//    }
}
