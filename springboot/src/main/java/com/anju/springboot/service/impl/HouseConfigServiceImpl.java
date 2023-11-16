package com.anju.springboot.service.impl;

import com.anju.springboot.entity.HouseConfig;
import com.anju.springboot.mapper.HouseConfigMapper;
import com.anju.springboot.service.HouseConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 房源配置表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-03
 */
@Service
public class HouseConfigServiceImpl extends ServiceImpl<HouseConfigMapper, HouseConfig> implements HouseConfigService {

}
