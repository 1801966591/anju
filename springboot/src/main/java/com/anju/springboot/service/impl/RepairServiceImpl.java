package com.anju.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.*;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.entity.vo.RepairVo;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.*;
import com.anju.springboot.service.RepairService;
import com.anju.springboot.service.RoleService;
import com.anju.springboot.utils.JwtTokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 报修信息表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-18
 */
@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private RepairMapper repairMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RentAuditOrderMapper rentAuditOrderMapper;

    @Autowired
    private RentAuditMapper auditMapper;

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

        List<RepairVo> list = new ArrayList<>();
        RepairVo vo = new RepairVo();

        //判断房源名称是否为空
        if (StringUtils.isNotEmpty(param.getName())) {
            LambdaQueryWrapper<House> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(House::getName,param.getName());
            List<House> houses = houseMapper.selectList(queryWrapper);

            for (House house : houses){
                Integer houseId = house.getId();
                List<Repair> repairs = new ArrayList<>();
                if (role.equals(Constant.ROLE_LANDLORD)){
                    repairs = repairMapper.getByConditionForLandlord(houseId,user.getId(),pageNo,pageSize);
                    total = repairMapper.getTotalByConditionForLandlord(houseId,user.getId());
                }else if (role.equals(Constant.ROLE_USER)){
                    repairs = repairMapper.getByConditionForUser(houseId,user.getId(),pageNo,pageSize);
                    total = repairMapper.getTotalByConditionForUser(houseId,user.getId());
                }
                searchRentOrderVo(repairs, list);
            }
        } else {
            List<Repair> repairs = new ArrayList<>();
            if (role.equals(Constant.ROLE_LANDLORD)){
                repairs = repairMapper.getByConditionForLandlord(null,user.getId(),pageNo,pageSize);
                total = repairMapper.getTotalByConditionForLandlord(null,user.getId());
            }else if (role.equals(Constant.ROLE_USER)){
                repairs = repairMapper.getByConditionForUser(null,user.getId(),pageNo,pageSize);
                total = repairMapper.getTotalByConditionForUser(null,user.getId());
            }
            searchRentOrderVo(repairs,list);
        }

        vo.setRecords(list);
        vo.setTotal(total);

        return Result.success(vo);

    }

    @Override
    public Result detail(Integer id) {

        RepairVo vo = new RepairVo();

        //根据id查出报修信息
        Repair repair = repairMapper.selectById(id);
        BeanUtils.copyProperties(repair,vo);

        //查出房源信息
        House house = houseMapper.selectById(repair.getHouseId());
        vo.setHouseName(house.getName());

        //查出对应的最新的订单信息
        Order order = orderMapper.getLatestByRentId(repair.getRentId());

        //查出对应的申请信息
        RentAuditOrder byOrderId = rentAuditOrderMapper.getByOrderId(order.getId());
        RentAudit audit = auditMapper.selectById(byOrderId.getAuditId());

        //查出租赁人相关信息
        vo.setName(audit.getName());
        vo.setMobile(audit.getMobile());

        return Result.success(vo);
    }

    private void searchRentOrderVo(List<Repair> repairs, List<RepairVo> list){

        for (Repair repair : repairs) {
            Integer houseId = repair.getHouseId();
            House house = houseMapper.selectById(houseId);

            RepairVo vo = new RepairVo();
            BeanUtils.copyProperties(repair,vo);
            vo.setHouseName(house.getName());

            list.add(vo);
        }
    }
}
