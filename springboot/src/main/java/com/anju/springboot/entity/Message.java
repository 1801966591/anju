package com.anju.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Getter
@Setter
@TableName("message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 消息标题
     */
    @TableField("title")
    private String title;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息接收人id
     */
    @TableField("receiver_id")
    private Integer receiverId;

    /**
     * 消息发送人id
     */
    @TableField("sender_id")
    private Integer senderId;

    /**
     * 已读状态 0 未读 1 已读
     */
    @TableField("read_status")
    private Integer readStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private String createTime;
}
