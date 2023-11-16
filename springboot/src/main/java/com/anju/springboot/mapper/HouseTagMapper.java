package com.anju.springboot.mapper;

import com.anju.springboot.entity.HouseTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
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
 * @since 2023-09-26
 */
@Mapper
public interface HouseTagMapper extends BaseMapper<HouseTag> {

    @Delete("""
            delete from house_tag where house_id=#{houseId}
            """)
    void deleteByHouseId(@Param("houseId") Integer houseId);

    @Select("""
            select * from house_tag where house_id=#{houseId}
            """)
    List<HouseTag> getByHouseId(@Param("houseId") Integer houseId);
}
