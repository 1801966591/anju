package com.anju.springboot.service;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户角色关系表 服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
public interface UserRoleService extends IService<UserRole> {


    Result getCount();

    Integer getAdminId();
}
