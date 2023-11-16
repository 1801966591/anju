package com.anju.springboot.service.impl;

import com.anju.springboot.entity.City;
import com.anju.springboot.mapper.CityMapper;
import com.anju.springboot.service.CityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 市表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

}
