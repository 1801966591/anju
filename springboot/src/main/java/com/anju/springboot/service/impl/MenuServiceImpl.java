package com.anju.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Menu;
import com.anju.springboot.entity.RoleMenu;
import com.anju.springboot.mapper.MenuMapper;
import com.anju.springboot.mapper.RoleMenuMapper;
import com.anju.springboot.service.MenuService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<Menu> getMenus(String name) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Menu::getName,name);

        List<Menu> list = menuMapper.selectList(queryWrapper);

        //找出一级菜单
        List<Menu> parentNodes = list.stream().filter(record -> record.getPid() == null).collect(Collectors.toList());

        //找出一级菜单中的子菜单
        for(Menu menu : parentNodes) {
            menu.setChildren(list.stream().filter(m -> menu.getId().equals(m.getPid())).collect(Collectors.toList()));
        }

        return parentNodes;
    }

    @Override
    public Result deleteById(Integer id) throws Exception {

        Menu menu = menuMapper.selectById(id);

        //查询菜单是否绑定角色
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getMenuId,id);
        List<RoleMenu> list = roleMenuMapper.selectList(queryWrapper);

        int i = 0;

        if (!list.isEmpty()){
            return Result.error("该菜单已经绑定角色，无法删除！");
        }else {
            if (menu.getPid() == null){
                //删除的不是子菜单
                //查找子菜单
                LambdaQueryWrapper<Menu> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(Menu::getId,menu.getPid());
                List<Menu> subMenuList = menuMapper.selectList(queryWrapper1);
                if (!subMenuList.isEmpty()){
                    //如果有子菜单的话则需要把该菜单下的所有子菜单一起删除
                    i += deleteSubMenu(menu.getId());
                }
            }
            //删除菜单
            i += menuMapper.deleteById(menu);

            if (i >= 1) {
                return Result.success();
            }else {
                throw new Exception();
            }
        }
    }

    @Override
    public Result deleteByIds(List<Integer> ids) throws Exception {
        int i = 0;

        for (Integer id : ids){
            //查询菜单是否绑定角色
            LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RoleMenu::getMenuId,id);
            List<RoleMenu> list = roleMenuMapper.selectList(queryWrapper);

            if (!list.isEmpty()){
                String name = menuMapper.selectById(id).getName();
                return Result.error("菜单"+ name +"已经绑定角色，无法删除！");
            }else {
                Menu menu = menuMapper.selectById(id);
                if (menu.getPid() == null){
                    //删除的不是子菜单
                    //查找子菜单
                    LambdaQueryWrapper<Menu> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(Menu::getId,menu.getPid());
                    List<Menu> subMenuList = menuMapper.selectList(queryWrapper1);
                    if (!subMenuList.isEmpty()){
                        //如果有子菜单的话则需要把该菜单下的所有子菜单一起删除
                        i += deleteSubMenu(id);
                    }

                }
                i += menuMapper.deleteById(id);
            }
        }
        if (i > 0){
            return Result.success();
        }else{
            throw new Exception();
        }
    }

    private Integer deleteSubMenu(Integer pid){
        //存储所有子菜单的id
        List<Integer> ids = new ArrayList<>();
        //查找所有子菜单，并将id存入子菜单id列表中
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getPid,pid);
        menuMapper.selectList(queryWrapper)
                .stream()
                .peek((item) -> ids.add(item.getId()))
                .forEachOrdered(item -> {});
        return menuMapper.deleteBatchIds(ids);
    }
}
