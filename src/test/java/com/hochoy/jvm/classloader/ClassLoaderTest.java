package com.hochoy.jvm.classloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

public class ClassLoaderTest {

    public static void main(String[] args) throws Exception {
//        File[] files = test1();
//        System.out.println(files.length);
//        test2();

        test3();
    }

    static File[] test1() {
        String property = System.getProperty("java.ext.dirs");
        System.out.println(property);
        File[] dirs;
        if (property != null) {
            StringTokenizer st =
                    new StringTokenizer(property, File.pathSeparator);
            int count = st.countTokens();
            dirs = new File[count];
            for (int i = 0; i < count; i++) {
                dirs[i] = new File(st.nextToken());
            }
        } else {
            dirs = new File[0];
        }
        return dirs;
    }

    static void test2() throws Exception {
        java.lang.ClassLoader loader = new java.lang.ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                InputStream is = getClass().getResourceAsStream(fileName);
                if (is == null) {
                    return super.loadClass(name);
                }
                try {
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return super.loadClass(name);
            }
        };

        Object o = loader.loadClass("com.hochoy.jvm.classloader.ClassLoaderTest").newInstance();
        System.out.println(o.getClass());
        System.out.println(o instanceof ClassLoaderTest);

    }

    static void test3() throws Exception {
        ClassLoader loader = ClassLoaderTest.class.getClassLoader();
        System.out.println("============================");
        System.out.println("parent.getParent    " + loader.getParent().getParent());
        System.out.println("parent    " + loader.getParent());
        System.out.println("this   " + loader);
        Object o = loader.loadClass("com.hochoy.jvm.classloader.ClassLoaderTest").newInstance();
        System.out.println(o.getClass());
        System.out.println("============================");
    }
}

class Test {
    public void say() {
        System.out.println("Hello");
    }
}

class CustomClassLoader extends ClassLoader{

    private  String classDir;
    public CustomClassLoader(String classDir){
        this.classDir = classDir;
    }
    protected CustomClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}