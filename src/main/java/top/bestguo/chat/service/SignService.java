package top.bestguo.chat.service;

import top.bestguo.chat.entity.User;
import top.bestguo.chat.msg.Message;

import javax.servlet.http.HttpSession;

/**
 * 登录注册服务
 */
public interface SignService {

    /**
     * 登录
     *
     * @param user 用户信息
     * @return 登录状态
     */
    Message signin(User user, HttpSession httpSession);

    /**
     * 注册
     *
     * @param user 用户信息
     * @return 注册状态
     */
    Message signup(User user);

    /**
     * 找回密码
     *
     * @param email 你注册的电子邮箱
     * @return 发送状态
     */
    Message retrievePassword(String email);

}
