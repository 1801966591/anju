package com.anju.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.anju.springboot.entity.HouseAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 房屋地址表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-27
 */
@Mapper
public interface HouseAddressMapper extends BaseMapper<HouseAddress> {

    @Select("""
            select * from house_address where house_id=#{houseId}
            """)
    HouseAddress getByHouseId(@Param("houseId") Integer houseId);

}
