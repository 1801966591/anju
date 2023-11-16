package com.anju.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.anju.springboot.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    @Select("select menu_id from role_menu where role_id=#{roleId}")
    List<Integer> getMenuIdsByRoleId(@Param("roleId") Integer roleId);
}
