package com.anju.springboot.mapper;

import com.anju.springboot.entity.HouseAudit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 房源审核表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-29
 */
@Mapper
public interface HouseAuditMapper extends BaseMapper<HouseAudit> {

    @Select("""
            select * from house_audit where house_id=#{houseId}
            """)
    HouseAudit getByHouseId(@Param("houseId") Integer houseId);

    @Update("""
            update house_audit set status=0,content='' where house_id=#{houseId}
            """)
    void updateStatusByHouseId(@Param("houseId") Integer houseId);
}
