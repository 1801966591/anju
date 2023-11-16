package com.anju.springboot.entity;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Getter
@Setter
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    @TableField("username")
    @Alias("用户名")
    private String username;

    /**
     * 昵称
     */
    @TableField("nickname")
    @Alias("昵称")
    private String nickname;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    @Alias("真实姓名")
    private String realName;

    /**
     * 性别((1:男;2:女))
     */
    @TableField("gender")
    @Alias("性别")
    private Integer gender;

    /**
     * 密码
     */
    @TableField("password")
    @Alias("密码")
    private String password;

    /**
     * 盐
     */
    @TableField("salt")
    @Alias("盐")
    private String salt;

    /**
     * 用户头像
     */
    @TableField("avatar")
    @Alias("用户头像")
    private String avatar;

    /**
     * 手机号
     */
    @TableField("mobile")
    @Alias("手机号")
    private String mobile;

    /**
     * 用户状态((1:正常;0:禁用))
     */
    @TableField("status")
    @Alias("用户状态")
    private Integer status;

    /**
     * 用户邮箱
     */
    @TableField("email")
    @Alias("用户邮箱")
    private String email;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @Alias("创建时间")
    private String createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @Alias("更新时间")
    private String updateTime;

}
