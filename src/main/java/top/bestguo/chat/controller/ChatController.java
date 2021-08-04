package top.bestguo.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.bestguo.chat.entity.Friend;
import top.bestguo.chat.entity.User;
import top.bestguo.chat.msg.Message;
import top.bestguo.chat.msg.SingleMessage;
import top.bestguo.chat.service.UserService;

import javax.servlet.http.HttpSession;

@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String chatIndex(HttpSession session, Model model) {
        return "chat";
    }

    @GetMapping("/findFriends")
    @ResponseBody
    public SingleMessage<?> findFriends(String keywords) {
        return userService.findFriends(keywords);
    }

    @GetMapping("/findFriend")
    @ResponseBody
    public SingleMessage<?> findFriend(Integer userId) {
        return userService.findFriend(userId);
    }

    @PostMapping("/addFriend")
    @ResponseBody
    public Message addFriend(Friend friend, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(friend.getFriendId().equals(user.getUserId())) {
            Message message = new Message();
            message.setStatus(Message.ERROR);
            message.setMsg("不能添加自己为好友");
            return message;
        }
        return userService.addFriend(friend);
    }

    @GetMapping("/confirmList")
    @ResponseBody
    public Message confirmList(Integer userId) {
        return userService.confirmFriendList(userId);
    }

    /**
     * 同意添加
     *
     * @param friend 好友
     * @param ok 是否同意
     * @return
     */
    @PostMapping("/confirmFriend")
    @ResponseBody
    public Message confirmFriend(Friend friend, Integer ok) {
        return userService.confirmFriend(friend, ok);
    }

}
