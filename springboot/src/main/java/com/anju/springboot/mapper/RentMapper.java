package com.anju.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.anju.springboot.entity.Rent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 租赁信息表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-18
 */
@Mapper
public interface RentMapper extends BaseMapper<Rent> {

    List<Rent> getByConditionForLandlord(@Param("houseId") Integer houseId,
                                         @Param("userId") Integer userId,
                                         @Param("pageNo") Integer pageNo,
                                         @Param("pageSize") Integer pageSize);

    int getTotalByConditionForLandlord(@Param("houseId") Integer houseId,
                                       @Param("userId") Integer userId);

    List<Rent> getByConditionForUser(@Param("houseId") Integer houseId,
                                     @Param("userId") Integer userId,
                                     @Param("pageNo") Integer pageNo,
                                     @Param("pageSize") Integer pageSize);

    int getTotalByConditionForUser(@Param("houseId") Integer houseId,
                                   @Param("userId") Integer userId);
    @Select("""
            select *
            from rent
             where house_id=#{houseId} and rent_status=1
             order by due_time desc
             limit 1
            """)
    Rent getLatestByHouseId(@Param("houseId") Integer houseId);
}
