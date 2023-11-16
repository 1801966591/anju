package com.anju.springboot.mapper;

import com.anju.springboot.entity.City;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 市表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Mapper
public interface CityMapper extends BaseMapper<City> {

    @Select("""
            select * from city where cityID=#{cityId}
            """)
    City getByCityId(@Param("cityId") Integer cityId);
}
