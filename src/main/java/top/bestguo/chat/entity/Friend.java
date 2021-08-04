package top.bestguo.chat.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName friend
 */
@TableName(value ="friend")
@Data
public class Friend implements Serializable {
    /**
     * 用户id
     */
    @TableField("userId")
    private Integer userId;

    /**
     * 好友id
     */
    @TableField("friendId")
    private Integer friendId;

    /**
     * 好友是否同意
     */
    @TableField(value = "isAllowed", updateStrategy = FieldStrategy.IGNORED)
    private Integer isAllowed;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}