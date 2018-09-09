package com.yj.shopmall.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class Email {
    @Autowired
    JavaMailSender javaMailSender;
    /*
     * 发送邮件
     * */
    public void sendEmail(String userId, String email) throws MessagingException {
        //复杂邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        //邮件内容
        helper.setSubject("欢迎注册南猫鞋城");
        helper.setText("<b>请前去激活邮件</b>  <a href='http://127.0.0.1:8080/user/active.do/" + userId + "'>点击激活</a>", true);
        helper.setTo(email);
        helper.setFrom("1375668614@qq.com");
        javaMailSender.send(mimeMessage);
    }

}
