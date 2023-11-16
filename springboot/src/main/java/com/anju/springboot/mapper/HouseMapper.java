package com.anju.springboot.mapper;

import com.anju.springboot.entity.House;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 房屋信息表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-26
 */
@Mapper
public interface HouseMapper extends BaseMapper<House> {

    List<House> getByName(@Param("name") String name, @Param("pageNo") Integer pageNo,@Param("pageSize") Integer pageSize);

    int getTotalByName(@Param("name") String name);


}
