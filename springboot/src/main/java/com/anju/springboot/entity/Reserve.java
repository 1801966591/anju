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
 * 
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-06
 */
@Getter
@Setter
@TableName("reserve")
public class Reserve implements Serializable {

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
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 预约人姓名
     */
    @TableField("name")
    private String name;

    /**
     * 电话号码
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 看房时间
     */
    @TableField("time")
    private String time;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 预约状态 0 未到预约时间 1 已过预约时间 2 用户已取消预约 3 房东超时未确认，已取消预约 4 已完成
     */
    @TableField("reserve_status")
    private Integer reserveStatus;

    /**
     * 预约创建时间
     */
    @TableField("create_time")
    private String createTime;

    /**
     * 预约修改时间
     */
    @TableField("update_time")
    private String updateTime;
}
