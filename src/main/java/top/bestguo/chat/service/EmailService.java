package top.bestguo.chat.service;

/**
 * 邮件服务
 */
public interface EmailService {

    /**
     * 邮件发送
     *
     * @param sender 发送者
     * @param receiver 接收者
     * @param content 邮件正文
     */
    void sendEmail(String sender, String receiver, String content);

}
