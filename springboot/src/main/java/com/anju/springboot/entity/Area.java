package com.anju.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 区县表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Getter
@Setter
@TableName("area")
public class Area implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId("id")
    private Integer id;

    /**
     * 区县id
     */
    @TableField("areaID")
    private String areaID;

    /**
     * 区县名称
     */
    @TableField("area")
    private String area;

    /**
     * 所属市id
     */
    @TableField("father")
    private String father;
}
