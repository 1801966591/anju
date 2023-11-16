package com.anju.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.House;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anju.springboot.entity.param.HouseParam;
import com.anju.springboot.entity.vo.HouseVo;

import java.util.List;

/**
 * <p>
 * 房屋信息表 服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-26
 */
public interface HouseService extends IService<House> {

    Result upload(HouseParam param) throws Exception;

    HouseVo search(Page<House> housePage);

    Result detail(Integer id);

    Result updateHouse(HouseParam param) throws Exception;

    int deleteById(Integer id);

    Result deleteByIds(List<Integer> ids) throws Exception;

    List<HouseVo> userSearch(HouseParam param,List<House> houses);

    void searchRent(House house);
}
