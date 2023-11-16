package com.anju.springboot.service;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.HouseAudit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anju.springboot.entity.param.Param;

/**
 * <p>
 * 房源审核表 服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-29
 */
public interface HouseAuditService extends IService<HouseAudit> {

    Result search(Param param);
}
