package com.anju.springboot.service;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Reserve;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-06
 */
public interface ReserveService extends IService<Reserve> {

    Result reserve(Reserve reserve) throws Exception;

    Result updateReserve(Reserve reserve) throws Exception;

    Result cancelReserve(Integer id) throws Exception;
}
