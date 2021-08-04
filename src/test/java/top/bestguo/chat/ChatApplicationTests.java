package top.bestguo.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.bestguo.chat.service.EmailService;

@SpringBootTest
class ChatApplicationTests {

    @Autowired
    private EmailService emailService;

    @Test
    void contextLoads() {
        emailService.sendEmail("bestguo2020@foxmail.com", "bestguo10456@qq.com", "邮件测试。。。。。。");
    }

}
