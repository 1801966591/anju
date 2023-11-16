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
 * 省表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-14
 */
@Getter
@Setter
@TableName("province")
public class Province implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId("id")
    private Integer id;

    /**
     * 省id
     */
    @TableField("provinceID")
    private String provinceID;

    /**
     * 省名称
     */
    @TableField("province")
    private String province;

    @TableField(exist = false)
    private List<City> children;
}
