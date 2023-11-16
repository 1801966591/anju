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
 * 房屋地址表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-27
 */
@Getter
@Setter
@TableName("house_address")
public class HouseAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 房屋id
     */
    @TableField("house_id")
    private Integer houseId;

    /**
     * 省id
     */
    @TableField("province_id")
    private Integer provinceId;

    /**
     * 市id
     */
    @TableField("city_id")
    private Integer cityId;

    /**
     * 区id
     */
    @TableField("area_id")
    private Integer areaId;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;
}
