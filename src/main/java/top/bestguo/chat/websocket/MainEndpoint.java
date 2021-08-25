package top.bestguo.chat.websocket;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import top.bestguo.chat.config.HttpSessionConfig;
import top.bestguo.chat.entity.Friend;
import top.bestguo.chat.entity.User;
import top.bestguo.chat.mapper.FriendMapper;
import top.bestguo.chat.mapper.UserMapper;
import top.bestguo.chat.msg.Message;
import top.bestguo.chat.msg.WebSocketMessage;
import top.bestguo.chat.service.UserService;
import top.bestguo.chat.util.MessageUtil;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天，好友列表的 EndPoint
 */
@ServerEndpoint(value = "/index/mainPoint", configurator = HttpSessionConfig.class)
@Component
@Slf4j
public class MainEndpoint {

    // 将上线的用户放到 Map 集合中
    private static final Map<Integer, MainEndpoint> onlineUsers = new ConcurrentHashMap<>();

    // 拿到当前用户的 WebSocket 会话
    private Session session;

    // 拿到使用 Http 协议的会话
    private HttpSession httpSession;

    // 获取 HttpSession 中的用户信息
    private User user;

    // 定时任务，每隔 2s 刷新一次好友在线状态
    private Timer timer = new Timer();

    // 不能直接通过注解的方式拿到容器中的对象，需要从上下文加载器中得到 ApplicationContext
    public static FriendMapper friendMapper;
    public static UserMapper userMapper;

    /**
     * 和服务端建立连接时，获取用户id
     * @param session Websocket 会话
     * @param config 配置
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
        // 赋值 Websocket 会话
        this.session = session;
        // 赋值 HttpSession 的会话
        this.httpSession = (HttpSession) config.getUserProperties().get("httpSession");
        // 获取 HttpSession 中的用户信息
        this.user = (User) this.httpSession.getAttribute("user");
        // 将当前的 Endpoint 放入到 map 集合中
        onlineUsers.put(this.user.getUserId(), this);
        // 加载当前用户信息
        // 封装成一个对象，发送给当前的用户
        WebSocketMessage<User> message = new WebSocketMessage<>();
        message.setWebsocketStatus(WebSocketMessage.LOAD_USER_DATA);
        message.setInfo(user);
        message.setStatus(Message.OK);
        session.getBasicRemote().sendText(MessageUtil.objectConvertJson(message));
        // 任务调度
        timer.schedule(new MyTimeTask(), 0, 2000);
    }

    /**
     * 任务
     */
    private class MyTimeTask extends TimerTask {
        /**
         * 好友数
         */
        private int friendsCount = 0;

        @Override
        public void run() {
            flushFriends();
            flushConfirmFriends();
        }

