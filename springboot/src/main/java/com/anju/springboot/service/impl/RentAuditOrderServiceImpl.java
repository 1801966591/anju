package com.anju.springboot.service.impl;

import com.anju.springboot.entity.RentAuditOrder;
import com.anju.springboot.mapper.RentAuditOrderMapper;
import com.anju.springboot.service.RentAuditOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审核订单关系表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Service
public class RentAuditOrderServiceImpl extends ServiceImpl<RentAuditOrderMapper, RentAuditOrder> implements RentAuditOrderService {

}
