package com.hochoy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class HochoyUtils {
    private HochoyUtils() {
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties(String fileName) {
        Properties prop = new Properties();
        InputStream in;
        try {
            in = HochoyUtils.class.getClassLoader().getResourceAsStream(fileName);

            prop.load(in);
            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

}

