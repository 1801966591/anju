package com.anju.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

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
@TableName("dic_value")
public class DicValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 字典名称
     */
    @TableField("name")
    private String name;

    /**
     * 字典值
     */
    @TableField("value")
    private String value;

    /**
     * 字典类型编码
     */
    @TableField("code")
    private String code;
}
