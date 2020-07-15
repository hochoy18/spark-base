package com.hochoy.jvm.classloader;

public class C {
    public static void main(String[] args) {
        try {
            Class.forName("com.hochoy.jvm.classloader.B");
        } catch (ClassNotFoundException e) {
        }
    }
}

