package com.anju.springboot.entity;

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
 * @since 2023-09-20
 */
@Getter
@Setter
@TableName("dic_type")
public class DicType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典类型编码
     */
    @TableId("code")
    private String code;

    /**
     * 字典类型名称
     */
    @TableField("name")
    private String name;

    /**
     * 字典类型描述
     */
    @TableField("description")
    private String description;
}
