package com.anju.springboot.service;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.ReserveAudit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anju.springboot.entity.param.Param;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-06
 */
public interface ReserveAuditService extends IService<ReserveAudit> {
    Result search(Param param);
}
