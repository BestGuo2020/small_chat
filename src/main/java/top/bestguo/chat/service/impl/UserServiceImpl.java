package top.bestguo.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.bestguo.chat.entity.Friend;
import top.bestguo.chat.entity.User;
import top.bestguo.chat.mapper.FriendMapper;
import top.bestguo.chat.mapper.UserMapper;
import top.bestguo.chat.msg.Message;
import top.bestguo.chat.msg.SingleMessage;
import top.bestguo.chat.service.UserService;

import javax.xml.transform.OutputKeys;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FriendMapper friendMapper;

    /**
     *
     * @param keywords 查询关键字
     * @return
     */
    @Override
    public SingleMessage<List<User>> findFriends(String keywords) {
        SingleMessage<List<User>> singleMessage = new SingleMessage<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("userId", "username", "website", "city", "bloodType", "head", "email");
        // 如果是数字
        Matcher matcher = Pattern.compile("^[1-9]\\d*$").matcher(keywords);
        if(matcher.matches() && keywords.length() >= 7) {
            queryWrapper.eq("userId", keywords).or().eq("username", keywords);
        } else {
            queryWrapper.like("username", keywords);
        }
        // 查询
        List<User> users = userMapper.selectList(queryWrapper);
        singleMessage.setStatus(Message.OK);
        singleMessage.setData(users);
        singleMessage.setMsg("查询" + users.size() + "条结果");
        return singleMessage;
    }

    /**
     * 通过id查询好友信息
     *
     * @param userId 好友信息
     * @return
     */
    @Override
    public SingleMessage<User> findFriend(Integer userId) {
        SingleMessage<User> message = new SingleMessage<>();
        User user = userMapper.selectById(userId);
        user.setPassword(null);
        message.setMsg("OK");
        message.setData(user);
        message.setStatus(Message.OK);
        return message;
    }

    /**
     * 添加好友
     *
     * @param friend 好友关系实体类
     * @return
     */
    @Override
    public Message addFriend(Friend friend) {
        Message message = new Message();
        // 查询条件
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", friend.getUserId())
                .eq("friendId", friend.getFriendId());
        Friend friend1 = friendMapper.selectOne(queryWrapper);
        if(friend1 == null) {
            // 添加信息
            int insert = friendMapper.insert(friend);
            if(insert > 0) {
                message.setMsg("添加好友成功，等待好友的同意！");
                message.setStatus(Message.OK);
            } else {
                message.setStatus(Message.ERROR);
                message.setMsg("添加失败！");
            }
        } else {
            message.setStatus(Message.ERROR);
            if(friend1.getIsAllowed() == null) {
                message.setMsg("你已经提交请求，等待对方是否同意!");
            } else if (friend1.getIsAllowed() == 1) {
                message.setMsg("你已经添加该好友！");
            } else if(friend1.getIsAllowed() == 0) {
                message.setStatus(Message.OK);
                message.setMsg("重新添加好友成功，等待好友的同意！");
                friendMapper.update(friend, queryWrapper);
            }
        }
        return message;
    }

    @Override
    public Message confirmFriend(Friend friend, Integer ok) {
        Message message = new Message();
        // 查询条件
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", friend.getFriendId())
                .eq("friendId", friend.getUserId());
        Friend friend1 = friendMapper.selectOne(queryWrapper);
        // 如果同意
        if(ok == 1) {
            friend1.setIsAllowed(ok);
            int update = friendMapper.update(friend1, queryWrapper);
            if(update == 1) {
                message.setStatus(Message.OK);
                message.setMsg("你和“" + friend.getUserId() + "”成为好友");
                // 更新条件位置调换
                queryWrapper.eq("userId", friend.getUserId())
                        .eq("friendId", friend.getFriendId());
                // 更新数据调换
                friend.setIsAllowed(ok);
                friendMapper.insert(friend);
            }
        } else {
            friend1.setIsAllowed(ok);
            message.setStatus(Message.OK);
            message.setMsg("你拒绝了和“" + friend.getUserId() + "”成为好友");
            friendMapper.update(friend1, queryWrapper);
        }
        return message;
    }

    @Override
    public SingleMessage<List<Friend>> confirmFriendList(Integer userId) {
        SingleMessage<List<Friend>> message = new SingleMessage<>();
        // 查询调价
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("friendId", userId).isNull("isAllowed");
        // 开始查询
        List<Friend> friends = friendMapper.selectList(queryWrapper);
        message.setStatus(Message.OK);
        message.setData(friends);
        return message;
    }


}
