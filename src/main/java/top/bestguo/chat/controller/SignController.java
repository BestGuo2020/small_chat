package top.bestguo.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.bestguo.chat.entity.User;
import top.bestguo.chat.msg.Message;
import top.bestguo.chat.service.SignService;

import javax.servlet.http.HttpSession;

/**
 * 登录、注册、忘记密码控制层
 */
@Controller
public class SignController {

    @Autowired
    SignService signService;

    @GetMapping("/signin")
    public String signin() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "register";
    }

    @GetMapping("/forgot")
    public String forgot() {
        return "forgot-password";
    }

    ///****************** 以下是处理请求的相关代码 ********************/

    /**
     * 处理用户注册的信息
     *
     * @param user
     * @return
     */
    @PostMapping("/signup_do")
    @ResponseBody
    public Message signup_do(User user) {
        return signService.signup(user);
    }

    /**
     * 处理用户登录请求
     *
     * @param user
     * @return
     */
    @PostMapping("/signin_do")
    @ResponseBody
    public Message signin_do(User user, HttpSession session) {
        return signService.signin(user, session);
    }

    /**
     * 注销，删除 session
     *
     * @param httpSession
     * @return
     */
    @GetMapping("/signout")
    public String signout(HttpSession httpSession) {
        httpSession.removeAttribute("user");
        return "redirect:/signin";
    }

}
