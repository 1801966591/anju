package com.anju.springboot.mapper;

import com.anju.springboot.entity.Repair;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 报修信息表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-18
 */
@Mapper
public interface RepairMapper extends BaseMapper<Repair> {

    List<Repair> getByConditionForLandlord(@Param("houseId") Integer houseId,
                                         @Param("userId") Integer userId,
                                         @Param("pageNo") Integer pageNo,
                                         @Param("pageSize") Integer pageSize);

    int getTotalByConditionForLandlord(@Param("houseId") Integer houseId,
                                       @Param("userId") Integer userId);

    List<Repair> getByConditionForUser(@Param("houseId") Integer houseId,
                                     @Param("userId") Integer userId,
                                     @Param("pageNo") Integer pageNo,
                                     @Param("pageSize") Integer pageSize);

    int getTotalByConditionForUser(@Param("houseId") Integer houseId,
                                   @Param("userId") Integer userId);
}
