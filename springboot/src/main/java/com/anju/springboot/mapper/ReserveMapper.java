package com.anju.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.anju.springboot.entity.Reserve;
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
 * @since 2023-10-06
 */
@Mapper
public interface ReserveMapper extends BaseMapper<Reserve> {

    @Select("""
            select * from reserve
            where house_id=#{houseId} and user_id=#{userId}
            order by create_time desc
            limit 1
            """)
    Reserve getByHouseIdAndUserId(@Param("houseId") Integer houseId, @Param("userId") Integer userId);

    @Select("""
            select * from reserve
            where house_id=#{houseId}
            """)
    List<Reserve> getByHouseId(@Param("houseId") Integer houseId);

    List<Reserve> getByConditionForLandlord(@Param("houseId") Integer houseId,
                                              @Param("name") String name,
                                              @Param("userId") Integer userId,
                                              @Param("pageNo") Integer pageNo,
                                              @Param("pageSize") Integer pageSize);
    int getTotalByConditionForLandlord(@Param("houseId") Integer houseId,
                                       @Param("name") String name,
                                       @Param("userId") Integer userId);
    List<Reserve> getByConditionForUser(@Param("houseId") Integer houseId,
                                          @Param("name") String name,
                                          @Param("userId") Integer userId,
                                          @Param("pageNo") Integer pageNo,
                                          @Param("pageSize") Integer pageSize);
    int getTotalByConditionForUser(@Param("houseId") Integer houseId,
                                   @Param("name") String name,
                                   @Param("userId") Integer userId);
}
