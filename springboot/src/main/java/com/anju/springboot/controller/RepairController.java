package com.anju.springboot.controller;

import cn.hutool.core.date.DateUtil;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.House;
import com.anju.springboot.entity.Repair;
import com.anju.springboot.entity.User;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.MessageMapper;
import com.anju.springboot.service.HouseService;
import com.anju.springboot.service.RepairService;
import com.anju.springboot.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 报修信息表 前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-18
 */
@RestController
@RequestMapping("/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private HouseService houseService;

    @PostMapping("/apply")
    public Result apply(@RequestBody Repair repair) throws Exception {

        repair.setCreateTime(DateUtil.now());
        repair.setRepairStatus(1);
        repair.setRepairType(1);
        repair.setRepairTimes(1);

        boolean flag = repairService.save(repair);

        if (flag) {

            House house = houseService.getById(repair.getHouseId());
            String content = "有用户提交了【"+ house.getName() +"】的报修信息，快去看看吧";
            messageMapper.saveMessage("用户提交报修信息",content,house.getAuthorId(),repair.getUserId(),0, DateUtil.now());

            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @GetMapping("/search")
    public Result search(Param param){
        return repairService.search(param);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable("id") Integer id){
        return repairService.detail(id);
    }

    @PutMapping("/handle/{id}")
    public Result handle(@PathVariable("id") Integer id) throws Exception {

        User user = JwtTokenUtils.getCurrentUser();
        if (user == null){
            throw new TokenException("登录失效");
        }

        Repair repair = repairService.getById(id);
        repair.setRepairStatus(2);
        repair.setRepairTime(DateUtil.now());

        boolean flag = repairService.updateById(repair);

        if (flag) {

            House house = houseService.getById(repair.getHouseId());

            String content = "您提交的【"+ house.getName() +"】的报修信息房东已经处理啦，快去看看吧";
            messageMapper.saveMessage("房东处理报修信息",content,repair.getUserId(),user.getId(),0,DateUtil.now());

            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @PutMapping("/reApply")
    public Result reApply(@RequestBody Repair repair) throws Exception {

        repair.setRepairStatus(1);
        repair.setRepairTime("");
        repair.setCreateTime(DateUtil.now());
        repair.setRepairType(2);
        repair.setRepairTimes(repair.getRepairTimes() + 1);

        boolean flag = repairService.updateById(repair);

        if (flag){
            House house = houseService.getById(repair.getHouseId());
            String content = "有用户再次提交了【"+ house.getName() +"】的报修信息，快去看看吧";
            messageMapper.saveMessage("用户再次提交报修信息",content,house.getAuthorId(),repair.getUserId(),0, DateUtil.now());

            return Result.success();
        }else {
            throw new Exception();
        }

    }

    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable("id") Integer id) throws Exception {

        Repair repair = repairService.getById(id);
        repair.setRepairStatus(3);
        boolean flag = repairService.updateById(repair);

        if (flag){
            House house = houseService.getById(repair.getHouseId());
            String content = "【"+ house.getName() +"】的报修完成啦，快去看看吧";
            messageMapper.saveMessage("用户确认报修完成",content,house.getAuthorId(),repair.getUserId(),0, DateUtil.now());

            return Result.success();
        }else {
            throw new Exception();
        }
    }


}