        /**
         * 刷新未验证的好友数
         */
        private void flushConfirmFriends() {
            // 封装成一个对象，发送给当前的用户
            WebSocketMessage<List<User>> message = new WebSocketMessage<>();
            List<User> users = new ArrayList<>();
            // 查询
            // 查询调价
            QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("friendId", user.getUserId()).isNull("isAllowed");
            // 开始查询
            List<Friend> friends = friendMapper.selectList(queryWrapper);
            for (Friend friend : friends) {
                User user = userMapper.selectById(friend.getUserId());
                users.add(user);
            }
            message.setWebsocketStatus(WebSocketMessage.FRIENDS_CONFIRM);
            message.setStatus(Message.OK);
            message.setMsg("获取成功");
            message.setInfo(users);
            try {
                session.getBasicRemote().sendText(MessageUtil.objectConvertJson(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 刷新好友状态
         */
        private void flushFriends() {
            // 封装成一个对象，发送给当前的用户
            WebSocketMessage<List<User>> message = new WebSocketMessage<>();
            // 查询当前用户的好友数
            List<Friend> friends = friendMapper.selectList(new QueryWrapper<Friend>().eq("userId", user.getUserId())
                    .eq("isAllowed", 1));
            // 如果新增了好友数
            if(friendsCount < friends.size()) {
                // 添加一个标记，重新加载好友
                message.setWebsocketStatus(WebSocketMessage.LOAD_FRIEND);
            } else {
                // 标记为刷新用户登录状态
                message.setWebsocketStatus(WebSocketMessage.FLUSH_ONLINE_STATUS);
            }
            friendsCount = friends.size();
            // 遍历 Map 中的所有在线人数，并且查询当前用户的好友是否在线
            Set<Integer> userIds = onlineUsers.keySet();
            // 将在线的好友放到一个数组中
            List<User> users = new ArrayList<>();
            // 遍历当前用户的好友列表
            log.info("当前用户：" + user.getUsername());
            for (Friend friend : friends) {
                // 获取好友信息，判断这个好友是否在线
                Integer friendId = friend.getFriendId();
                User user1 = userMapper.selectById(friendId);
                // 不能暴露密码
                user1.setPassword(null);
                if(userIds.contains(friendId)) {
                    user1.setIsOnline(1);
                } else {
                    user1.setIsOnline(0);
                }
                // 好友在线状态
                log.info("好友：" + user1.getUserId() + "，是否在线：" + user1.getIsOnline());
                // 添加好友信息到列表中
                users.add(user1);
            }
            message.setStatus(Message.OK);
            message.setMsg("获取成功");
            message.setInfo(users);
            try {
                session.getBasicRemote().sendText(MessageUtil.objectConvertJson(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 接收来自客户端的信息
     *
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        // 设置消息内容
        WebSocketMessage<User> message1 = new WebSocketMessage<>();
        try {
            JSONObject jsonObject = new JSONObject(message);
            int sendType = jsonObject.getInt("sendType");
            // 判断是加载聊天信息
            if(sendType == WebSocketMessage.LOAD_CHAT_INFO) {
                // 拿到用户id，查询用户信息
                User user = userMapper.selectById(jsonObject.getInt("userId"));
                user.setPassword(null);
                // 判断是否在线
                Integer online = onlineUsers.containsKey(user.getUserId()) ? 1 : 0;
                user.setIsOnline(online);
                message1.setWebsocketStatus(WebSocketMessage.LOAD_CHAT_INFO);
                message1.setInfo(user);
                message1.setStatus(Message.OK);
                log.info("加载聊天界面，获取用户信息......");
            } else if (sendType == WebSocketMessage.SEND_MESSAGE) {
                // 拿到用户id、发送消息和它的连接对象
                int userId = jsonObject.getInt("userId");
                String msg = jsonObject.getString("message");
                String time = jsonObject.getString("time");
                MainEndpoint mainEndpoint = onlineUsers.get(userId);
                // 判断当前用户是否在线，不在线就提示它不在线
                if(mainEndpoint == null) {
                    // 发送消息给发送者
                    message1.setWebsocketStatus(WebSocketMessage.FRIEND_OFFLINE);
                    message1.setInfo(user);
                    message1.setStatus(Message.OK);
                    message1.setMsg("发送失败，好友 " + userId + " 不在线");
                    message1.setTime(time);
                } else {
                    // 发送消息给这位用户
                    message1.setWebsocketStatus(WebSocketMessage.SEND_MESSAGE);
                    message1.setInfo(user);
                    message1.setStatus(Message.OK);
                    message1.setMsg(msg);
                    message1.setTime(time);
                    log.info("发送状态：   " + user.getUsername() + " -> message -> " + mainEndpoint.user.getUsername());
                    mainEndpoint.session.getBasicRemote().sendText(MessageUtil.objectConvertJson(message1));
                    return;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            message1.setStatus(Message.ERROR);
            message1.setMsg("发生异常，json 解析错误");
        }
        // 发送消息啦
        session.getBasicRemote().sendText(MessageUtil.objectConvertJson(message1));
    }

    @OnClose
    public void onClose(Session session) {
        // 断开连接时，关闭定时任务
        timer.cancel();
        // 移除用户的在线状态
        onlineUsers.remove(user.getUserId());

    }

}
