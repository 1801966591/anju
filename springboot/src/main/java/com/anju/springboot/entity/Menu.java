package com.anju.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Getter
@Setter
@TableName("menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 路径
     */
    @TableField("path")
    private String path;

    /**
     * 页面路径，去掉.vue
     */
    @TableField("page_path")
    private String pagePath;

    /**
     * 图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 父级菜单id
     */
    @TableField("pid")
    private Integer pid;


    @TableField(exist = false)
    private List<Menu> children;

}
