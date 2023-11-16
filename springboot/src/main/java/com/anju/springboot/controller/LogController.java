package com.anju.springboot.controller;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Log;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.service.LogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 操作日志表 前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/search")
    public Result list(Param param){

        Page<Log> pageInfo = new Page<>(param.getCurrentPage(),param.getPageSize());
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(param.getName()),"name",param.getName())
                .orderByDesc("time");
        logService.page(pageInfo,queryWrapper);

        return Result.success(pageInfo);

    }

    @GetMapping("/getLastLoginTime")
    public Result getLastLoginTime(){
        return logService.getLastLoginTime();
    }

}
