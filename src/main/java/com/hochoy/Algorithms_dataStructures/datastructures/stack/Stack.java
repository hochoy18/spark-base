package com.hochoy.Algorithms_dataStructures.datastructures.stack;

import java.util.Iterator;

public class Stack<T> implements Iterable<Object> {

    private int size ;
    Node<T> head;

    public Stack() {
        this.head = new Node<>(null);
        this.size = 0;
    }

    public void push(T t ){
        Node<T> node = new Node<>(t);
        node.next = head.next;
        head.next = node;
        size ++;
    }
    public T pop(){
        if (head.next  == null ){
            return null;
        }
        Node<T> next = head.next;
        head.next = head.next.next;
        size --;
        return next.value;
    }

    public int size(){
        return size;
    }

    @Override
    public Iterator<Object> iterator() {
        StackIterator stackIterator = new StackIterator();
        return stackIterator;
    }

    private class Node<E> {
        E value;
        Node<E> next;

        Node(E value) {
            this.value = value;
        }
    }

    private class StackIterator implements Iterator{

        private Node<T> n;

        public StackIterator() {
            this.n = head;
        }

        @Override
        public boolean hasNext() {
            return n.next != null;
        }

        @Override
        public Object next() {
            n = n.next;
            return n.value;
        }
    }


}
