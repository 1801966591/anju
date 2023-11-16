package com.anju.springboot.service;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anju.springboot.entity.param.Param;

/**
 * <p>
 * 租赁订单表 服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
public interface OrderService extends IService<Order> {

    Result search(Param param);

    void updateStatus(String tradeNo, int payStatus, String gmtPayment, String alipayTradeNo);

    Result detail(Integer id);

    Result cancelOrder(Integer id) throws Exception;

    Result getMoney();

    Result getCount();
}
