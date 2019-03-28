package com.hochoy.thread;


/**
 * @author :  hochoy
 * @version :  V1.0
 * @note :  TODO
 * @since :  Date : 2018年11月15日 13:50
 */
 class MethodHandleTest {

    static {
        System.out.println("methodhandle init");
    }

    public static void println(String s) {

        System.out.println(s);
    }
}
public class ThreadTest {

    public static void main(String[] args) {
//        Thread.currentThread.getStackTrace()
//        test1();
    }
//     void test1(){
//         try {
////			MethodHandle mh = MethodHandles.lookup().findStatic(MethodHandleTest.class, "println", mt);
////			mh.invoke("ss");
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
    static void test(){
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : stes) {
            System.out.println(ste.toString());
        }
    }
    static void test1(){

    }
}