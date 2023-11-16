package com.anju.springboot.mapper;

import com.anju.springboot.entity.Province;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 省表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Mapper
public interface ProvinceMapper extends BaseMapper<Province> {


    @Select("""
            select * from province where provinceID=#{provinceId}
            """)
    Province getByProvinceId(@Param("provinceId") Integer provinceId);
}
