package com.hochoy.jvm.memorymanage.reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/07/31
 */
public class ReferenceTest1 {

    public static void main(String[] args) {
        test1();
    }

    static void teste2() {
        MyObject aMyObject = new MyObject(10002);
        ReferenceQueue queue = new ReferenceQueue();
        SoftReference ref = new SoftReference(aMyObject, queue);

        ref = null;
        while ((ref = (SoftReference) queue.poll()) != null) {
            ref.clear();
        }
    }

    static void test1() {

        MyObject aRef = new MyObject(10001);
        SoftReference aSR = new SoftReference(aRef);
        aRef = null;

        MyObject o = (MyObject) aSR.get();
        System.out.println(o == null);
        System.out.println(o);
//        ReferenceQueue queue = new ReferenceQueue();
//        SoftReference ref = new SoftReference(aRef,queue);
//
//
//        SoftReference r = null;
//        while ((r = (SoftReference)queue.poll()) != null){
//            r.clear();
//        }
    }
}


class MyObject {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MyObject(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MyObject{" +
                "id=" + id +
                '}';
    }
}


class Employee {
    private String id;// 雇员的标识号码
    private String name;// 雇员姓名
    private String department;// 该雇员所在部门
    private String Phone;// 该雇员联系电话
    private int salary;// 该雇员薪资
    private String origin;// 该雇员信息的来源

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Employee(String id) {
        this.id = id;
        getDataFromlnfoCenter();
    }

    // 到数据库中取得雇员信息
    private void getDataFromlnfoCenter() {
// 和数据库建立连接井查询该雇员的信息，将查询结果赋值
// 给name，department，plone，salary等变量
// 同时将origin赋值为"From DataBase"
        setName("hochoy");
        setDepartment("tech");
        setPhone("188xxxxxx");
        setSalary(10000);
        setOrigin("from nanjing ");
    }
}

class EmployeeCache {
    static private EmployeeCache cache;
    private Hashtable<String, EmployeeRef> employeeRefs;// 用于Chche内容的存储
    private ReferenceQueue<Employee> q;// 垃圾Reference的队列

    private class EmployeeRef extends SoftReference<Employee> {
        private String _key = "";

        public EmployeeRef(Employee em, ReferenceQueue<Employee> q) {
            super(em, q);
            _key = em.getId();
        }
    }

    private EmployeeCache() {
        employeeRefs = new Hashtable<String, EmployeeRef>();
        q = new ReferenceQueue<Employee>();
    }

    // 取得缓存器实例
    public static EmployeeCache getInstance() {
        if (cache == null) {
            cache = new EmployeeCache();
        }
        return cache;
    }

    // 以软引用的方式对一个Employee对象的实例进行引用并保存该引用
    private void cacheEmployee(Employee em) {
        cleanCache();// 清除垃圾引用
        EmployeeRef ref = new EmployeeRef(em, q);
        employeeRefs.put(em.getId(), ref);
    }

    // 依据所指定的ID号，重新获取相应Employee对象的实例
    public Employee getEmployee(String ID) {
        Employee em = null;
// 缓存中是否有该Employee实例的软引用，如果有，从软引用中取得。
        if (employeeRefs.containsKey(ID)) {
            EmployeeRef ref = (EmployeeRef) employeeRefs.get(ID);
            em = (Employee) ref.get();
        }
// 如果没有软引用，或者从软引用中得到的实例是null，重新构建一个实例，
// 并保存对这个新建实例的软引用
        if (em == null) {
            em = new Employee(ID);
            System.out.println("Retrieve From EmployeeInfoCenter. ID=" + ID);
            this.cacheEmployee(em);
        }
        return em;
    }

    // 清除那些所软引用的Employee对象已经被回收的EmployeeRef对象
    private void cleanCache() {
        EmployeeRef ref = null;
        while ((ref = (EmployeeRef) q.poll()) != null) {
            employeeRefs.remove(ref._key);
        }
    }

    // 清除Cache内的全部内容
    public void clearCache() {
        cleanCache();
        employeeRefs.clear();
        System.gc();
        System.runFinalization();
    }

}



class tttt{

    private HashMap<String, SoftReference<Set<String>>> gidSet;


}