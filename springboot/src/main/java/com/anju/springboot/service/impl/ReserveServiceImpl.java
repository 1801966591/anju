package com.anju.springboot.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.House;
import com.anju.springboot.entity.Reserve;
import com.anju.springboot.entity.ReserveAudit;
import com.anju.springboot.entity.User;
import com.anju.springboot.exception.CustomException;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.HouseMapper;
import com.anju.springboot.mapper.MessageMapper;
import com.anju.springboot.mapper.ReserveAuditMapper;
import com.anju.springboot.mapper.ReserveMapper;
import com.anju.springboot.service.ReserveService;
import com.anju.springboot.utils.JwtTokenUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-06
 */
@Service
public class ReserveServiceImpl extends ServiceImpl<ReserveMapper, Reserve> implements ReserveService {

    @Autowired
    private ReserveMapper reserveMapper;

    @Autowired
    private ReserveAuditMapper auditMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Override
    public Result reserve(Reserve reserve) throws Exception{
        //判断预约时间是否冲突
        otherTime(reserve);
        User user = JwtTokenUtils.getCurrentUser();
        //判断登录是否失效
        if (ObjectUtil.isNull(user)) {
            throw new TokenException("登录失效");
        }
        reserve.setUserId(user.getId());
        reserve.setReserveStatus(0);
        reserve.setCreateTime(DateUtil.now());
        //保存预约信息
        int i = reserveMapper.insert(reserve);
        Integer reserveId = reserve.getId();
        //生成审核信息
        ReserveAudit audit = new ReserveAudit();
        audit.setReserveId(reserveId);
        audit.setStatus(0);
        i += auditMapper.insert(audit);
        if (i == 2){
            House house = houseMapper.selectById(reserve.getHouseId());
            String content = "有用户预约了【"+ house.getName() +"】的看房时间，快去确认吧";
            messageMapper.saveMessage("用户预约",content,house.getAuthorId(),user.getId(),0,DateUtil.now());
            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @Override
    public Result updateReserve(Reserve reserve) throws Exception {

        if (reserve.getReserveStatus() != 0) {
            throw new CustomException("当前预约状态不能修改预约信息");
        }

        User user = JwtTokenUtils.getCurrentUser();
        if (ObjectUtil.isNull(user)) {
            throw new TokenException("登录失效");
        }

        otherTime(reserve);

        //将审核状态设置为待确认
        ReserveAudit audit = auditMapper.getByReserveId(reserve.getId());
        audit.setStatus(0);
        auditMapper.updateById(audit);

        reserve.setReserveStatus(0);
        reserve.setUpdateTime(DateUtil.now());
        int i = reserveMapper.updateById(reserve);

        if (i == 1){
            House house = houseMapper.selectById(reserve.getHouseId());
            String content = "有用户修改了【"+ house.getName() +"】的看房时间，快去确认吧";

            messageMapper.saveMessage("用户修改预约信息",content,house.getAuthorId(),user.getId(),0,DateUtil.now());
            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @Override
    public Result cancelReserve(Integer id) throws Exception {

        Reserve reserve = reserveMapper.selectById(id);

        if (reserve.getReserveStatus() != 0) {
            throw new CustomException("当前预约状态不能取消预约");
        }

        User user = JwtTokenUtils.getCurrentUser();
        if (ObjectUtil.isNull(user)) {
            throw new TokenException("登录失效");
        }

        //将审核状态设置为取消预约
        ReserveAudit audit = auditMapper.getByReserveId(id);
        audit.setStatus(4);
        auditMapper.updateById(audit);

        //取消预约
        reserve.setReserveStatus(2);
        reserve.setUpdateTime(DateUtil.now());
        int i = reserveMapper.updateById(reserve);

        if (i == 1){
            House house = houseMapper.selectById(reserve.getHouseId());
            String content = "有用户取消了【"+ house.getName() +"】的预约看房";
            messageMapper.saveMessage("用户修改预约信息",content,house.getAuthorId(),user.getId(),0,DateUtil.now());
            return Result.success();
        }else {
            throw new Exception();
        }

    }

    private void otherTime(Reserve reserve){
        // 查询预约时间是否冲突
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.parse(reserve.getTime(), formatter);
        //根据房源id查询预约列表
        List<Reserve> list = reserveMapper.getByHouseId(reserve.getHouseId());
        if (!list.isEmpty()){
            for (Reserve r : list){
                LocalDateTime reserveTime = LocalDateTime.parse(r.getTime(), formatter);
                if (time.isEqual(reserveTime)) {
                    //与其他看房时间相同
                    throw new CustomException("当前预约的看房时间与其他用户的预约时间相同，请重新预约");
                }else if(reserveTime.isAfter(time.minusHours(1)) && reserveTime.isBefore(time)){
                    //与当前时间之前的看房时间冲突
                    throw new CustomException("当前预约的看房时间之前1小时内与其他用户的预约时间冲突，请重新预约");
                } else if (reserveTime.isAfter(time) && reserveTime.isBefore(time.plusHours(1))){
                    //与当前时间之后的看房时间冲突
                    throw new CustomException("当前预约的看房时间之后1小时内与其他用户的预约时间冲突，请重新预约");
                }
            }
        }

        //查询是否是特定时间看房，并判断是否冲突
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        House house = houseMapper.selectById(reserve.getHouseId());
        if (house.getViewStatus() == 1){
            //特定时间看房
            LocalTime startTime = LocalTime.parse(house.getStartTime(), timeFormatter);
            LocalTime endTime = LocalTime.parse(house.getEndTime(), timeFormatter);
            String hour = time.getHour() < 10 ? "0"+time.getHour() : String.valueOf(time.getHour());
            String minute = time.getMinute() < 10 ? "0"+time.getMinute() : String.valueOf(time.getMinute());
            String second = time.getSecond() < 10 ? "0"+time.getSecond() : String.valueOf(time.getSecond());
            LocalTime reserveTime = LocalTime.parse(hour + ":" + minute + ":" + second,timeFormatter);
            if (!reserveTime.isAfter(startTime) && !reserveTime.isBefore(endTime)){
                throw new CustomException("当前预约的看房时间不符合房源的特定看房时间，请重新预约");
            }else {
                if (!reserveTime.isBefore(endTime.minusHours(1))){
                    throw new CustomException("当前预约的看房时间应早于房源的特定看房结束时间1小时，请重新预约");
                }
            }
        }
    }
}
