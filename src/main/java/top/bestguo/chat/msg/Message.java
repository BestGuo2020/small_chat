package top.bestguo.chat.msg;

import lombok.Data;

@Data
public class Message {

    public static final int OK = 1; // 成功时的状态码
    public static final int ERROR = 0; // 失败时的状态码
    public static final int SESSION_EXIST = 2; // 会话存在
    public static final int SESSION_EXPIRED = -1; // session 过期的状态

    /**
     * 状态码
     */
    private int status;

    /**
     * 消息
     */
    private String msg;

}
