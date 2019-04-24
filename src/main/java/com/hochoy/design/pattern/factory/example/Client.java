package com.hochoy.design.pattern.factory.example;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/23
 */
public class Client {
    public static void main(String[] args) {
        try {
            String clazz = "com.hochoy.design.pattern.factory.example.mail.SendMailFactory";
            Provider  provider  = (Provider)Class.forName(clazz).newInstance();
            Sender sender = provider.produce();
            sender.send();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
