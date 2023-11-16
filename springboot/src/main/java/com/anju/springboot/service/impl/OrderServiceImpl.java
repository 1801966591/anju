package com.anju.springboot.service.impl;

import cn.hutool.core.date.DateUtil;
import com.anju.springboot.entity.vo.CountVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.*;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.entity.vo.OrderVo;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.*;
import com.anju.springboot.service.OrderService;
import com.anju.springboot.service.RoleService;
import com.anju.springboot.utils.JwtTokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 租赁订单表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private RentAuditOrderMapper auditOrderMapper;

    @Autowired
    private RentAuditMapper auditMapper;

    @Autowired
    private RentMapper rentMapper;

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

        List<OrderVo> list = new ArrayList<>();
        OrderVo vo = new OrderVo();

        //判断房源名称是否为空
        if (StringUtils.isNotEmpty(param.getName())) {
            LambdaQueryWrapper<House> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(House::getName,param.getName());
            List<House> houses = houseMapper.selectList(queryWrapper);

            for (House house : houses){
                Integer houseId = house.getId();
                List<Order> orders = new ArrayList<>();
                if (role.equals(Constant.ROLE_LANDLORD)){
                    orders = orderMapper.getByConditionForLandlord(houseId,param.getRealName(),user.getId(),pageNo,pageSize);
                    total = orderMapper.getTotalByConditionForLandlord(houseId,param.getRealName(),user.getId());
                }else if (role.equals(Constant.ROLE_USER)){
                    orders = orderMapper.getByConditionForUser(houseId,param.getRealName(),user.getId(),pageNo,pageSize);
                    total = orderMapper.getTotalByConditionForUser(houseId,param.getRealName(),user.getId());
                }
                searchRentOrderVo(orders, list);
            }
        } else {
            List<Order> orders = new ArrayList<>();
            if (role.equals(Constant.ROLE_LANDLORD)){
                orders = orderMapper.getByConditionForLandlord(null,param.getRealName(),user.getId(),pageNo,pageSize);
                total = orderMapper.getTotalByConditionForLandlord(null,param.getRealName(),user.getId());
            }else if (role.equals(Constant.ROLE_USER)){
                orders = orderMapper.getByConditionForUser(null,param.getRealName(),user.getId(),pageNo,pageSize);
                total = orderMapper.getTotalByConditionForUser(null,param.getRealName(),user.getId());
            }
            searchRentOrderVo(orders,list);
        }

        vo.setRecords(list);
        vo.setTotal(total);

        return Result.success(vo);
    }

    @Override
    public void updateStatus(String tradeNo, int payStatus, String gmtPayment, String alipayTradeNo) {

        //支付状态为已支付
        orderMapper.updatePayStatus(tradeNo,payStatus,gmtPayment,alipayTradeNo);
        //更新房源状态为已出租
        Order order = orderMapper.getByNo(tradeNo);
        House house = houseMapper.selectById(order.getHouseId());
        if (house.getRentType() == 1){
            //合租
            if (house.getRentRoomNumber() - 1 == 0){
                //最后一间,设置房源出租状态为已出租
                house.setRentStatus(1);
            }else {
                //减少一间出租的房间
                house.setRentRoomNumber(house.getRentRoomNumber() - 1);
            }
        }else {
            //整租或独栋，设置房源出租状态为已出租
            house.setRentStatus(1);
        }
        houseMapper.updateById(house);

        //新增或修改租赁信息
        if (order.getRentId() == null){
            //首次
            Rent newRent = new Rent();
            newRent.setHouseId(order.getHouseId());
            newRent.setUserId(order.getUserId());
            newRent.setTerm(order.getTerm());
            newRent.setDueTime(order.getDueTime());
            newRent.setRentTime(order.getPaymentTime());
            newRent.setRentStatus(1);
            rentMapper.insert(newRent);

            order.setRentId(newRent.getId());
            orderMapper.updateById(order);
        }else {
            Rent rent = rentMapper.selectById(order.getRentId());
            //续租
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime time = LocalDateTime.parse(rent.getDueTime(), formatter);
            rent.setTerm(rent.getTerm() + order.getTerm());
            rent.setDueTime(time.plusMonths(order.getTerm()).format(formatter));
            rentMapper.updateById(rent);
        }

        String content = "有用户支付了【"+ house.getName() +"】的租赁订单，快去看看吧";
        messageMapper.saveMessage("用户支付租赁订单",content,house.getAuthorId(),order.getUserId(),0, DateUtil.now());
    }

    @Override
    public Result detail(Integer id) {

        Order order = orderMapper.selectById(id);
        OrderVo vo = new OrderVo();
        BeanUtils.copyProperties(order,vo);

        System.out.println(id);
        RentAuditOrder auditOrder = auditOrderMapper.getByOrderId(id);
        RentAudit audit = auditMapper.selectById(auditOrder.getAuditId());
        BeanUtils.copyProperties(audit,vo);

        House house = houseMapper.selectById(order.getHouseId());
        vo.setHouseName(house.getName());

        return Result.success(vo);
    }

    @Override
    public Result cancelOrder(Integer id) throws Exception {

        Order order = orderMapper.selectById(id);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getHouseId,order.getHouseId());
        queryWrapper.eq(Order::getUserId,order.getUserId());
        List<Order> orders = orderMapper.selectList(queryWrapper);
        boolean flag = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (orders.size() > 1){
            for (Order order1 : orders){
                if (!order1.getId().equals(order.getId())){
                    if (order1.getPayStatus() == 1 && LocalDateTime.now().isBefore(LocalDateTime.parse(order.getDueTime(),formatter))){
                        flag = true;
                        break;
                    }
                }
            }
        }

        House house = houseMapper.selectById(order.getHouseId());
        if (!flag){
            //其他订单都已结束，上架房源
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

        order.setPayStatus(4);
        int i = orderMapper.updateById(order);
        if (i == 1){
            String content = "有用户取消了【"+ house.getName() +"】的租赁订单，快去看看吧";
            messageMapper.saveMessage("用户取消租赁订单",content,house.getAuthorId(),order.getUserId(),0, DateUtil.now());

            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @Override
    public Result getMoney() {

        User user = JwtTokenUtils.getCurrentUser();
        if (user == null) {
            throw new TokenException("登录失效");
        }

        double money = 0;
        //获取当前用户发布的房源id
        LambdaQueryWrapper<House> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(House::getAuthorId,user.getId());
        List<House> houses = houseMapper.selectList(queryWrapper);
        for (House house : houses) {
            //查询该房源所有的订单
            LambdaQueryWrapper<Order> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Order::getHouseId,house.getId());
            List<Order> orders = orderMapper.selectList(queryWrapper1);
            for (Order order : orders) {
                //获取订单的收入
                if (order.getReturnMoney() == null){
                    order.setReturnMoney((double) 0);
                }
                money += order.getTotalAmount() - order.getReturnMoney();
            }
        }
        return Result.success(money);
    }

    @Override
    public Result getCount() {

        User user = JwtTokenUtils.getCurrentUser();
        if (user == null){
            throw new TokenException("登录失效");
        }
        LambdaQueryWrapper<House> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(House::getAuthorId,user.getId());
        List<House> houses = houseMapper.selectList(queryWrapper);

        List<CountVo> list = new ArrayList<>();
        for (House house : houses){
            CountVo vo = new CountVo();
            LambdaQueryWrapper<Order> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Order::getHouseId,house.getId());
            List<Order> orders = orderMapper.selectList(queryWrapper1);
            if (!orders.isEmpty()){
                vo.setName(house.getName());
                vo.setValue(orders.size());
                list.add(vo);
            }
        }
        return Result.success(list);
    }

    private void searchRentOrderVo(List<Order> orders, List<OrderVo> list){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Order rentOrder : orders) {
            LocalDateTime createTime = LocalDateTime.parse(rentOrder.getCreateTime(), formatter);
            Order order = orderMapper.selectById(rentOrder.getId());
            House house = houseMapper.selectById(order.getHouseId());
            if (order.getPayStatus() == 0 && LocalDateTime.now().isAfter(createTime.plusHours(2))){
                //超过两小时未支付，订单取消
                order.setPayStatus(2);
                orderMapper.updateById(order);
                //重新上架房源
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

            OrderVo vo = new OrderVo();
            BeanUtils.copyProperties(order,vo);
            vo.setHouseName(house.getName());

            list.add(vo);
        }
    }
}
