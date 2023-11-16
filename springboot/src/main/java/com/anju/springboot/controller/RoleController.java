package com.anju.springboot.controller;

import cn.hutool.core.date.DateUtil;
import com.anju.springboot.common.AutoLog;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Role;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/search")
    public Result search(Param param){

        Page<Role> pageInfo = new Page<>(param.getCurrentPage(),param.getPageSize());

        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(param.getName()),Role::getName,param.getName());
        roleService.page(pageInfo, queryWrapper);

        return Result.success(pageInfo);
    }

    @PostMapping("/save")
    @AutoLog("新增角色信息")
    public Result save(@RequestBody Role role) throws Exception {

        role.setCreateTime(DateUtil.now());
        boolean flag = roleService.save(role);

        if (flag) {
            return Result.success();
        } else {
            throw new Exception();
        }

    }

    @PutMapping("/update")
    @AutoLog("修改角色信息")
    public Result update(@RequestBody Role role) throws Exception {
        role.setUpdateTime(DateUtil.now());
        boolean flag = roleService.updateById(role);

        if (flag) {
            return Result.success();
        } else {
            throw new Exception();
        }
    }

    @DeleteMapping("/deleteById/{id}")
    @AutoLog("删除角色信息")
    public Result deleteById(@PathVariable("id") Integer id) throws Exception {
        return roleService.deleteById(id);
    }

    @DeleteMapping("/deleteByIds/{ids}")
    @AutoLog("批量删除角色信息")
    public Result deleteById(@PathVariable("ids") List<Integer> ids) throws Exception {
        return roleService.deleteByIds(ids);
    }

    @PostMapping("/setMenu")
    @AutoLog("分配角色菜单信息")
    public Result setRoleMenu(@RequestBody Param param) throws Exception {

        Integer roleId = param.getId();

        List<Integer> menuIds = param.getMenuIds();

        roleService.setRoleMenu(roleId,menuIds);

        return Result.success();
    }

    @GetMapping("/getRoleMenu/{roleId}")
    public Result getRoleMenu(@PathVariable Integer roleId){

        List<Integer> list = roleService.getRoleMenu(roleId);

        return Result.success(list);

    }

}
