package com.hochoy.concurrent;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConcurrentModificationExceptionTest {

    public static void main(String[] args) {
        test2();
//        arrayListTest();
    }
    static void arrayListTest(){
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()){
            Integer i = it.next();
            if (i == 5 ){
                System.out.println("i    "+i);
                /**
                 * 会抛 ConcurrentModificationException 异常
                 */
                //list.remove(i);
                /**
                 *  1 使用Iterator提供的remove方法，用于删除当前元素
                 */
                it.remove();
            }
            System.out.println("i is ... " +i);
        }
        System.out.println(list);


        // 2 建一个集合，记录需要删除的元素，之后统一删除
        // 可以查看removeAll源码，其中使用Iterator进行遍历
        List<Integer> tmpList = new ArrayList<Integer>();
        for (Integer i : list ){
            if (i % 2 == 1 ){
                tmpList.add(i);
            }
        }
        list.removeAll(tmpList);
        System.out.println("........... "+list);

        // 3. 使用线程安全CopyOnWriteArrayList进行删除操作
        List<Integer> l = new CopyOnWriteArrayList<Integer>();
        l.add(1);
        l.add(2);
        l.add(3);
        l.add(8);
        l.add(4);
        l.add(5);
        Iterator<Integer> iterator = l.iterator();
        while (iterator.hasNext()){
            Integer ii = iterator.next();
            if (ii == 8){
                l.remove(ii);
                l.add(9);
            }
        }
        System. out.println( "List Value:" + l.toString());

        // 4. 不使用Iterator进行遍历，需要注意的是自己保证索引正常
        for ( int i = 0; i < list.size(); i++) {
            Integer value = list.get(i);
            System. out.println( ".............List Value:" + value);
            if (value==2) {
                list.remove(value);  // ok
                i--; // 因为位置发生改变，所以必须修改i的位置
            }
        }
        System. out.println( "List Value:" + list.toString());

    }
    static void test2(){
        final List<Integer> list = new CopyOnWriteArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        Thread t1 = new Thread("t1...."){
            @Override
            public void run() {
                Iterator<Integer> it = list.iterator();
                while (it.hasNext()){
                    Integer i = it.next();
                    System.out.println(this.getName()  +i);
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e ){
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread t2= new Thread("t2...."){
            @Override
            public void run() {
                 Iterator<Integer> it = list.iterator();
                while (it.hasNext()){
                    Integer i = it.next();
                    if (i == 3){
                        it.remove();
                    }
                }
            }
        };

        t1.start();
        t2.start();
    }
}

