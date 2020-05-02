package com.hochoy.java8;

import java.io.*;

public class TransientTest {

    static String path =   System.getProperty("user.dir") + File.separator + "target" + File.separator + "user.txt";

    public static void main(String[] args) {

        User user = new User();
        user.setUsername("Alexia");
        user.setPassword("123456");
        User.setSex("male");

        System.out.println("read before Serializable: ");
        System.out.println("username: " + user.getUsername());
        System.err.println("password: " + user.getPassword());
        System.err.println("sex: " + User.getSex());

        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
            os.writeObject(user);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            user = (User) in.readObject();
            in.close();
            System.out.println("\nread after Serializable: ");
            System.out.println("username: " + user.getUsername());
            System.err.println("password: " + user.getPassword());
            System.err.println("sex: " + User.getSex());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}


class User implements Serializable {

    private static final long serialVersionUID = 8294180014912103005L;
    private byte[] size = new byte[1024 * 1024 * 10];

    private String username;
    /**
     * test key word transient
     */
    private transient String password;
    private static String sex;

    public static String getSex() {
        return sex;
    }

    public static void setSex(String sex) {
        User.sex = sex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}