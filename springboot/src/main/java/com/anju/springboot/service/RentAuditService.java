package com.anju.springboot.service;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.RentAudit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anju.springboot.entity.param.Param;

/**
 * <p>
 * 租赁审核表 服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
public interface RentAuditService extends IService<RentAudit> {

    Result apply(RentAudit audit) throws Exception;

    Result search(Param param);

    Result audit(RentAudit rentAudit) throws Exception;

    Result relet(RentAudit audit) throws Exception;
}
