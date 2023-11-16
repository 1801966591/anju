package com.anju.springboot.mapper;

import com.anju.springboot.entity.RentAuditOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 审核订单关系表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Mapper
public interface RentAuditOrderMapper extends BaseMapper<RentAuditOrder> {

    @Select("""
            select * from rent_audit_order where order_id=#{orderId}
            """)
    RentAuditOrder getByOrderId(@Param("orderId") Integer orderId);
}
