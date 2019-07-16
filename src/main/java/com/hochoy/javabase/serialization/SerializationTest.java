package com.hochoy.javabase.serialization;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import java.util.HashMap;
import java.io.*;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/7/13
 */
public class SerializationTest {

    public static void main(String[] args) throws Exception{
//        write();

        read();

    }

    static void read () throws Exception{
        File f = new File("target/user.txt");
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
        User o = (User)ois.readObject();

        System.out.println(o);
    }

    static void write() throws Exception{
        User u = new User();
        u.setAge(20);
        u.setName("merry");
        u.setSex("M");
        u.setGpa(5f);
        System.out.println(u);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("target/user.txt"));
        oos.writeObject(u);
        oos.close();
    }

}

class User  implements Externalizable {
    private  int age;
    private String name;
    private transient String sex;
    private float gpa;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeInt(age);
        out.writeObject(sex);
        out.writeFloat(gpa);
        out.writeObject(null);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name  = (String)in.readObject();
        age = in.readInt();
        sex =(String)in.readObject();
        gpa = in.readFloat();

    }

    public User() {
    }

    public User(int age, String name, String sex, float gpa) {
        this.age = age;
        this.name = name;
        this.sex = sex;
        this.gpa = gpa;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public float getGpa() {
        return gpa;
    }

    public void setGpa(float gpa) {
        this.gpa = gpa;
    }

    @Override
    public String toString() {

        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("name",name)
                .append("age",age)
                .append("sex",sex)
                .append("gpa",gpa)
                .toString();
    }
}
