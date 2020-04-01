package com.hochoy.stringtest;

public class MyArrayList<T> {

    private int AMOUNT = 10;
    private int index ;
    private int length ;

    T [] value ;

    public void init(){
        value = (T[]) new Object[AMOUNT];
        index = 0;
        length = AMOUNT;
    }

    public MyArrayList() {
        init();
    }


    public int length(){
        return length;
    }

    public void add(int i ,T t){
        if (i<0 | i> length){
            throw new ArrayIndexOutOfBoundsException();
        }
        if (i == length){
            resize();
        }
        for (int k = index; k >i; k --) {
            value[k] = value[k-1];
        }
        value[i] = t;
        index ++;
    }
    public void add(T t){
        add(index,t);
    }
    public int find(T t){
        for (int i = 0; i < index; i++) {
            if (t.equals(value[i]))
                return i + 1;
        }
        return -1;
    }


//    public boolean isEmpty(){
//
//    }

    public void resize(){
        T[] tmp = (T[]) new Object[length() * 2 + 1];
        for (int i = 0; i < length; i++) {
            tmp[i] = value[i];
        }
        value = tmp;

    }
    public T get(int i ){
        if (i < 0 || i > length)
            throw new ArrayIndexOutOfBoundsException();
        return value[i];
    }

    public static void main(String[] args) {
        MyArrayList list = new MyArrayList();
        list.add(1,1);
        list.add(3,1);
        list.add(5,1);
        list.add(0,1);

        Object o = list.get(9);
        System.out.println(o);
        Object o1 = list.get(5);
        System.out.println(o1);

    }
}
