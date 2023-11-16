package com.anju.springboot.service;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.DicType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
public interface DicTypeService extends IService<DicType> {

    Result saveDicType(DicType dicType) throws Exception;

    Result updateDicType(DicType dicType) throws Exception;

    Result deleteByCode(String code) throws Exception;

    Result deleteByCodes(List<String> codes) throws Exception;
}
