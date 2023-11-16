package com.anju.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Role;
import com.anju.springboot.entity.RoleMenu;
import com.anju.springboot.entity.UserRole;
import com.anju.springboot.exception.CustomException;
import com.anju.springboot.mapper.RoleMapper;
import com.anju.springboot.mapper.RoleMenuMapper;
import com.anju.springboot.mapper.UserRoleMapper;
import com.anju.springboot.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Result deleteById(Integer id) throws Exception {

        Role role = roleMapper.selectById(id);
        if (role.getCode().equals(Constant.ROLE_ADMIN)) {
            throw new CustomException("当前选中的角色为管理员，不能删除");
        } else {
            int i = roleMapper.deleteById(id);
            if (i == 1){
                return Result.success();
            }else {
                throw new Exception();
            }
        }
    }

    @Override
    public Result deleteByIds(List<Integer> ids) throws Exception {

        boolean flag = false;

        for (Integer id : ids){
            Role role = roleMapper.selectById(id);
            if (role.getCode().equals(Constant.ROLE_ADMIN)) {
                throw new CustomException("当前选中的角色中有管理员，不能删除");
            } else {
                int i = roleMapper.deleteById(id);
                flag = i > 0;
            }
        }

        if (flag) {
            return Result.success();
        } else {
            throw new Exception();
        }
    }

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {

        //删除当前角色id的所有绑定关系
        LambdaQueryWrapper<RoleMenu> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId,roleId);
        roleMenuMapper.delete(queryWrapper);

        //把前端传过来的菜单id和角色id绑定
        for (Integer menuId : menuIds){
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<Integer> getRoleMenu(Integer roleId) {

        return roleMenuMapper.getMenuIdsByRoleId(roleId);
    }

    @Override
    public String getRoleByUserId(Integer userId) {
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,userId);
        UserRole userRole = userRoleMapper.selectOne(queryWrapper);

        Integer roleId = userRole.getRoleId();
        Role role = roleMapper.selectById(roleId);

        return role.getCode();
    }
}
