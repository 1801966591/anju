package com.anju.springboot.mapper;

import com.anju.springboot.entity.ReserveAudit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-06
 */
@Mapper
public interface ReserveAuditMapper extends BaseMapper<ReserveAudit> {

    @Select("""
            select *
            from reserve_audit
            where reserve_id=#{reserveId}
            """)
    ReserveAudit getByReserveId(@Param("reserveId") Integer reserveId);
}
