package com.anju.springboot.service;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
public interface RoleService extends IService<Role> {
    Result deleteById(Integer id) throws Exception;

    Result deleteByIds(List<Integer> ids) throws Exception;


    void setRoleMenu(Integer roleId, List<Integer> menuIds);

    List<Integer> getRoleMenu(Integer roleId);

    String getRoleByUserId(Integer userId);
}
