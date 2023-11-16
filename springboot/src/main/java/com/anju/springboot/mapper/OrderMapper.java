package com.anju.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.anju.springboot.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 租赁订单表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    List<Order> getByConditionForLandlord(@Param("houseId") Integer houseId,
                                          @Param("name") String name,
                                          @Param("userId") Integer userId,
                                          @Param("pageNo") Integer pageNo,
                                          @Param("pageSize") Integer pageSize);
    int getTotalByConditionForLandlord(@Param("houseId") Integer houseId,
                                       @Param("name") String name,
                                       @Param("userId") Integer userId);
    List<Order> getByConditionForUser(@Param("houseId") Integer houseId,
                                      @Param("name") String name,
                                      @Param("userId") Integer userId,
                                      @Param("pageNo") Integer pageNo,
                                      @Param("pageSize") Integer pageSize);
    int getTotalByConditionForUser(@Param("houseId") Integer houseId,
                                   @Param("name") String name,
                                   @Param("userId") Integer userId);

    @Update("""
            update rent_order
                set
                    pay_status=#{payStatus},
                    payment_time=#{paymentTime},
                    alipay_no=#{alipayNo}
                where
                    no=#{no}
            """)
    void updatePayStatus(@Param("no") String no,
                         @Param("payStatus") Integer payStatus,
                         @Param("paymentTime") String paymentTime,
                         @Param("alipayNo") String alipayNo);

    @Select("""
            select * from rent_order where no=#{no}
            """)
    Order getByNo(String no);

    @Select("""
            select *
            from rent_order
            where rent_id=#{rentId}
            order by payment_time desc
            limit 1
            """)
    Order getLatestByRentId(Integer rentId);
}
