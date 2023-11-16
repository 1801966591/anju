package com.anju.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Menu;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
public interface MenuService extends IService<Menu> {

    List<Menu> getMenus(String name);

    Result deleteById(Integer id) throws Exception;

    Result deleteByIds(List<Integer> ids) throws Exception;
}
