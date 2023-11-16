package com.anju.springboot.controller;

import cn.hutool.core.date.DateUtil;
import com.anju.springboot.exception.CustomException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anju.springboot.common.AutoLog;
import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.House;
import com.anju.springboot.entity.User;
import com.anju.springboot.entity.param.HouseParam;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.entity.vo.HouseVo;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.service.HouseService;
import com.anju.springboot.service.RoleService;
import com.anju.springboot.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 房屋信息表 前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-24
 */
@RestController
@RequestMapping("/house")
@Slf4j
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private RoleService roleService;

    @PostMapping("/upload")
    @AutoLog("上传房源")
    public Result upload(@RequestBody HouseParam param) throws Exception{
        return houseService.upload(param);
    }

    @GetMapping("/search")
    public Result search(Param param){

        User user = JwtTokenUtils.getCurrentUser();
        if (user == null){
            throw new TokenException("登录失效");
        }
        String role = roleService.getRoleByUserId(user.getId());

        Page<House> page = new Page<>(param.getCurrentPage(),param.getPageSize());
        LambdaQueryWrapper<House> queryWrapper = new LambdaQueryWrapper<>();
        if (role.equals(Constant.ROLE_LANDLORD)){
            //登录用户角色是房东，查询该用户对应的房源
            queryWrapper.eq(House::getAuthorId,user.getId());
        }
        queryWrapper.like(StringUtils.isNotEmpty(param.getName()),House::getName,param.getName());
        Page<House> housePage = houseService.page(page, queryWrapper);

        HouseVo vo = houseService.search(housePage);

        return Result.success(vo);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable("id") Integer id){
        return houseService.detail(id);
    }

    @PutMapping("/update")
    @AutoLog("修改房源信息")
    public Result update(@RequestBody HouseParam param) throws Exception {
        return houseService.updateHouse(param);
    }

    @DeleteMapping("/deleteById/{id}")
    @AutoLog("删除房源信息")
    public Result deleteById(@PathVariable("id") Integer id) throws Exception {
        House house = houseService.getById(id);
        if (house.getListingStatus() == 1 || house.getRentStatus() == 1){
            throw new CustomException("不能删除已上架或已出租的房源");
        }else {
            int i = houseService.deleteById(id);
            if (i > 0){
                return Result.success();
            }else {
                throw new Exception();
            }
        }

    }

    @DeleteMapping("/deleteByIds/{ids}")
    @AutoLog("批量删除房源信息")
    public Result deleteByIds(@PathVariable("ids") List<Integer> ids) throws Exception {
        return houseService.deleteByIds(ids);
    }

    @PostMapping("/sale/{id}")
    @AutoLog("上/下架房源")
    public Result list(@PathVariable("id") Integer id) throws Exception {
        House house = houseService.getById(id);
        Integer listingStatus = house.getListingStatus();
        int status = 0;
        if (listingStatus == 0){
            //当前状态是未上架，上架房源
            house.setListTime(DateUtil.now());
            status = 1;
        }
        house.setListingStatus(status);
        boolean flag = houseService.updateById(house);

        if (flag){
            return Result.success(status);
        } else {
            throw new Exception();
        }
    }

    @PostMapping("/maintenance/{id}")
    public Result maintenance(@PathVariable("id") Integer id) throws Exception {

        House house = new House();
        house.setId(id);
        house.setMaintenanceTime(DateUtil.now());
        boolean flag = houseService.updateById(house);

        if (flag){
            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @GetMapping("/userSearch")
    public Result userSearch(HouseParam param){

        List<House> all = houseService.list();

        for (House house : all){
            //查询租赁情况，过期自动上架
            houseService.searchRent(house);
        }

        Page<House> pageInfo = new Page<>(param.getCurrentPage(),param.getPageSize());
        LambdaQueryWrapper<House> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(param.getName()), House::getName,param.getName())
                .eq(House::getListingStatus,1)
                .eq(House::getRentStatus,0)
                //根据类型查询
                .eq(param.getRentType() != null,House::getRentType,param.getRentType())
                //根据租金区间查询
                .ge(param.getMinRent() != null, House::getRent, param.getMinRent())
                .le(param.getMaxRent() != null, House::getRent, param.getMaxRent())
                //根据面积区间查询
                .ge(param.getMinArea() != null, House::getArea, param.getMinArea())
                .le(param.getMaxArea() != null, House::getArea, param.getMaxArea());
        if (StringUtils.isNotEmpty(param.getOrderBy())){
            switch (param.getOrderBy()) {
                //根据面积升序或降序查询
                case "area" -> {
                    if (param.getOrder().equals("ASC")) {
                        queryWrapper.orderByAsc(House::getArea);
                    } else {
                        queryWrapper.orderByDesc(House::getArea);
                    }
                }
                //根据租金升序或降序查询
                case "rent" -> {
                    if (param.getOrder().equals("ASC")) {
                        queryWrapper.orderByAsc(House::getRent);
                    } else {
                        queryWrapper.orderByDesc(House::getRent);
                    }
                }
                //根据上架时间查询
                case "listTime" -> queryWrapper.orderByDesc(House::getListTime);
            }
        }
        Page<House> page = houseService.page(pageInfo, queryWrapper);
        List<House> houses = page.getRecords();

        HouseVo vo = new HouseVo();

        List<HouseVo> list = houseService.userSearch(param,houses);

        vo.setRecords(list);
        vo.setTotal((int) page.getTotal());

        return Result.success(vo);
    }
}
