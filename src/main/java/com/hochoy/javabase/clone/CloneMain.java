package com.hochoy.javabase.clone;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/7/15
 */
public class CloneMain {

    public static void main(String[] args) throws Exception{
        test2();
    }

    static  void test2() throws Exception{
        Student s = new Student("zhangsan",20,"F");
        Teacher teacher = new Teacher();
        teacher.setName("Mr.zhang");
        teacher.setStudent(s);
        Teacher clone = teacher.clone();
        Student s2 = clone.getStudent();
        s2.setAge(1000);
        s2.setName("lisi");
        s2.setSex("MM");
        System.out.println(teacher);
        System.out.println(clone);
    }

    static void  test1() throws Exception{
        Student s = new Student("zhangsan",20,"F");
        Student clone = s.clone();
        System.out.println(s);
        System.out.println(clone);

    }
}
