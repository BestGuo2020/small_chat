package top.bestguo.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 账号
     */
    @TableId(value = "userId", type = IdType.AUTO)
    private Integer userId;

    /**
     * 昵称
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 站点
     */
    @TableField(value = "website")
    private String website;

    /**
     * 城市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 血型
     */
    @TableField(value = "bloodType")
    private String bloodType;

    /**
     * 头像
     */
    @TableField(value = "head")
    private String head;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 是否在线标识
     */
    @TableField(exist = false)
    private Integer isOnline;

}