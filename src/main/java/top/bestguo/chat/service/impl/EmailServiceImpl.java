package top.bestguo.chat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import top.bestguo.chat.service.EmailService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮件服务实现类
 */
@Service("emailService")
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Override
    public void sendEmail(String sender, String receiver, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(receiver);
        message.setSubject("欢迎使用“小聊”");
        message.setText(content);
        // 发送邮件
        mailSender.send(message);
    }
}
