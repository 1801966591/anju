package com.anju.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-06
 */
@Data
@TableName("reserve_audit")
public class ReserveAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 预约id
     */
    @TableField("reserve_id")
    private Integer reserveId;

    /**
     * 审核状态 0 待确认 1 同意 2 拒绝 3 超时未确认 4 用户已取消预约
     */
    @TableField("status")
    private Integer status;

    /**
     * 拒绝意见
     */
    @TableField("content")
    private String content;
}
