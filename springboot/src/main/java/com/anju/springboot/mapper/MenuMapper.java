package com.anju.springboot.mapper;

import com.anju.springboot.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

}
