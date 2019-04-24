package com.hochoy.design.pattern.factory.example.mail;

import com.hochoy.design.pattern.factory.example.Sender;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/23
 */
public class MailSender implements Sender {
    @Override
    public void send() {
        System.out.println("com.hochoy.design.pattern.factory.mail.MailSender .........");
    }
}
