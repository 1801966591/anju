package com.anju.springboot.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.DicType;
import com.anju.springboot.exception.CustomException;
import com.anju.springboot.mapper.DicTypeMapper;
import com.anju.springboot.mapper.DicValueMapper;
import com.anju.springboot.service.DicTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Service
public class DicTypeServiceImpl extends ServiceImpl<DicTypeMapper, DicType> implements DicTypeService {

    @Autowired
    private DicTypeMapper dicTypeMapper;

    @Autowired
    private DicValueMapper dicValueMapper;

    @Override
    public Result saveDicType(DicType dicType) throws Exception {

        DicType byName = dicTypeMapper.getByName(dicType.getName());
        DicType byCode = dicTypeMapper.getByCode(dicType.getCode());

        if (ObjectUtil.isNotNull(byName)){
            throw new CustomException("该字典类型名称已存在");
        } else if (ObjectUtil.isNotNull(byCode)) {
            throw new CustomException("该字典类型编码已存在");
        } else {
            int i = dicTypeMapper.insert(dicType);
            if (i == 1) {
                return Result.success();
            } else {
                throw new Exception();
            }
        }
    }

    @Override
    public Result updateDicType(DicType dicType) throws Exception {

        DicType byName = dicTypeMapper.getByName(dicType.getName());

        if (ObjectUtil.isNotNull(byName) && !byName.getName().equals(dicType.getName())) {
            throw new CustomException("该字典类型名称已存在");
        } else {
            int i = dicTypeMapper.updateDicType(dicType);

            if (i == 1) {
                return Result.success();
            } else {
                throw new Exception();
            }
        }
    }

    @Override
    public Result deleteByCode(String code) throws Exception {

        //删除该字典类型对应的字典值
        int i = dicValueMapper.deleteByCode(code);

        //删除该字典类型
        i += dicTypeMapper.deleteByCode(code);

        if (i > 0){
            return Result.success();
        } else {
            throw new Exception();
        }

    }

    @Override
    public Result deleteByCodes(List<String> codes) throws Exception {

        int i = 0;

        for (String code : codes){
            //删除该字典类型对应的字典值
            i += dicValueMapper.deleteByCode(code);

            //删除该字典类型
            i += dicTypeMapper.deleteByCode(code);
        }

        if (i > 0){
            return Result.success();
        } else {
            throw new Exception();
        }
    }
}
