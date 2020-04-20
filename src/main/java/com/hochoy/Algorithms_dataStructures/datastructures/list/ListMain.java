package com.hochoy.Algorithms_dataStructures.datastructures.list;

import java.util.ArrayList;
import java.util.HashMap;

public class ListMain {
    public static void main(String[] args) {

        List.Node node1 = new List.Node(1, "tangseng", "TS");

        List.Node node2 = new List.Node(2, "sunwukong", "SWK");

        List.Node node3 = new List.Node(3, "zhubajie", "zbj");

        List.Node node4 = new List.Node(4, "shaseng", "SS");
        List l = new List();
//        l.add(node1);
//        l.add(node2);
//        l.add(node3);
//        l.add(node4);
//        l.list();


        l.addSort(node1);
        l.addSort(node3);
        l.addSort(node4);
        l.addSort(node2);
        l.list();
        System.out.println("add end ................................");
        List.Node node22 = new List.Node(3, "sunwukong----", "SWK");

        l.remove(node22);

        System.out.println(l.length());

        l.list();

    }

}

class List {

    private final Node head;

    public List() {
        head = new Node(0, "", "");
    }

    public Node getHead() {
        return head;
    }

    public void add(Node t) {
        Node temp = head;


        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = t;


        //        while (true) {
//            if (temp.next == null) {
//                temp.next = t;
//                break;
//            }
//            temp = temp.next;
//        }
    }

    public void addSort(Node t) {

        Node temp = head;

        while (true) {
            if (temp.next == null) {
                temp.next = t;
                break;
            }
//            if (temp.next.id == t.id ){
//                System.out.printf("------------duplicate element %s %n",t);
//                break;
//            }
            if (temp.next.id > t.id) {
                t.next = temp.next;
                temp.next = t;
                break;
            }
            temp = temp.next;
        }


//        while (temp.next.id > t.id){
//
//        }

    }

    public void update(Node t) {
        Node temp = head;
        while (true) {
            if (temp.next == null) {
                break;
            }
            if (temp.next.id == t.id) {
                if (temp.next.next != null) {
                    t.next = temp.next.next;
                }
                temp.next = t;
                break;
            }

            temp = temp.next;
        }

    }

    public void remove(Node t) {
        Node temp = head;

        while (true) {
            if (temp.next == null)
                break;
            if (temp.next.id == t.id) {
                Node last = null;
                if (temp.next.next != null) {
                    last = temp.next.next;
                }
                temp.next = last;
                break;
            }
            temp = temp.next;

        }

    }

    public void list() {

        if (head.next == null) {
            System.err.println("list is empty");
            return;
        }

        Node temp = head;
        while (true) {
            if (temp.next == null) {
                break;
            }
            System.out.println(temp.next);
            temp = temp.next;
        }

    }

    public int length() {
        if (head.next == null)
            return 0;
        int len = 0;
        Node temp = head;
        while (temp.next != null){
            len ++;
            temp = temp.next;
        }

        return len;
    }

//    public void reverse() {
//        if (head.next != null){
//            Node tmpHead = new Node(0,"","");
//            Node temp = head;
//            while (true){
//                if (temp.next != null){
//
//                    tmpHead.next = temp.next;
//                }
//                temp = temp.next;
//
//            }
//
//        }
//
//    }


    static class Node {
        public int id;
        public String name;
        public String nickName;
        public Node next;

        public Node(int id, String name, String nickName) {
            this.id = id;
            this.name = name;
            this.nickName = nickName;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", nickName='" + nickName + '\'' +
//                    ", next=" + next +
                    '}';
        }
    }

//    static class Node<T> {
//        public T data;
//        public Node next;
//
//        public Node getNext() {
//            return next;
//        }
//
//        public void setNext(Node next) {
//            this.next = next;
//        }
//
//        public Node(T data) {
//            this.data = data;
//        }
//
//        public Node(Node next) {
//            this.next = next;
//        }
//
//        public Node() {
//        }
//
//        @Override
//        public String toString() {
//            return "Node{" +
//                    "data=" + data +
//                    '}';
//        }
//    }

}

