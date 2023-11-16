package com.anju.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anju.springboot.common.AutoLog;
import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.DicValue;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.service.DicValueService;
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
@RequestMapping("/dicValue")
public class DicValueController {

    @Autowired
    private DicValueService dicValueService;

    @GetMapping("/search")
    public Result search(Param param){
        Page<DicValue> page = new Page<>(param.getCurrentPage(),param.getPageSize());
        LambdaQueryWrapper<DicValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DicValue::getCode,param.getCode())
                    .like(StringUtils.isNotEmpty(param.getName()),DicValue::getName,param.getName());
        dicValueService.page(page,queryWrapper);

        return Result.success(page);
    }

    @PostMapping("/save")
    @AutoLog("新增字典值")
    public Result save(@RequestBody DicValue dicValue) throws Exception {
        return dicValueService.saveDicValue(dicValue);
    }

    @PutMapping("/update")
    @AutoLog("修改字典值")
    public Result update(@RequestBody DicValue dicValue) throws Exception {
        return dicValueService.updateDicValue(dicValue);
    }

    @DeleteMapping("/deleteById/{id}")
    @AutoLog("删除字典值")
    public Result deleteById(@PathVariable("id") Integer id) throws Exception {
        boolean flag = dicValueService.removeById(id);
        if (flag) {
            return Result.success();
        } else {
            throw new Exception();
        }
    }

    @DeleteMapping("/deleteByIds/{ids}")
    @AutoLog("批量删除字典值")
    public Result deleteByIds(@PathVariable("ids") List<Integer> ids) throws Exception {
        boolean flag = dicValueService.removeByIds(ids);
        if (flag) {
            return Result.success();
        } else {
            throw new Exception();
        }
    }

    @GetMapping("/getMenuIconList")
    public Result getMenuIconList(){

        LambdaQueryWrapper<DicValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DicValue::getCode, Constant.TYPE_CODE_MENU_ICON);

        return Result.success(dicValueService.list(queryWrapper));
    }

    @GetMapping("/getHouseTagList")
    public Result getHouseTagList(){

        LambdaQueryWrapper<DicValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DicValue::getCode, Constant.TYPE_CODE_HOUSE_TAG);

        return Result.success(dicValueService.list(queryWrapper));
    }

    @GetMapping("/getHouseConfigList")
    public Result getHouseConfigList(){
        LambdaQueryWrapper<DicValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DicValue::getCode, Constant.TYPE_CODE_HOUSE_CONFIG_ICON);

        return Result.success(dicValueService.list(queryWrapper));
    }

}
