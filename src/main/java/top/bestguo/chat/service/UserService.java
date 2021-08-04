package top.bestguo.chat.service;

import top.bestguo.chat.entity.Friend;
import top.bestguo.chat.entity.User;
import top.bestguo.chat.msg.Message;
import top.bestguo.chat.msg.SingleMessage;

import java.util.List;

public interface UserService {

    /**
     * 通过id查询
     *
     * @param keywords 查询关键字
     * @return 查询的好友
     */
    SingleMessage<List<User>> findFriends(String keywords);

    /**
     * 通过 id 查询好友信息
     * @param userId 好友信息
     * @return 查询的好友
     */
    SingleMessage<User> findFriend(Integer userId);

    /**
     * 添加朋友
     *
     * @param friend 好友关系实体类
     * @return
     */
    Message addFriend(Friend friend);

    /**
     * 确认好友
     *
     * @param friend 好友
     * @param ok 是否确认
     * @return
     */
    Message confirmFriend(Friend friend, Integer ok);

    /**
     * 确认好友
     *
     * @param userId 用户id
     * @return
     */
    SingleMessage<List<Friend>> confirmFriendList(Integer userId);
}
