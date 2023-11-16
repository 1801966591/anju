package com.anju.springboot.mapper;

import com.anju.springboot.entity.RentAudit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 租赁审核表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Mapper
public interface RentAuditMapper extends BaseMapper<RentAudit> {

    List<RentAudit> getByConditionForLandlord(@Param("houseId") Integer houseId,
                                              @Param("name") String name,
                                              @Param("userId") Integer userId,
                                              @Param("pageNo") Integer pageNo,
                                              @Param("pageSize") Integer pageSize);
    int getTotalByConditionForLandlord(@Param("houseId") Integer houseId,
                            @Param("name") String name,
                            @Param("userId") Integer userId);
    List<RentAudit> getByConditionForUser(@Param("houseId") Integer houseId,
                                              @Param("name") String name,
                                              @Param("userId") Integer userId,
                                              @Param("pageNo") Integer pageNo,
                                              @Param("pageSize") Integer pageSize);
    int getTotalByConditionForUser(@Param("houseId") Integer houseId,
                                       @Param("name") String name,
                                       @Param("userId") Integer userId);
}
