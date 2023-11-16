package com.anju.springboot.mapper;

import com.anju.springboot.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户角色关系表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}
