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
 * @since 2023-09-26
 */
@Getter
@Setter
@TableName("house_tag")
public class HouseTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 房屋id
     */
    @TableField("house_id")
    private Integer houseId;

    /**
     * 标签id
     */
    @TableField("tag_id")
    private Integer tagId;
}
