package com.anju.springboot.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.config.AliPayConfig;
import com.anju.springboot.entity.House;
import com.anju.springboot.entity.Order;
import com.anju.springboot.entity.Rent;
import com.anju.springboot.entity.User;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.entity.vo.CountVo;
import com.anju.springboot.entity.vo.RentVo;
import com.anju.springboot.exception.CustomException;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.HouseMapper;
import com.anju.springboot.mapper.MessageMapper;
import com.anju.springboot.mapper.OrderMapper;
import com.anju.springboot.mapper.RentMapper;
import com.anju.springboot.service.RentService;
import com.anju.springboot.service.RoleService;
import com.anju.springboot.utils.JwtTokenUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 租赁信息表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-18
 */
@Service
@Slf4j
public class RentServiceImpl extends ServiceImpl<RentMapper, Rent> implements RentService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private RentMapper rentMapper;

    @Autowired
    private RoleService roleService;

    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    //签名方式
    private static final String SIGN_TYPE = "RSA2";

    @Resource
    private AliPayConfig aliPayConfig;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private MessageMapper messageMapper;

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

        List<RentVo> list = new ArrayList<>();
        RentVo vo = new RentVo();

        //判断房源名称是否为空
        if (StringUtils.isNotEmpty(param.getName())) {
            LambdaQueryWrapper<House> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(House::getName,param.getName());
            List<House> houses = houseMapper.selectList(queryWrapper);

            for (House house : houses){
                Integer houseId = house.getId();
                List<Rent> rents = new ArrayList<>();
                if (role.equals(Constant.ROLE_LANDLORD)){
                    rents = rentMapper.getByConditionForLandlord(houseId,user.getId(),pageNo,pageSize);
                    total = rentMapper.getTotalByConditionForLandlord(houseId,user.getId());
                }else if (role.equals(Constant.ROLE_USER)){
                    rents = rentMapper.getByConditionForUser(houseId,user.getId(),pageNo,pageSize);
                    total = rentMapper.getTotalByConditionForUser(houseId,user.getId());
                }
                searchRentOrderVo(rents, list);
            }
        } else {
            List<Rent> rents = new ArrayList<>();
            if (role.equals(Constant.ROLE_LANDLORD)){
                rents = rentMapper.getByConditionForLandlord(null,user.getId(),pageNo,pageSize);
                total = rentMapper.getTotalByConditionForLandlord(null,user.getId());
            }else if (role.equals(Constant.ROLE_USER)){
                rents = rentMapper.getByConditionForUser(null,user.getId(),pageNo,pageSize);
                total = rentMapper.getTotalByConditionForUser(null,user.getId());
            }
            searchRentOrderVo(rents,list);
        }

        vo.setRecords(list);
        vo.setTotal(total);

        return Result.success(vo);
    }

    @Override
    public Result rentOut(Integer id) throws AlipayApiException {

        //计算租金
        Rent rent = rentMapper.selectById(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime rentTime = LocalDateTime.parse(rent.getRentTime(), formatter);
        Duration duration = Duration.between(rentTime,LocalDateTime.now());
        long day = duration.toDays();

        House house = houseMapper.selectById(rent.getHouseId());
        double dayRent = house.getRent() / 31;

        double total = dayRent * day;

        //查找订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getRentId,id);
        List<Order> orders = orderMapper.selectList(queryWrapper);
        double totalAmount;

        for (Order order : orders){
            //租金计算
            totalAmount = order.getTotalAmount() - total;
            if (totalAmount > 0){
                order.setTotalAmount(Double.parseDouble(String.format("%.2f", totalAmount)));
                break;
            }else {
                order.setTotalAmount((double) 0);
                total = -totalAmount;
            }
        }

        boolean flag = true;
        int i = 1;
        for (Order order : orders){
            //退款
            if (order.getPayStatus() == 1){
                // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
                AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL,
                        aliPayConfig.getAppId(), aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET,
                        aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);
                // 2. 创建 Request，设置参数
                AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
                JSONObject bizContent = new JSONObject();
                bizContent.set("trade_no", order.getAlipayNo());  // 支付宝回调的订单流水号
                bizContent.set("refund_amount", order.getTotalAmount());  // 订单的总金额
                bizContent.set("out_request_no",order.getAlipayNo());

                request.setBizContent(bizContent.toString());

                // 3. 执行请求
                AlipayTradeRefundResponse response = alipayClient.execute(request);
                if (response.isSuccess()) {  // 退款成功，isSuccess 为true
                    log.info("调用成功，第{}个订单退款成功",i);
                    Order byId = orderMapper.selectById(order.getId());
                    byId.setPayStatus(3);
                    byId.setReturnMoney(order.getTotalAmount());
                    byId.setReturnTime(DateUtil.now());
                    orderMapper.updateById(byId);
                } else {   // 退款失败，isSuccess 为false
                    flag = false;
                    log.error("第" + i + "个订单退款失败，原因：" + response.getBody());
                }
                i++;
            }
        }

        if (flag){
            // 4. 更新数据库状态
            rent.setRentStatus(3);
            rent.setReturnTime(DateUtil.now());
            rentMapper.updateById(rent);

            //合租
            if (house.getRentType() == 1){
                //合租
                if (house.getListingStatus() == 0){
                    house.setListingStatus(1);
                    house.setRentStatus(0);
                    house.setListTime(rent.getReturnTime());
                    house.setRentRoomNumber(1);
                }else {
                    house.setRentRoomNumber(house.getRentRoomNumber() + 1);
                }
            }else {
                house.setListingStatus(1);
                house.setRentStatus(0);
                house.setListTime(rent.getReturnTime());
            }
            houseMapper.updateById(house);

            String content = "租赁了【"+ house.getName() +"】的用户退租了，快去看看吧";
            messageMapper.saveMessage("用户退租",content,house.getAuthorId(),rent.getUserId(),0, DateUtil.now());

            return Result.success();
        }else {
            throw new CustomException("退租失败");
        }

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
            LambdaQueryWrapper<Rent> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Rent::getHouseId,house.getId());
            queryWrapper1.eq(Rent::getRentStatus,1);
            List<Rent> rents = rentMapper.selectList(queryWrapper1);
            if (!rents.isEmpty()){
                vo.setName(house.getName());
                vo.setValue(rents.size());
                list.add(vo);
            }
        }
        return Result.success(list);

    }

    private void searchRentOrderVo(List<Rent> rents, List<RentVo> list){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Rent rent : rents) {
            Integer houseId = rent.getHouseId();
            Rent rentByHouseId = rentMapper.getLatestByHouseId(houseId);
            if (rentByHouseId != null) {
                LocalDateTime dueTime = LocalDateTime.parse(rentByHouseId.getDueTime(), formatter);
                House house = houseMapper.selectById(houseId);

                if (LocalDateTime.now().isAfter(dueTime) && house.getRentStatus() == 1){
                    //已过到期时间，上架房源
                    if (house.getRentType() == 1){
                        //合租
                        if (house.getListingStatus() == 0){
                            house.setListingStatus(1);
                            house.setRentStatus(0);
                            house.setListTime(rentByHouseId.getDueTime());
                            house.setRentRoomNumber(1);
                        }else {
                            house.setRentRoomNumber(house.getRentRoomNumber() + 1);
                        }
                    }else {
                        house.setListingStatus(1);
                        house.setRentStatus(0);
                        house.setListTime(rentByHouseId.getDueTime());
                    }
                    houseMapper.updateById(house);
                }

                if (rent.getRentStatus() == 1 && LocalDateTime.now().isAfter(LocalDateTime.parse(rent.getDueTime(),formatter))){
                    //当前租赁已到期，且状态为正在租赁，设置状态为已完成
                    rent.setRentStatus(2);
                    rentMapper.updateById(rent);
                }

                RentVo vo = new RentVo();
                BeanUtils.copyProperties(rent,vo);
                vo.setHouseName(house.getName());
                vo.setImg(house.getImg1());

                list.add(vo);
            }
        }
    }
}
