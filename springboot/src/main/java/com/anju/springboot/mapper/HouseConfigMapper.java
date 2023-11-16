package com.anju.springboot.mapper;

import com.anju.springboot.entity.HouseConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 房源配置表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-03
 */
@Mapper
public interface HouseConfigMapper extends BaseMapper<HouseConfig> {

    @Delete("""
            delete from house_config where house_id=#{houseId}
            """)
    void deleteByHouseId(@Param("houseId") Integer houseId);
}
