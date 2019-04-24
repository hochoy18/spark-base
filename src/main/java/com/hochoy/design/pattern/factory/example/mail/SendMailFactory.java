package com.hochoy.design.pattern.factory.example.mail;

import com.hochoy.design.pattern.factory.example.Provider;
import com.hochoy.design.pattern.factory.example.Sender;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/23
 */
public class SendMailFactory implements Provider {
    @Override
    public Sender produce() {
        return new MailSender();
    }
}
