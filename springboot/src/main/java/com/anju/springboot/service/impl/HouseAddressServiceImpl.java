package com.anju.springboot.service.impl;

import com.anju.springboot.entity.HouseAddress;
import com.anju.springboot.mapper.HouseAddressMapper;
import com.anju.springboot.service.HouseAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 房屋地址表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-27
 */
@Service
public class HouseAddressServiceImpl extends ServiceImpl<HouseAddressMapper, HouseAddress> implements HouseAddressService {

}
