package com.anju.springboot.controller;

import cn.hutool.core.date.DateUtil;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.House;
import com.anju.springboot.entity.RentAudit;
import com.anju.springboot.entity.User;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.MessageMapper;
import com.anju.springboot.service.HouseService;
import com.anju.springboot.service.RentAuditService;
import com.anju.springboot.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 租赁审核表 前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@RestController
@RequestMapping("/rentAudit")
public class RentAuditController {

    @Autowired
    private RentAuditService rentAuditService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private MessageMapper messageMapper;

    @PostMapping("/apply")
    public Result apply(@RequestBody RentAudit audit) throws Exception {
        return rentAuditService.apply(audit);
    }

    @GetMapping("/search")
    public Result search(Param param){
        return rentAuditService.search(param);
    }

    @PutMapping("/update")
    public Result update(@RequestBody RentAudit audit) throws Exception {

        User user = JwtTokenUtils.getCurrentUser();
        if (user == null){
            throw new TokenException("登录失效");
        }

        audit.setAuditStatus(0);
        audit.setUpdateTime(DateUtil.now());
        boolean flag = rentAuditService.updateById(audit);

        if (flag) {
            RentAudit byId = rentAuditService.getById(audit.getId());
            Integer houseId = byId.getHouseId();
            House house = houseService.getById(houseId);
            String content = "有用户修改了【"+ house.getName() +"】的租赁申请，快去确认吧";
            messageMapper.saveMessage("用户修改租赁申请",content,house.getAuthorId(),user.getId(),0,DateUtil.now());
            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @DeleteMapping("/cancelApply/{id}")
    public Result cancelApply(@PathVariable("id") Integer id) throws Exception {

        User user = JwtTokenUtils.getCurrentUser();
        if (user == null){
            throw new TokenException("登录失效");
        }

        RentAudit audit = rentAuditService.getById(id);
        audit.setAuditStatus(4);
        boolean flag = rentAuditService.updateById(audit);

        if (flag){
            //重新上架
            Integer houseId = audit.getHouseId();
            House house = houseService.getById(houseId);
            house.setListingStatus(1);
            house.setListTime(DateUtil.now());
            houseService.updateById(house);

            String content = "有用户取消了【"+ house.getName() +"】的租赁申请，快去看看吧";
            messageMapper.saveMessage("用户取消租赁申请",content,house.getAuthorId(),user.getId(),0,DateUtil.now());

            return Result.success();
        }else {
            throw new Exception();
        }

    }

    @PutMapping("/audit")
    public Result audit(@RequestBody RentAudit rentAudit) throws Exception {
        return rentAuditService.audit(rentAudit);
    }

    @PostMapping("/relet")
    public Result relet(@RequestBody RentAudit audit) throws Exception{
        return rentAuditService.relet(audit);
    }

}
