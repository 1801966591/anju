package com.anju.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 房源信息表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("house")
public class House implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 房源名称
     */
    @TableField("name")
    private String name;

    /**
     * 房源类型
     */
    @TableField("house_type")
    private String houseType;

    /**
     * 房源朝向 1 东 2 南 3 西 4 北
     */
    @TableField("toward")
    private Integer toward;

    /**
     * 房源租赁类型 1 合租 2 整租 3 独栋
     */
    @TableField("rent_type")
    private Integer rentType;

    /**
     * 出租房间数，仅合租
     */
    @TableField("rent_room_number")
    private Integer rentRoomNumber;

    /**
     * 房源面积
     */
    @TableField("area")
    private String area;

    /**
     * 房源装修程度 1 毛坯 2 简装 3 精装
     */
    @TableField("decoration")
    private Integer decoration;

    /**
     * 房源所在楼层
     */
    @TableField("floor")
    private String floor;

    /**
     * 总楼层
     */
    @TableField("total_floor")
    private String totalFloor;

    /**
     * 是否有电梯，0 无 1 有
     */
    @TableField("elevator")
    private Integer elevator;

    /**
     * 租金
     */
    @TableField("rent")
    private Double rent;

    /**
     * 发布人id
     */
    @TableField("author_id")
    private Integer authorId;

    /**
     * 上架状态 0 未上架 1 已上架
     */
    @TableField("listing_status")
    private Integer listingStatus;

    /**
     * 出租状态 0 未出租 1 已出租
     */
    @TableField("rent_status")
    private Integer rentStatus;

    /**
     * 看房时间 0 随时看房 1 特定时间看房
     */
    @TableField("view_status")
    private Integer viewStatus;

    /**
     * 开始看房时间
     */
    @TableField("start_time")
    private String startTime;

    /**
     * 结束看房时间
     */
    @TableField("end_time")
    private String endTime;

    /**
     * 用水类型 0 未知 1 商水 2 民水
     */
    @TableField("water_type")
    private Integer waterType;

    /**
     * 用电类型 0 未知 1 商电 2 民电
     */
    @TableField("power_type")
    private Integer powerType;

    /**
     * 供暖类型 0 无供暖 1 集中供暖 2 地板辐射供暖
     */
    @TableField("heating_type")
    private Integer heatingType;

    /**
     * 是否有燃气 0 无 1 有
     */
    @TableField("gas")
    private Integer gas;

    /**
     * 入住性别要求 0 不限 1 男 2 女
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 维护时间
     */
    @TableField("maintenance_time")
    private String maintenanceTime;

    /**
     * 图1
     */
    @TableField("img1")
    private String img1;

    /**
     * 图2
     */
    @TableField("img2")
    private String img2;

    /**
     * 图3
     */
    @TableField("img3")
    private String img3;

    /**
     * 图4
     */
    @TableField("img4")
    private String img4;

    /**
     * 房源描述
     */
    @TableField("description")
    private String description;

    /**
     * 上架时间
     */
    @TableField("list_time")
    private String listTime;

    /**
     * 发布时间
     */
    @TableField("create_time")
    private String createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private String updateTime;
}
