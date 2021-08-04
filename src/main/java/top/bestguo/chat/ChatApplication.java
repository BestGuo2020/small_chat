package top.bestguo.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("top.bestguo.chat.mapper")
public class ChatApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ChatApplication.class, args);
        System.out.println(run.containsBean("mainEndpoint"));
    }

}
