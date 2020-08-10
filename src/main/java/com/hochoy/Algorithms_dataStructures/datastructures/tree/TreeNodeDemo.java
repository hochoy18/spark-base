package com.hochoy.Algorithms_dataStructures.datastructures.tree;


import java.util.*;

public class TreeNodeDemo {


    public static void preOrder(TreeNode root) {
        if (root == null)
            return;
        System.out.println(root.value);
        preOrder(root.left);
        preOrder(root.right);
    }


    public static TreeNode createTreeNode(LinkedList<Integer> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        Integer integer = list.removeFirst();
        TreeNode node = new TreeNode(integer);
        node.left = createTreeNode(list);
        node.right = createTreeNode(list);

        return node;
    }




    public void wideFirst(TreeNode node) {
        if (node == null) {
            return;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(node);

        while (!queue.isEmpty()) {

            TreeNode poll = queue.poll();
            int value = poll.value;
            System.out.println(value);
            if (poll.left != null)
                queue.offer(poll.left);
            if (poll.right != null)
                queue.offer(poll.right);
        }
    }
}
