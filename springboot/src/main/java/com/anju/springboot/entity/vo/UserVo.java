package com.anju.springboot.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.anju.springboot.entity.Menu;
import com.anju.springboot.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.xx.springboot.entity.vo
 * @ClassName: UserVo
 * @Time: 2023/9/18 19:59
 * @Description: TODO
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserVo extends User {
    /**
     * 登录token信息
     */
    @TableField(exist = false)
    private String token;

    /**
     * 角色信息
     */
    @TableField(exist = false)
    private String role;

    /**
     * 所有菜单信息
     */
    @TableField(exist = false)
    List<Menu> allMenus;

    /**
     * 该用户的角色绑定的菜单
     */
    @TableField(exist = false)
    List<Menu> menu;

    List<UserVo> records;
    int total;
}
