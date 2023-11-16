package com.anju.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 市表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-14
 */
@Getter
@Setter
@TableName("city")
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId("id")
    private Integer id;

    /**
     * 市id
     */
    @TableField("cityID")
    private String cityID;

    /**
     * 市名称
     */
    @TableField("city")
    private String city;

    /**
     * 所属省id
     */
    @TableField("father")
    private String father;

    @TableField(exist = false)
    private List<Area> children;
}
