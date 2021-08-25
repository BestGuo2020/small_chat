package top.bestguo.chat.msg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 带信息的 Websocket 消息
 * @param <T> 需要传递的消息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage<T> extends Message {

    /**
     * 加载用户信息的状态
     */
    public static final int LOAD_USER_DATA = 5;

    /**
     * 加载好友的标记
     */
    public static final int LOAD_FRIEND = 10;

    /**
     * 刷新在线状态的标记
     */
    public static final int FLUSH_ONLINE_STATUS = 11;

    /**
     * 加载聊天记录的标记
     */
    public static final int LOAD_CHAT_INFO = 20;

    /**
     * 发送聊天信息的标记
     */
    public static final int SEND_MESSAGE = 21;

    /**
     * 未验证的好友
     */
    public static final int FRIENDS_CONFIRM = 30;

    /**
     * 好友不在线
     */
    public static final int FRIEND_OFFLINE = 31;

    /**
     * 携带信息
     */
    private T info;

    /**
     * 使用 websocket 时携带的状态
     */
    private int websocketStatus;

    /**
     * 发送时间啊
     */
    private String time;

}
