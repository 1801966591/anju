package com.anju.springboot.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.*;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.entity.vo.RentAuditVo;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.*;
import com.anju.springboot.service.RentAuditService;
import com.anju.springboot.service.RoleService;
import com.anju.springboot.utils.JwtTokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 租赁审核表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Service
public class RentAuditServiceImpl extends ServiceImpl<RentAuditMapper, RentAudit> implements RentAuditService {

    @Autowired
    private RentAuditMapper auditMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RentAuditOrderMapper auditOrderMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RentMapper rentMapper;

    @Override
    public Result apply(RentAudit audit) throws Exception {

        User user = JwtTokenUtils.getCurrentUser();
        if (user == null){
            throw new TokenException("登录失效");
        }

        audit.setUserId(user.getId());
        audit.setAuditStatus(0);
        audit.setApplyType(1);
        audit.setApplyTime(DateUtil.now());
        int i = auditMapper.insert(audit);

        if (i == 1){
            //下架房源
            Integer houseId = audit.getHouseId();
            House house = houseMapper.selectById(houseId);
            //判断出租类型是不是合租
            if (house.getRentType() == 1){
                //合租
                if (house.getRentRoomNumber() - 1 == 0){
                    //如果是最后一个房间，则下架房源
                    house.setListingStatus(0);
                }
            }else {
                house.setListingStatus(0);
            }
            houseMapper.updateById(house);

            String content = "有用户提交了【"+ house.getName() +"】的租赁申请，快去确认吧";
            messageMapper.saveMessage("用户提交租赁申请",content,house.getAuthorId(),user.getId(),0,DateUtil.now());

            return Result.success();
        }else {
            throw new Exception();
        }

    }

    @Override
    public Result search(Param param) {

        User user = JwtTokenUtils.getCurrentUser();
        if (user == null){
            throw new TokenException("登录失效");
        }
        String role = roleService.getRoleByUserId(user.getId());
        Integer currentPage = param.getCurrentPage();
        Integer pageSize = param.getPageSize();
        Integer pageNo = (currentPage - 1) * pageSize;
        int total = 0;

        List<RentAuditVo> list = new ArrayList<>();
        RentAuditVo vo = new RentAuditVo();

        //判断房源名称是否为空
        if (StringUtils.isNotEmpty(param.getName())) {
            LambdaQueryWrapper<House> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(House::getName,param.getName());
            List<House> houses = houseMapper.selectList(queryWrapper);

            for (House house : houses){
                Integer houseId = house.getId();
                List<RentAudit> audits = new ArrayList<>();
                if (role.equals(Constant.ROLE_LANDLORD)){
                    audits = auditMapper.getByConditionForLandlord(houseId,param.getRealName(),user.getId(),pageNo,pageSize);
                    total = auditMapper.getTotalByConditionForLandlord(houseId,param.getRealName(),user.getId());
                }else if (role.equals(Constant.ROLE_USER)){
                    audits = auditMapper.getByConditionForUser(houseId,param.getRealName(),user.getId(),pageNo,pageSize);
                    total = auditMapper.getTotalByConditionForUser(houseId,param.getRealName(),user.getId());
                }
                searchRentAuditVo(audits, list);
            }
        } else {
            List<RentAudit> audits = new ArrayList<>();
            if (role.equals(Constant.ROLE_LANDLORD)){
                audits = auditMapper.getByConditionForLandlord(null,param.getRealName(),user.getId(),pageNo,pageSize);
                total = auditMapper.getTotalByConditionForLandlord(null,param.getRealName(),user.getId());
            }else if (role.equals(Constant.ROLE_USER)){
                audits = auditMapper.getByConditionForUser(null,param.getRealName(),user.getId(),pageNo,pageSize);
                total = auditMapper.getTotalByConditionForUser(null,param.getRealName(),user.getId());
            }
            searchRentAuditVo(audits,list);
        }

        vo.setRecords(list);
        vo.setTotal(total);

        return Result.success(vo);
    }

