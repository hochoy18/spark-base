package com.hochoy.Algorithms_dataStructures.sort;

import com.hochoy.Algorithms_dataStructures.datastructures.tree.TreeNode;
import com.hochoy.Algorithms_dataStructures.datastructures.tree.TreeNodeDemo;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

public class TreeNodeDemoTest {
    TreeNodeDemo treeNodeDemo = new TreeNodeDemo();
    @Test
    public void preOrder(){
        TreeNode root = new TreeNode(5);

        TreeNode treeNode = new TreeNode(4);
        treeNode.left = new TreeNode(1);
        treeNode.right = new TreeNode(2);
        root.left = treeNode;

        TreeNode treeNode1 = new TreeNode(8);
        treeNode1.left = new TreeNode(6);
        treeNode1.right = new TreeNode(7);
        root.right = treeNode1;

        TreeNodeDemo.preOrder(root); // 5 4 8 1 2 6 7
    }

    @Test
    public void createTreeNode(){
        LinkedList<Integer> list = new LinkedList< >(Arrays.asList(9,6,4,8,3,7,2,10,25,7));

        TreeNode treeNode = TreeNodeDemo.createTreeNode(list);
        System.out.println(treeNode);
    }


    @Test
    public void  wideFirst(){

        TreeNode root = genTree();
        treeNodeDemo.wideFirst(root); // 5 [4 8] [1 2 6 7]
    }

    TreeNode genTree(){
        TreeNode root = new TreeNode(5);

        TreeNode treeNode = new TreeNode(4);
        treeNode.left = new TreeNode(1);
        treeNode.right = new TreeNode(2);
        root.left = treeNode;

        TreeNode treeNode1 = new TreeNode(8);
        treeNode1.left = new TreeNode(6);
        treeNode1.right = new TreeNode(7);
        root.right = treeNode1;
        return root;
    }
}
