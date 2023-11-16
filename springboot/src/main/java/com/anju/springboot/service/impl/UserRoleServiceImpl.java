package com.anju.springboot.service.impl;

import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Role;
import com.anju.springboot.entity.UserRole;
import com.anju.springboot.entity.vo.CountVo;
import com.anju.springboot.mapper.RoleMapper;
import com.anju.springboot.mapper.UserRoleMapper;
import com.anju.springboot.service.UserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Result getCount() {

        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Role::getCode, Constant.ROLE_ADMIN);
        List<Role> roles = roleMapper.selectList(queryWrapper);

        List<CountVo> list = new ArrayList<>();
        for (Role role : roles){
            LambdaQueryWrapper<UserRole> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(UserRole::getRoleId,role.getId());
            List<UserRole> userRoles = userRoleMapper.selectList(queryWrapper1);
            if (!userRoles.isEmpty()){
                CountVo vo = new CountVo();
                vo.setName(role.getName());
                vo.setValue(userRoles.size());
                list.add(vo);
            }
        }
        return Result.success(list);

    }

    @Override
    public Integer getAdminId() {

        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getCode,Constant.ROLE_ADMIN);
        Role role = roleMapper.selectOne(queryWrapper);

        LambdaQueryWrapper<UserRole> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(UserRole::getRoleId,role.getId());
        UserRole userRole = userRoleMapper.selectOne(queryWrapper1);

        return userRole.getUserId();

    }
}
