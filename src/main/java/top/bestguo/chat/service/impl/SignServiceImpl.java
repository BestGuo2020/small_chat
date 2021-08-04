package top.bestguo.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import top.bestguo.chat.entity.User;
import top.bestguo.chat.mapper.UserMapper;
import top.bestguo.chat.msg.Message;
import top.bestguo.chat.service.EmailService;
import top.bestguo.chat.service.SignService;

import javax.servlet.http.HttpSession;

/**
 * 登录注册服务实现类
 */
@Service
public class SignServiceImpl implements SignService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmailService emailService;
    @Value("${spring.mail.username}")
    private String username;

    @Override
    public Message signin(User user, HttpSession httpSession) {
        Message message = new Message();
        // 查询该用户是否存在
        User user1 = userMapper.selectOne(new QueryWrapper<User>()
                .eq("userId", user.getUserId())
                .eq("password", user.getPassword()));
        if(user1 != null) {
            message.setStatus(Message.OK);
            message.setMsg("登录成功");
            // 保存 session 信息
            httpSession.setAttribute("user", user1);
        } else {
            message.setStatus(Message.ERROR);
            message.setMsg("登录失败，账号或密码有误");
        }
        return message;
    }

    /**
     * 注册用户
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public Message signup(User user) {
        Message message = new Message();
        try {
            int res = userMapper.insert(user);
            if(res > 0) {
                message.setMsg("注册成功，稍等一会儿将会收到一封邮件");
                // 通过邮箱查询账号
                User user1 = userMapper.selectOne(new QueryWrapper<User>().eq("email", user.getEmail()));
                message.setStatus(Message.OK);
                emailService.sendEmail(username, user.getEmail(),
                        "欢迎注册“小聊”\n" +
                                "昵称：" + user1.getUsername() + "\n" +
                                "你的账号为：" + user1.getUserId() + "\n" +
                                "请牢记住你的密码，如果忘记了，请使用找回功能找回你的密码\n");
            } else {
                message.setStatus(Message.ERROR);
                message.setMsg("注册失败");
            }
        } catch (Exception e) {
            // 判断异常是否为唯一异常
            if(e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
                message.setStatus(Message.ERROR);
                message.setMsg("该邮箱已经注册过");
            }
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public Message retrievePassword(String email) {
        return null;
    }
}
