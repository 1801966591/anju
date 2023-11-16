package com.anju.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.*;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.entity.vo.ReserveVo;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.HouseMapper;
import com.anju.springboot.mapper.ReserveAuditMapper;
import com.anju.springboot.mapper.ReserveMapper;
import com.anju.springboot.service.ReserveAuditService;
import com.anju.springboot.service.RoleService;
import com.anju.springboot.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
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
 *  服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-06
 */
@Service
@Slf4j
public class ReserveAuditServiceImpl extends ServiceImpl<ReserveAuditMapper, ReserveAudit> implements ReserveAuditService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private ReserveMapper reserveMapper;

    @Autowired
    ReserveAuditMapper auditMapper;

    @Autowired
    private RoleService roleService;

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
        List<ReserveVo> list = new ArrayList<>();
        ReserveVo vo = new ReserveVo();

        //判断房源名称是否为空
        if (StringUtils.isNotEmpty(param.getName())) {
            LambdaQueryWrapper<House> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(House::getName,param.getName());
            List<House> houses = houseMapper.selectList(queryWrapper);

            for (House house : houses){
                Integer houseId = house.getId();
                List<Reserve> audits = new ArrayList<>();
                if (role.equals(Constant.ROLE_LANDLORD)){
                    audits = reserveMapper.getByConditionForLandlord(houseId,param.getRealName(),user.getId(),pageNo,pageSize);
                    total = reserveMapper.getTotalByConditionForLandlord(houseId,param.getRealName(),user.getId());
                }else if (role.equals(Constant.ROLE_USER)){
                    audits = reserveMapper.getByConditionForUser(houseId,param.getRealName(),user.getId(),pageNo,pageSize);
                    total = reserveMapper.getTotalByConditionForUser(houseId,param.getRealName(),user.getId());
                }
                searchReserveVo(audits, list);
            }
        } else {
            List<Reserve> audits = new ArrayList<>();
            if (role.equals(Constant.ROLE_LANDLORD)){
                audits = reserveMapper.getByConditionForLandlord(null,param.getRealName(),user.getId(),pageNo,pageSize);
                total = reserveMapper.getTotalByConditionForLandlord(null,param.getRealName(),user.getId());
            }else if (role.equals(Constant.ROLE_USER)){
                audits = reserveMapper.getByConditionForUser(null,param.getRealName(),user.getId(),pageNo,pageSize);
                total = reserveMapper.getTotalByConditionForUser(null,param.getRealName(),user.getId());
            }
            searchReserveVo(audits,list);
        }

        vo.setRecords(list);
        vo.setTotal(total);

        return Result.success(vo);
    }

    private void searchReserveVo(List<Reserve> reserves, List<ReserveVo> list){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Reserve reserve : reserves) {
            LocalDateTime createTime = LocalDateTime.parse(reserve.getCreateTime(), formatter);
            LocalDateTime Time = LocalDateTime.parse(reserve.getTime(), formatter);
            ReserveAudit audit = auditMapper.getByReserveId(reserve.getId());

            if (audit.getStatus() == 0 && LocalDateTime.now().isAfter(createTime.plusHours(24))){
                //房东超时未确认
                reserve.setReserveStatus(3);
                reserveMapper.updateById(reserve);
            }

            if (LocalDateTime.now().isAfter(Time) && reserve.getReserveStatus() == 0){
                //设置已过预约时间
                reserve.setReserveStatus(1);
                reserveMapper.updateById(reserve);
            }

            if (LocalDateTime.now().isAfter(Time.plusHours(1)) && audit.getStatus() == 1 && reserve.getReserveStatus() == 1){
                //如果预约看房时间已经过了1小时，且房东审核状态是同意，则代表已看房，将预约状态设置为已完成
                reserve.setReserveStatus(4);
                reserveMapper.updateById(reserve);
            }

            ReserveVo reserveVo = new ReserveVo();
            BeanUtils.copyProperties(reserve,reserveVo);
            House byId = houseMapper.selectById(reserve.getHouseId());
            reserveVo.setHouseName(byId.getName());

            reserveVo.setAuditId(audit.getId());
            reserveVo.setReserveId(audit.getReserveId());
            reserveVo.setStatus(audit.getStatus());
            reserveVo.setContent(audit.getContent());

            list.add(reserveVo);
        }
    }

}
