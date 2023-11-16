package com.anju.springboot.service;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Repair;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anju.springboot.entity.param.Param;

/**
 * <p>
 * 报修信息表 服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-18
 */
public interface RepairService extends IService<Repair> {

    Result search(Param param);

    Result detail(Integer id);
}
