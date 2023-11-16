package com.anju.springboot.service;

import com.alipay.api.AlipayApiException;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Rent;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anju.springboot.entity.param.Param;

/**
 * <p>
 * 租赁信息表 服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-18
 */
public interface RentService extends IService<Rent> {

    Result search(Param param);

    Result rentOut(Integer id) throws AlipayApiException;

    Result getCount();
}
