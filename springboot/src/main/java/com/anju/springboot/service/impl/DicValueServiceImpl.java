package com.anju.springboot.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.DicValue;
import com.anju.springboot.exception.CustomException;
import com.anju.springboot.mapper.DicValueMapper;
import com.anju.springboot.service.DicValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Service
public class DicValueServiceImpl extends ServiceImpl<DicValueMapper, DicValue> implements DicValueService {

    @Autowired
    private DicValueMapper dicValueMapper;

    @Override
    public Result saveDicValue(DicValue dicValue) throws Exception {

        DicValue byName = dicValueMapper.getByName(dicValue.getName());
        DicValue byValue = dicValueMapper.getByValue(dicValue.getValue());

        if (ObjectUtil.isNotNull(byName)) {
            throw new CustomException("该字典名称已存在");
        } else if (ObjectUtil.isNotNull(byValue)) {
            throw new CustomException("该字典值已存在");
        } else {
            int insert = dicValueMapper.insert(dicValue);
            if (insert == 1){
                return Result.success();
            } else {
                throw new Exception();
            }
        }
    }

    @Override
    public Result updateDicValue(DicValue dicValue) throws Exception {

        DicValue byName = dicValueMapper.getByName(dicValue.getName());
        DicValue byValue = dicValueMapper.getByValue(dicValue.getValue());

        if (ObjectUtil.isNotNull(byName) && !byName.getName().equals(dicValue.getName())) {
            throw new CustomException("该字典名称已存在");
        } else if (ObjectUtil.isNotNull(byValue) && !byValue.getValue().equals(dicValue.getValue())) {
            throw new CustomException("该字典值已存在");
        } else {
            int insert = dicValueMapper.updateById(dicValue);
            if (insert == 1){
                return Result.success();
            } else {
                throw new Exception();
            }
        }
    }
}
