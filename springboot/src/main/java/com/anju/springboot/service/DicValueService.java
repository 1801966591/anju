package com.anju.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.DicValue;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
public interface DicValueService extends IService<DicValue> {

    Result saveDicValue(DicValue dicValue) throws Exception;

    Result updateDicValue(DicValue dicValue) throws Exception;
}
