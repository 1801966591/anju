package com.anju.springboot.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anju.springboot.common.AutoLog;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Notice;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.service.NoticeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 系统公告表 前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/search")
    public Result search(Param param){

        Page<Notice> pageInfo = new Page<>(param.getCurrentPage(),param.getPageSize());
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(param.getTitle()),"title",param.getTitle())
                .orderByDesc("time");
        noticeService.page(pageInfo,queryWrapper);

        return Result.success(pageInfo);

    }

    @PostMapping("/save")
    @AutoLog("新增公告信息")
    public Result saveRole(@RequestBody Notice notice) throws Exception {

        notice.setTime(DateUtil.now());
        boolean flag = noticeService.save(notice);

        if (flag) {
            return Result.success();
        }else {
            throw new Exception();
        }

    }

    @PutMapping("/update")
    @AutoLog("修改公告信息")
    public Result update(@RequestBody Notice notice) throws Exception {

        notice.setTime(DateUtil.now());
        boolean flag = noticeService.updateById(notice);

        if (flag) {
            return Result.success();
        }else {
            throw new Exception();
        }

    }

    @DeleteMapping("/deleteById/{id}")
    @AutoLog("删除公告信息")
    public Result deleteById(@PathVariable("id") Integer id) throws Exception {

        boolean flag = noticeService.removeById(id);

        if (flag) {
            return Result.success();
        }else {
            throw new Exception();
        }

    }

    @DeleteMapping("/deleteByIds/{ids}")
    @AutoLog("批量删除公告信息")
    public Result deleteById(@PathVariable("ids") List<Integer> ids) throws Exception {

        boolean flag = noticeService.removeByIds(ids);

        if (flag) {
            return Result.success();
        }else {
            throw new Exception();
        }

    }

    @GetMapping("/getFiveNotice")
    public Result get5Notice(){

        Page<Notice> page = new Page<>(1,5);
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Notice::getTime);
        noticeService.page(page,queryWrapper);
        return Result.success(page.getRecords());
    }

}
