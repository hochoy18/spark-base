package com.hochoy.Algorithms_dataStructures.datastructures.linkedlist;

import java.util.Stack;

public class LinkedList_Test {

    public static void main(String[] args) {

//        update();
//        addBySort();
//        findLastKIndex();
        reverse();
    }

    static void reverse(){
        System.out.println("-----------");
        LinkedList list = new LinkedList();
        list.addBySort(new Node(4));
        list.addBySort(new Node(2));
        list.addBySort(new Node(1));
        list.addBySort(new Node(3));
        list.list();
        list.reverse();
        list.list();
    }

    static void addBySort(){
        System.out.println("-----------");
        LinkedList list = new LinkedList();
        list.list();
        list.addBySort(new Node(4));
        list.list();
        list.addBySort(new Node(2));
        list.list();
        list.addBySort(new Node(1));
        list.list();
        list.addBySort(new Node(3));
        list.list();
        list.addBySort(new Node(3));
        list.list();

    }

    static void findLastKIndex(){
        System.out.println("-----------");
        LinkedList list = new LinkedList();
        list.addBySort(new Node(4));
        list.addBySort(new Node(2));
        list.addBySort(new Node(1));
        list.addBySort(new Node(3));
        list.addBySort(new Node(3));
        list.list();
        Node lastKIndex = list.findLastKIndex(3);
        System.out.println(lastKIndex);
        lastKIndex = list.findLastKIndex(2);
        System.out.println(lastKIndex);
        lastKIndex = list.findLastKIndex(1);
        System.out.println(lastKIndex);
        lastKIndex = list.findLastKIndex(0);
        System.out.println(lastKIndex);


    }

    static void update(){
        System.out.println("-----------");
        LinkedList list = new LinkedList();
        list.addNodeToTail(new Node(1));
        list.addNodeToTail(new Node(2));
        list.addNodeToTail(new Node(3));
        list.addNodeToTail(new Node(4));
        list.list();

        list.update(5,new Node(9));
        list.list();
        list.update(4,new Node(9));
        list.list();
        System.out.println(list.size());

    }
    static void testAdd(){
        System.out.println("-----------");
        Node n1 = new Node(1);
        LinkedList list = new LinkedList();
        list.addNodeToTail(n1);
        System.out.println(list.size());
        list.addNodeToTail(new Node(2));

        list.addNodeToTail(new Node(3));
        list.addNodeToTail(new Node(4));
        list.list();

        list.delete(3);
        list.list();

        list.delete(1);
        list.list();

        list.addNodeToTail(new Node(5));
        list.list();

        list.delete(5);
        list.list();

    }


}

class Node{
    int val;
    Node next;

    public Node(int val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "Node{" + "val=" + val + '}';
    }
}

class LinkedList{
    private Node head ;

    public LinkedList() {
        this.head = new Node(0);
    }
    public void  addNodeToTail(Node node){
        Node tmp = head;
        while (true){
            if (tmp.next == null){
                break;
            }
            tmp = tmp.next;
        }
        tmp.next = node;
    }

    public void delete(int id){
        if (head.next == null){
            System.out.println(" the linked list is empty");
            return;
        }
        Node tmp = head;



        while (true){
            if (tmp.next.val == id){
                break;
            }
            tmp = tmp.next;
        }

        tmp.next = tmp.next.next;

    }

    public void update(int id ,Node node ){
        if (head.next == null){
            System.out.println(" the linked list is empty");
            return;
        }
        Node tmp = head;
        boolean flag = false;
        while (true){
            if (tmp.next == null)// 遍历完整个 链表
                break;
            if (tmp.next.val == id){
                flag = true;
                break;
            }
            tmp = tmp.next;

        }

        if (flag){
            node.next = tmp.next.next;
            tmp.next = node;
        }else
            System.out.printf("Node{val=%s} not exists,could not update %n" , id);

    }

    public void addBySort(Node node){
        Node tmp = head;
        boolean flag = false;
        while (true){
            if (tmp.next == null){
                break;// 遍历完 退出
            }
            if (tmp.next.val > node.val){
                break;
            }else if (tmp.next.val == node.val){
                flag =true;
                break;
            }
            tmp = tmp.next;
        }
        if (flag)
            System.out.printf("Node{val=%s} existed %n",node.val);
        else {
            node.next = tmp.next;
            tmp.next = node;
        }

    }

    // 查找倒数第 k 个节点

    public Node findLastKIndex(int index ){
        Node head = getHead();
        if (head.next ==null)
            return null;

        Node tmp = head.next;
        int size = size();
        if (size < index || index < 0)
            return null;
        int k = size - index - 1;
        int i = 0;
        while ( true ){
            if ( i == k)
                break;
            tmp = tmp.next;
            i++;
        }
        return tmp;

    }

    void reverse(){
//        Node head = getHead();
        if (head.next == null)
            return;
        Node tmp = head.next;
        Stack<Node> stack = new Stack<>();
        while (true){
            if (tmp == null )
                break;
            stack.push(tmp);
            tmp = tmp.next;
        }
        head.next = null;
//        tmp.next = null;
        for (int i = 0; i < stack.size(); i++) {
            addNodeToTail(stack.pop());
        }
    }



    public Node getHead(){
        return head;
    }


    public void list(){
        if (head.next == null){
            System.out.println("the node is empty");
            return;
        }
        Node tmp = head.next;
        while (true){
            if (tmp == null)
                break;
            System.out.printf( "%s -> " ,tmp.toString());
            tmp = tmp.next;
        }
        System.out.println();
    }

    public int  size(){
        if (head.next == null)
            return 0 ;
        Node tmp = head.next;
        int size = 0;
        while (true){
            if (tmp == null)
                break;
            size ++;
            tmp = tmp.next;
        }
        return size;
    }


}