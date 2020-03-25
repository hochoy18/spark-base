package com.hochoy.leetcode.basic.algorithm.tree;

public class BinarySortTree {
    public static void main(String[] args) {
        Node root = new Node(100);
        root.add(new Node(50));
        root.add(null);
        root.add(new Node(30));
        root.add(new Node(110));
        System.out.println(root);
    }

     public static class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }

        public void add(Node node) {
            if (node == null) {
                return;
            }
            // //判断传入的节点的值比当前子树的根节点的值大还是小
            if (node.value < this.value) {
                if (this.left == null) {
                    this.left = node;
                } else {
                    this.left.add(node);
                }
            } else {
                if (this.right == null) {
                    this.right = node;
                } else {
                    this.right.add(node);
                }
            }
        }
    }
}
