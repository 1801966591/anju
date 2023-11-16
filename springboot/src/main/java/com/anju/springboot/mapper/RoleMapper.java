package com.anju.springboot.mapper;

import com.anju.springboot.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

}
