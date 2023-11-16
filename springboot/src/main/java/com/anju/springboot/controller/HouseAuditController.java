package com.anju.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.anju.springboot.common.AutoLog;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.House;
import com.anju.springboot.entity.HouseAudit;
import com.anju.springboot.entity.User;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.MessageMapper;
import com.anju.springboot.service.HouseAuditService;
import com.anju.springboot.service.HouseService;
import com.anju.springboot.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 房源审核表 前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-29
 */
@RestController
@RequestMapping("/houseAudit")
public class HouseAuditController {

    @Autowired
    private HouseAuditService houseAuditService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private MessageMapper messageMapper;

    @GetMapping("/search")
    public Result search(Param param){
        return houseAuditService.search(param);
    }

    @PutMapping("/audit")
    @AutoLog("审核房源")
    public Result audit(@RequestBody HouseAudit audit) throws Exception {

        User user = JwtTokenUtils.getCurrentUser();
        //判断登录是否失效
        if (ObjectUtil.isNull(user)) {
            throw new TokenException("登录失效");
        }
        boolean flag = houseAuditService.updateById(audit);
        if (flag){
            //审核成功，提醒房东
            House house = houseService.getById(audit.getHouseId());
            String content = "您的房源【"+ house.getName() +"】的审核状态有变化，快去看看吧";
            messageMapper.saveMessage("房源审核状态变更",content,house.getAuthorId(),
                    user.getId(),0, DateUtil.now());
            return Result.success();
        }else {
            throw new Exception();
        }

    }

}
