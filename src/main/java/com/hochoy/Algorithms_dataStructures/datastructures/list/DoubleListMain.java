package com.hochoy.Algorithms_dataStructures.datastructures.list;

public class DoubleListMain {
}


class DList<T> {

    DNode<T> first;

    public DList( T t) {
        first = new DNode<>(t);

    }

    static class DNode<T> {
        T data;
        T pre;
        T next;

        public DNode(T data) {
            this.data = data;
        }
    }
}