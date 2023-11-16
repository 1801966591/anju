package com.anju.springboot.service.impl;

import com.anju.springboot.entity.Area;
import com.anju.springboot.mapper.AreaMapper;
import com.anju.springboot.service.AreaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 区县表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

}
