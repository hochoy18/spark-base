package com.hochoy.Algorithms_dataStructures.datastructures.tree;


public class Node<Key, Value> {
    Key key;
    Value value;
    Node<Key, Value> left;
    Node<Key, Value> right;

    public Node(Key key, Value value) {
        this.key = key;
        this.value = value;
    }
}
