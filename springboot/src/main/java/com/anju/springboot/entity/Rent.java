package com.anju.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 租赁信息表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-18
 */
@Getter
@Setter
@TableName("rent")
public class Rent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 房源id
     */
    @TableField("house_id")
    private Integer houseId;

    /**
     * 租赁人id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 租期
     */
    @TableField("term")
    private Integer term;

    /**
     * 到期时间
     */
    @TableField("due_time")
    private String dueTime;

    /**
     * 租赁状态 1 正在租赁 2 已完成 3 已退租
     */
    @TableField("rent_status")
    private Integer rentStatus;

    /**
     * 租赁时间
     */
    @TableField("rent_time")
    private String rentTime;

    /**
     * 退租时间
     */
    @TableField("return_time")
    private String returnTime;
}
