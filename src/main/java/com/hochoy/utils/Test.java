package com.wbkit.cobub.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Test {

    public static void main(String[] args) {

        Apple a1 = new Apple(1,12.1);
        Apple a2 = new Apple(2,12.2);
        Apple a3 = new Apple(3,12.0);
        List<Apple> l = new ArrayList<Apple>();
        l.add(a1);l.add(a2);l.add(a3);

        System.out.printf("this list of apples : %s\n",l);
        Collections.sort(l);
        System.out.printf("collections.sort: %s\n",l);
        Collections.sort(l,new AESComparator());
        System.out.println("..............");
        System.out.printf("this list of apples : %s\n",l);
        Collections.sort(l,new DESComparator());
        System.out.printf("this list of apples : %s\n",l);



        System.exit(-1);

        Domain d1 = new Domain("abc");
        Domain d2 = new Domain("abd");
        Domain d3 = new Domain("abe");
        Domain d4 = new Domain("abd");
        System.out.println(d1.compareTo(d2));
        System.out.println(d2.compareTo(d3));
        System.out.println(d2.compareTo(d4));
        System.out.println("=====================");
        D d = new D();
        System.out.println(d.compare(d1, d3));
        System.out.println(d.compare(d2, d4));

    }

}

class Domain implements Comparable<Domain> {

    private String str;

    private Integer age;
    private Double aDouble;
    private int a;

    public Domain(String str) {
        this.str = str;
    }

    @Override
    public int compareTo(Domain o) {
        if (this.str.compareTo(o.str) > 0) {
            return 1;
        } else if (this.str.compareTo(o.str) < 0) {
            return -1;
        } else
            return 0;
    }

    public String getStr() {
        return str;
    }
}

class D implements Comparator<Domain> {

    @Override
    public int compare(Domain o1, Domain o2) {
        if (o1.getStr().compareTo(o2.getStr()) == 0) return 0;
        else return o1.getStr().compareTo(o2.getStr()) > 0 ? 1 : -1;
    }
}

class Apple implements Comparable<Apple> {
    int id;
    double price;

    public Apple(int id, double price) {
        this.id = id;
        this.price = price;
    }

    @Override
    public int compareTo(Apple that) {
        double d = this.price - that.price;
        return d < 0.001 ? 0 : (-d > 0 ? 1 : -1);
    }

    @Override
    public String toString() {
        return "Apple{" +
                "id=" + id +
                ", price=" + price +
                '}';
    }
}

class AESComparator implements Comparator<Apple> {
    @Override
    public int compare(Apple o1, Apple o2) {
        double d = o1.price - o2.price;
        if (Math.abs(d) < 0.001) return 0;
        else return d > 0 ? 1 : -1;
    }
}

class DESComparator implements Comparator<Apple> {
    @Override
    public int compare(Apple o1, Apple o2) {
        double d = o1.price - o2.price;
        if (Math.abs(d) < 0.001) return 0;
        else return d > 0 ? -1 : 1;
    }
}