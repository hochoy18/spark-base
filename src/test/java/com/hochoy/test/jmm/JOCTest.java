package com.hochoy.test.jmm;

import org.openjdk.jol.info.ClassLayout;

public class JOCTest {
    public static void main(String[] args) {
        //  -XX:InitialHeapSize=131726912 -XX:MaxHeapSize=2107630592 -XX:+PrintCommandLineFlags -XX:-UseCompressedClassPointers -XX:-UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseParallelGC
        Object o = new User(1,
                "-XX:InitialHeapSize=131726912 -XX:MaxHeapSize=2107630592 -XX:+PrintCommandLineFlags -XX:-UseCompressedClassPointers -XX:-UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseParallelGC",
                8,"female");
//        Object o = new Object();

        System.out.println(ClassLayout.parseInstance(o).toPrintable());
        System.out.println("============================================");
        synchronized (o){
            System.out.println(ClassLayout.parseInstance(o).toPrintable());
        }

    }
}
class User {
    private  int id;
    private String name;
    private int age;
    private String gender;

    public User(int id, String name, int age, String gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }
}

/**

 com.hochoy.test.jmm.User object internals:
 OFFSET  SIZE               TYPE DESCRIPTION                               VALUE
 0     4                    (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
 4     4                    (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 8     4                    (object header)                           43 c1 00 20 (01000011 11000001 00000000 00100000) (536920387)
 12     4                int User.id                                   1
 16     4                int User.age                                  8
 20     4   java.lang.String User.name                                 (object)
 24     4   java.lang.String User.gender                               (object)
 28     4                    (loss due to the next object alignment)
 Instance size: 32 bytes
 Space losses: 0 bytes internal + 4 bytes external = 4 bytes total

 ============================================
 com.hochoy.test.jmm.User object internals:
 OFFSET  SIZE               TYPE DESCRIPTION                               VALUE
 0     4                    (object header)                           08 f1 a9 02 (00001000 11110001 10101001 00000010) (44691720)
 4     4                    (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 8     4                    (object header)                           43 c1 00 20 (01000011 11000001 00000000 00100000) (536920387)
 12     4                int User.id                                   1
 16     4                int User.age                                  8
 20     4   java.lang.String User.name                                 (object)
 24     4   java.lang.String User.gender                               (object)
 28     4                    (loss due to the next object alignment)
 Instance size: 32 bytes
 Space losses: 0 bytes internal + 4 bytes external = 4 bytes total


 */
