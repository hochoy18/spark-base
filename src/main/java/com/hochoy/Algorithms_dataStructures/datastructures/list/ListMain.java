package com.hochoy.Algorithms_dataStructures.datastructures.list;
import java.util.ArrayList;
import java.util.HashMap;
public class ListMain {

}

class List<T >{

    private Node<T> head = new Node<>();


    public Node<T> getHead() {
        return head;
    }

    public void add(T t){

    }
    public void addSort(T t){

    }
    public void update(T t) {

    }
    public void remove(T t){

    }

    public void list(){}

    public int length(){
        return 0;
    }
    public void  reverse(){

    }

    static class Node<T> {
        public T data;
        public Node next;

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node(T data) {
            this.data = data;
        }

        public Node(Node next) {
            this.next = next;
        }

        public Node() {
        }

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + data +
                    '}';
        }
    }

}

