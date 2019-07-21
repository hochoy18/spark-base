package com.hochoy.jvm.memorymanage;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

/**
 * https://blog.csdn.net/rickyit/article/details/53895060
 *
 * VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 * VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:SurvivorRatio=8
 * VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError -XX:SurvivorRatio=8
 * VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails
 *
 * -Xms${size} :设置初始java 堆大小；是指设定程序启动时占用内存大小。一般来讲，大点，程序会启动的快一点，但是也可能会导致机器短时间变慢
 *      也是最小堆的大小，它等价于：-XX:InitialHeapSize
 * -Xmx${size} :设置最大java 堆大小；是指设定程序运行期间最大可占用的内存大小。如果程序运行需要占用更多的内存，超出了这个设置值，就会抛出OutOfMemoryError异常。
 *      它等价于-XX:MaxHeapSize。
 * -Xss${size} :设置java线程堆栈大小; 是指设定每个线程的堆栈大小。这个就要依据你的程序，看一个线程大约需要占用多少内存，可能会有多少线程同时运行等。
 *
 * 以上三个参数的设置都是默认以Byte为单位的，也可以在数字后面添加[k/K]或者[m/M]来表示KB或者MB。
 * 而且，超过机器本身的内存大小也是不可以的，否则就等着机器变慢而不是程序变慢了。
 *
 * -Xms 为jvm启动时分配的内存，比如-Xms200m，表示分配200M
 * -Xmx 为jvm运行过程中分配的最大内存，比如-Xms500m，表示jvm进程最多只能够占用500M内存
 * -Xss 为jvm启动的每个线程分配的内存大小，默认JDK1.4中是256K，JDK1.5+中是1M
 *
 *
 *
 * -XX:+HeapDumpOnOutOfMemoryError:导出内存溢出的堆信息(hprof文件)
 *      设置了该参数，JVM 就会在发生内存泄露时抓拍下当时的内存状态，也就是我们想要的堆转储文件。
 *       jhat java_pid${java_pid}.hprof
 *
 * -XX:+PrintGCDetails ：打印GC详细信息 （GC详细信息简单说明：https://blog.csdn.net/yuanchongtian/article/details/80975029）
 * -XX:SurvivorRatio : 它定义了新生代中Eden区域和Survivor区域（From幸存区或To幸存区）的比例，默认为8，也就是说Eden占新生代的8/10，From幸存区和To幸存区各占新生代的1/10
 * -XX:+PrintFlagsFinal：表示打印出XX选项在运行程序时生效的值。
 * -XX:+PrintFlagsInitial：表示打印出所有XX选项的默认值
 *
 * Created by Hochoy on 2019/07/20.
 */
public class HeapOOM {
    static class OOMObject {
    }

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<>();
        while (true) {
            list.add(new OOMObject());
        }
    }
}


