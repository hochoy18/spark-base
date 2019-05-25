package com.hochoy.jvm.classloader;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/5/16
 */
public class ClassLoaderTest {

    private static int count=0;
    public static void recursion(long a,long b,long c){
        count++;
        recursion(a,b,c);
    }

    public static void main(String[] args) {
        try{
            // 设置：-Xss2m  -Xss128k  -Xss256k   -Xss1m  等
            recursion(0L,0L,0L);
        }catch(Throwable e){

            System.out.println("deep of calling = "+count);
            e.printStackTrace();
        }
    }

}
class T1 {
    static int n = 2;
}
class T2 {
    final static int n =2;
}