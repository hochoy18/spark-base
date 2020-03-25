package com.hochoy.leetcode.basic.algorithm.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeUtil {

    public <T extends TreeNode> List<T> build(List<T> treeNodes,Object root){
        ArrayList<T> tree = new ArrayList<>();
        for (T treeNode : treeNodes) {
            if (root.equals(treeNode.getParentid())){
                tree.add(treeNode);
            }
            for (T node : treeNodes) {
                if (node.getParentid() == treeNode.getId()){
                    if (treeNode.getChildren() == null){
                        treeNode.setChildren(new ArrayList<>());
                    }
                    treeNode.add(node);
                }
            }
        }
        return tree;
    }

    public <T extends TreeNode> T findChildren(T treeNode,List<T> treeNodes){
        for (T node : treeNodes) {
            if (treeNode.getId() == node.getParentid()){
                if (treeNode.getChildren() == null){
                    treeNode.setChildren(new ArrayList<>());
                }
                treeNode.add(findChildren(node,treeNodes));
            }
        }
        return treeNode;

    }


}

class TreeNode {

    private int id;
    private int parentid;
    private String name;
    private List<TreeNode> children = new ArrayList<>();

    public void add(TreeNode node) {
        if (node != null) {
            children.add(node);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }
}
