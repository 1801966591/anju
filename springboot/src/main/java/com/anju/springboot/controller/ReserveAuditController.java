package com.anju.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.anju.springboot.common.AutoLog;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.House;
import com.anju.springboot.entity.Reserve;
import com.anju.springboot.entity.ReserveAudit;
import com.anju.springboot.entity.User;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.MessageMapper;
import com.anju.springboot.service.HouseService;
import com.anju.springboot.service.ReserveAuditService;
import com.anju.springboot.service.ReserveService;
import com.anju.springboot.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-06
 */
@RestController
@RequestMapping("/reserveAudit")
public class ReserveAuditController {

    @Autowired
    private ReserveAuditService auditService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private ReserveService reserveService;

    @Autowired
    private MessageMapper messageMapper;

    @GetMapping("/search")
    public Result search(Param param){
        return auditService.search(param);
    }

    @PutMapping("/audit")
    @AutoLog("房东审核预约看房")
    public Result audit(@RequestBody ReserveAudit audit) throws Exception {

        User user = JwtTokenUtils.getCurrentUser();
        if (ObjectUtil.isNull(user)) {
            throw new TokenException("登录失效");
        }
        boolean flag = auditService.updateById(audit);
        if (flag) {
            ReserveAudit byId = auditService.getById(audit.getId());
            Reserve reserve = reserveService.getById(byId.getReserveId());
            House house = houseService.getById(reserve.getHouseId());
            String content = "";
            if (audit.getStatus() == 1){
                //审核通过
                content = "您预约的【"+ house.getName() +"】的看房时间的审核通过啦，快去看看吧";
            }else if (audit.getStatus() == 2){
                //审核不通过
                content = "您预约的【"+ house.getName() +"】的看房时间的审核未通过，快去看看吧";
            }
            messageMapper.saveMessage("房东审核预约",content,reserve.getUserId(),user.getId(),0, DateUtil.now());
            return Result.success();
        } else {
            throw new Exception();
        }
    }

}
