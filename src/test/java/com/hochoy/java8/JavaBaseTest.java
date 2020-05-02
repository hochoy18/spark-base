package com.hochoy.java8;

import com.hochoy.utils.HochoyUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JavaBaseTest {



    @Test
    public void testEqual(){
        int i = 10;
        float f = 10.0f;
        double d = 10.0;
        System.out.println(i == f);
        System.out.println(i == d);
    }


    @Test
    public void refTest(){
        List<String> list = new ArrayList<>();
        String str = "hello world";
        list.add(str);
        str = null;
        List<User> users = new ArrayList<>();
        int i= 0;

        while (i < 90){
            User u = new User();
            u.setPassword("123456__"+i);
            u.setUsername("zhangsan=="+i);
            users.add(u);
            i++;
            u = null;
            HochoyUtils.sleep(3000);
            System.out.println(users.size());
        }
        HochoyUtils.sleep(1000 * 300);
        System.out.println(users);

        System.out.println(list);
    }


}
