package com.anju.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anju.springboot.common.AutoLog;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.DicType;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.service.DicTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@RestController
@RequestMapping("/dicType")
public class DicTypeController {

    @Autowired
    private DicTypeService dicTypeService;

    @GetMapping("/search")
    public Result search(Param param){

        Page<DicType> page = new Page<>(param.getCurrentPage(),param.getPageSize());
        LambdaQueryWrapper<DicType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(param.getName()),DicType::getName,param.getName());
        dicTypeService.page(page,queryWrapper);

        return Result.success(page);

    }

    @PostMapping("/save")
    @AutoLog("新增字典类型")
    public Result save(@RequestBody DicType dicType) throws Exception {
        return dicTypeService.saveDicType(dicType);
    }

    @PutMapping("/update")
    @AutoLog("修改字典类型")
    public Result update(@RequestBody DicType dicType) throws Exception {
        return dicTypeService.updateDicType(dicType);
    }

    @DeleteMapping("deleteByCode/{code}")
    @AutoLog("删除字典类型")
    private Result deleteByCode(@PathVariable("code") String code) throws Exception {
        return dicTypeService.deleteByCode(code);
    }

    @DeleteMapping("deleteByCodes/{codes}")
    @AutoLog("批量删除字典类型")
    private Result deleteByCode(@PathVariable("codes") List<String> codes) throws Exception {
        return dicTypeService.deleteByCodes(codes);
    }

}
