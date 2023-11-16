package com.anju.springboot.mapper;

import com.anju.springboot.entity.Area;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 区县表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Mapper
public interface AreaMapper extends BaseMapper<Area> {

    @Select("""
            select * from area where areaID=#{areaId}
            """)
    Area getByAreaId(@Param("areaId") Integer areaId);
}
