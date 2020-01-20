package com.hochoy.java8.hashmap.tree;

public class TreeTest {




}
class TreeNode<T>{
    T data;
    TreeNode<T> left;
    TreeNode<T> right;

    public TreeNode(T data) {
        this.data = data;
    }
}
class TreeSearch<T>{
    StringBuilder searchPath = new StringBuilder();
    private boolean isSearch = false;

    public void preOrderTraversal(TreeNode<T> root, T data){
        if (root == null){
            return;
        }
        if (!isSearch){
            if (!searchPath.toString().equals("")){
                searchPath.append("->");
            }
            searchPath.append(root.data);

            if (root.data.equals(data)){

            }
        }
    }
}




















