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
 * 报修信息表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-18
 */
@Getter
@Setter
@TableName("repair")
public class Repair implements Serializable {

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
     * 报修人id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 租赁信息id
     */
    @TableField("rent_id")
    private Integer rentId;

    /**
     * 报修信息
     */
    @TableField("repair_info")
    private String repairInfo;

    /**
     * 报修时间
     */
    @TableField("create_time")
    private String createTime;

    /**
     * 处理时间
     */
    @TableField("repair_time")
    private String repairTime;

    /**
     * 1 待处理 2 已处理 3 已完成
     */
    @TableField("repair_status")
    private Integer repairStatus;

    /**
     * 1 首次报修 2 再次报修
     */
    @TableField("repair_type")
    private Integer repairType;

    /**
     * 报修次数
     */
    @TableField("repair_times")
    private Integer repairTimes;


}