    @Override
    public Result audit(RentAudit rentAudit) throws Exception {

        User user = JwtTokenUtils.getCurrentUser();
        if (user == null){
            throw new TokenException("登录失效");
        }

        //审核
        int i = auditMapper.updateById(rentAudit);

        if (i == 1){
            RentAudit audit = auditMapper.selectById(rentAudit.getId());
            House house = houseMapper.selectById(audit.getHouseId());
            String type = "";
            if (audit.getApplyType() == 1){
                type = "租赁申请";
            }else {
                type = "续租申请";
            }
            if (audit.getAuditStatus() == 1){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                //审核通过，创建订单
                Order order = new Order();
                BeanUtils.copyProperties(audit,order, "id");
                order.setNo(DateUtil.format(new Date(),"yyyyMMd") + System.currentTimeMillis());
                order.setPayStatus(0);
                order.setTotalAmount(house.getRent() * audit.getTerm());
                order.setTerm(audit.getTerm());
                order.setCreateTime(DateUtil.now());
                order.setRentId(audit.getRentId());
                order.setOrderType(audit.getApplyType());

                if (order.getOrderType() == 1){
                    //首次
                    order.setDueTime(LocalDateTime.now().plusMonths(order.getTerm()).format(formatter));
                }else {
                    //续租
                    Rent rent = rentMapper.selectById(order.getRentId());
                    LocalDateTime dueTime = LocalDateTime.parse(rent.getDueTime(), formatter);
                    order.setDueTime(dueTime.plusMonths(order.getTerm()).format(formatter));
                }

                orderMapper.insert(order);

                RentAuditOrder rentAuditOrder = new RentAuditOrder();
                rentAuditOrder.setOrderId(order.getId());
                rentAuditOrder.setAuditId(audit.getId());

                auditOrderMapper.insert(rentAuditOrder);

                String content1 = "您提交的【"+ house.getName() +"】的"+type+"的审核通过啦，快去看看吧";
                messageMapper.saveMessage("房东审核"+type,content1,audit.getUserId(),user.getId(),0,DateUtil.now());

                Thread.sleep(1000);

                String content2 = "您有一笔新的待支付订单，请在两小时内完成支付";
                messageMapper.saveMessage("待支付订单提醒",content2,audit.getUserId(),user.getId(),0,DateUtil.now());
            }else {
                String content = "您提交的【"+ house.getName() +"】的"+type+"的审核未通过，快去看看吧";
                messageMapper.saveMessage("房东审核"+type,content,audit.getUserId(),user.getId(),0,DateUtil.now());
            }
            return Result.success();
        }else {
            throw new Exception();
        }

    }

    @Override
    public Result relet(RentAudit audit) throws Exception {

        User user = JwtTokenUtils.getCurrentUser();
        if (user == null){
            throw new TokenException("登录失效");
        }

        audit.setUserId(user.getId());
        audit.setAuditStatus(0);
        audit.setApplyType(2);
        audit.setApplyTime(DateUtil.now());
        int i = auditMapper.insert(audit);

        if (i == 1){
            //申请成功
            House house = houseMapper.selectById(audit.getHouseId());
            String content = "有用户提交了【"+ house.getName() +"】的续租申请，快去确认吧";
            messageMapper.saveMessage("用户提交续租申请",content,house.getAuthorId(),user.getId(),0,DateUtil.now());
            return Result.success();
        }else {
            throw new Exception();
        }
    }

    private void searchRentAuditVo(List<RentAudit> audits, List<RentAuditVo> list){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (RentAudit rentAudit : audits) {
            LocalDateTime createTime = LocalDateTime.parse(rentAudit.getApplyTime(), formatter);
            RentAudit audit = auditMapper.selectById(rentAudit.getId());
            House house = houseMapper.selectById(audit.getHouseId());

            if (audit.getAuditStatus() == 0 && LocalDateTime.now().isAfter(createTime.plusHours(24))){
                //房东超时未确认
                audit.setAuditStatus(3);
                auditMapper.updateById(audit);
                if (audit.getApplyType() == 1){
                    //首次申请，上架房源
                    if (house.getRentType() == 1){
                        //合租
                        if (house.getListingStatus() == 0){
                            house.setListingStatus(1);
                            house.setRentStatus(0);
                            house.setListTime(DateUtil.now());
                            house.setRentRoomNumber(1);
                        }else {
                            house.setRentRoomNumber(house.getRentRoomNumber() + 1);
                        }
                    }else {
                        house.setListingStatus(1);
                        house.setRentStatus(0);
                        house.setListTime(DateUtil.now());
                    }
                    houseMapper.updateById(house);
                }
            }

            RentAuditVo rentAuditVo = new RentAuditVo();
            BeanUtils.copyProperties(audit,rentAuditVo);
            rentAuditVo.setHouseName(house.getName());

            list.add(rentAuditVo);
        }
    }
}
