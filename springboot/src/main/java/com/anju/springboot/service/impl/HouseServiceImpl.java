package com.anju.springboot.service.impl;

import cn.hutool.core.date.DateUtil;
import com.anju.springboot.exception.CustomException;
import com.anju.springboot.service.UserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.*;
import com.anju.springboot.entity.param.HouseParam;
import com.anju.springboot.entity.vo.HouseVo;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.*;
import com.anju.springboot.service.HouseService;
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
import java.util.Objects;

/**
 * <p>
 * 房屋信息表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-26
 */
@Service
@Slf4j
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private HouseTagMapper houseTagMapper;

    @Autowired
    private HouseAddressMapper houseAddressMapper;

    @Autowired
    private HouseAuditMapper houseAuditMapper;

    @Autowired
    private ProvinceMapper provinceMapper;

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private DicValueMapper dicValueMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HouseConfigMapper houseConfigMapper;

    @Autowired
    private ReserveMapper reserveMapper;

    @Autowired
    private RentMapper rentMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public Result upload(HouseParam param) throws Exception {

        //查询当前登录用户Token是否有效
        User user = JwtTokenUtils.getCurrentUser();
        if (user == null){
            throw new TokenException("登录失效");
        }
        House house = new House();
        BeanUtils.copyProperties(param,house);
        if(StringUtils.isNotEmpty(param.getViewTime())){
            //设置为看房时间为随时看房
            house.setViewStatus(0);
        }else {
            //设置为看房时间特定时间看房
            house.setViewStatus(1);
        }

        List<String> fileList = param.getFileList();
        house.setImg1(fileList.get(0));
        house.setImg2(fileList.get(1));
        house.setImg3(fileList.get(2));
        house.setImg4(fileList.get(3));
        house.setAuthorId(user.getId());
        house.setListingStatus(0);
        house.setRentStatus(0);
        house.setCreateTime(DateUtil.now());

        //保存房屋信息
        int i = houseMapper.insert(house);

        //获取刚刚保存的房屋id
        Integer houseId = house.getId();

        //处理房屋地址信息
        HouseAddress address = new HouseAddress();
        BeanUtils.copyProperties(param,address);
        address.setHouseId(houseId);
        i += houseAddressMapper.insert(address);

        //处理审核状态
        HouseAudit audit = new HouseAudit();
        audit.setHouseId(houseId);
        audit.setStatus(0);
        i += houseAuditMapper.insert(audit);

        //处理房屋配置信息
        for (Integer config : param.getConfig()){
            HouseConfig houseConfig = new HouseConfig();
            houseConfig.setHouseId(houseId);
            houseConfig.setConfigIconId(config);
            i += houseConfigMapper.insert(houseConfig);
        }

        //处理房屋标签
        for (Integer tag : param.getTag()){
            HouseTag houseTag = new HouseTag();
            houseTag.setHouseId(houseId);
            houseTag.setTagId(tag);
            i += houseTagMapper.insert(houseTag);
        }

        if (i > 0){

            Integer adminId = userRoleService.getAdminId();

            String content = "有房东上传了新的房源信息，快去审核吧";
            messageMapper.saveMessage("待审核房源提醒",content,adminId,user.getId(),0,DateUtil.now());

            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @Override
    public HouseVo search(Page<House> housePage) {

        HouseVo vo = new HouseVo();
        List<HouseVo> list = new ArrayList<>();

        List<House> records = housePage.getRecords();


        for (House house : records) {

            searchRent(house);
            House byId = houseMapper.selectById(house.getId());

            HouseVo houseVo = new HouseVo();
            BeanUtils.copyProperties(byId,houseVo);

            //审核信息
            HouseAudit one = houseAuditMapper.getByHouseId(house.getId());
            houseVo.setAuditStatus(one.getStatus());
            houseVo.setContent(one.getContent());

            //发布人信息
            User user = userMapper.selectById(house.getAuthorId());
            houseVo.setAuthor(user.getRealName());

            list.add(houseVo);
        }
        vo.setRecords(list);
        vo.setTotal((int) housePage.getTotal());

        return vo;
    }

    @Override
    public Result detail(Integer id) {

        House house = houseMapper.selectById(id);
        HouseVo vo = new HouseVo();
        BeanUtils.copyProperties(house,vo);
        vo.setHouseArea(house.getArea());
        //处理发布人信息
        User user = userMapper.selectById(house.getAuthorId());
        vo.setAuthor(user.getRealName());
        vo.setAvatar(user.getAvatar());
        vo.setNickname(user.getNickname());
        //处理审核信息
        HouseAudit audit = houseAuditMapper.getByHouseId(id);
        vo.setAuditStatus(audit.getStatus());
        vo.setContent(audit.getContent());
        //处理地址信息
        HouseAddress houseAddress = houseAddressMapper.getByHouseId(id);
        Integer provinceId = houseAddress.getProvinceId();
        Integer cityId = houseAddress.getCityId();
        Integer areaId = houseAddress.getAreaId();
        String address = houseAddress.getAddress();
        Province province = provinceMapper.getByProvinceId(provinceId);
        City city = cityMapper.getByCityId(cityId);
        Area area = areaMapper.getByAreaId(areaId);
        vo.setProvinceId(provinceId);
        vo.setProvince(province.getProvince());
        vo.setCityId(cityId);
        vo.setCity(city.getCity());
        vo.setAreaId(areaId);
        vo.setArea(area.getArea());
        vo.setAddress(address);
        //处理房屋配置信息
        List<DicValue> configs = new ArrayList<>();
        List<Integer> configIds = new ArrayList<>();
        LambdaQueryWrapper<HouseConfig> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(HouseConfig::getHouseId,id);
        List<HouseConfig> houseConfigs = houseConfigMapper.selectList(queryWrapper1);
        for (HouseConfig config : houseConfigs) {
            Integer configIconId = config.getConfigIconId();
            configIds.add(configIconId);
            DicValue dicValue = dicValueMapper.selectById(configIconId);
            configs.add(dicValue);
        }
        vo.setConfigs(configs);
        vo.setConfigIds(configIds);
        //处理标签信息
        List<String> tags = new ArrayList<>();
        List<Integer> tagIds = new ArrayList<>();
        LambdaQueryWrapper<HouseTag> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(HouseTag::getHouseId,id);
        List<HouseTag> houseTags = houseTagMapper.selectList(queryWrapper2);
        for (HouseTag tag : houseTags){
            Integer tagId = tag.getTagId();
            tagIds.add(tagId);
            DicValue dicValue = dicValueMapper.selectById(tagId);
            tags.add(dicValue.getName());
        }
        vo.setTags(tags);
        vo.setTagIds(tagIds);
        //处理图片信息
        List<String> imgs = new ArrayList<>();
        imgs.add(house.getImg1());
        imgs.add(house.getImg2());
        imgs.add(house.getImg3());
        imgs.add(house.getImg4());
        vo.setImgs(imgs);
        //处理预约信息
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new TokenException("登录失效");
        }
        Reserve reserve = reserveMapper.getByHouseIdAndUserId(house.getId(),currentUser.getId());
        if (reserve != null){
            // 比较是否预约时间是否晚于当前时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime time = LocalDateTime.parse(reserve.getTime(), formatter);
            if (LocalDateTime.now().isBefore(time) && reserve.getReserveStatus() == 0){
                //如果晚于当前时间，且预约状态不是取消预约，返回已预约
                vo.setReserveStatus(0);
            }
        }
        return Result.success(vo);
    }

    @Override
    public Result updateHouse(HouseParam param) throws Exception {

        House house = new House();
        BeanUtils.copyProperties(param,house);
        Integer houseId = house.getId();
        List<String> fileList = param.getFileList();
        house.setImg1(fileList.get(0));
        house.setImg2(fileList.get(1));
        house.setImg3(fileList.get(2));
        house.setImg4(fileList.get(3));
        if(StringUtils.isNotEmpty(param.getViewTime())){
            //设置为看房时间为随时看房
            house.setViewStatus(0);
        }else {
            //设置为看房时间特定时间看房
            house.setViewStatus(1);
        }

        //处理地址
        HouseAddress houseAddress = houseAddressMapper.getByHouseId(houseId);
        houseAddress.setProvinceId(param.getProvinceId());
        houseAddress.setCityId(param.getCityId());
        houseAddress.setAreaId(param.getAreaId());
        houseAddress.setAddress(param.getAddress());
        LambdaQueryWrapper<HouseAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HouseAddress::getHouseId,houseAddress.getHouseId());
        int i = houseAddressMapper.update(houseAddress, queryWrapper);

        //处理房屋配置信息
        //删除原来的配置
        houseConfigMapper.deleteByHouseId(houseId);
        //重新添加配置
        for (Integer config : param.getConfig()){
            HouseConfig houseConfig = new HouseConfig();
            houseConfig.setHouseId(houseId);
            houseConfig.setConfigIconId(config);
            i += houseConfigMapper.insert(houseConfig);
        }

        //处理标签
        //删除原来的标签
        houseTagMapper.deleteByHouseId(houseId);
        //重新添加标签
        for (Integer tag : param.getTag()){
            HouseTag houseTag = new HouseTag();
            houseTag.setHouseId(houseId);
            houseTag.setTagId(tag);
            i += houseTagMapper.insert(houseTag);
        }

        //处理审核状态，将审核状态设置为未审核
        houseAuditMapper.updateStatusByHouseId(houseId);

        //处理上架状态，将上架状态设置为未上架
        house.setListingStatus(0);

        //更新房源信息
        i += houseMapper.updateById(house);

        if (i>0){
            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @Override
    public int deleteById(Integer houseId) {

        int i = 0;

        //处理地址
        HouseAddress houseAddress = houseAddressMapper.getByHouseId(houseId);
        i += houseAddressMapper.deleteById(houseAddress.getId());
        //处理房屋配置信息
        houseConfigMapper.deleteByHouseId(houseId);
        //处理标签
        houseTagMapper.deleteByHouseId(houseId);
        //处理审核状态
        HouseAudit audit = houseAuditMapper.getByHouseId(houseId);
        i += houseAuditMapper.deleteById(audit.getId());
        //删除房源信息
        i += houseMapper.deleteById(houseId);

        return i;
    }

    @Override
    public Result deleteByIds(List<Integer> ids) throws Exception {

        int i = 0;

        for (Integer houseId : ids){
            House house = houseMapper.selectById(houseId);
            if (house.getListingStatus() == 1 || house.getRentStatus() == 1){
                throw new CustomException("不能删除已上架或已出租的房源");
            }else {
                i += deleteById(houseId);
            }
        }

        if (i > 0){
            return Result.success();
        } else {
            throw new Exception();
        }
    }

    @Override
    public List<HouseVo> userSearch(HouseParam param,List<House> houses) {

        List<HouseVo> list = new ArrayList<>();

        for (House house : houses) {

            HouseAudit audit = houseAuditMapper.getByHouseId(house.getId());
            if (audit.getStatus() == 1 && house.getListingStatus() == 1 && house.getRentStatus() == 0){
                HouseVo vo = new HouseVo();
                Integer houseId = house.getId();
                HouseAddress houseAddress = houseAddressMapper.getByHouseId(houseId);
                List<HouseTag> tags = houseTagMapper.getByHouseId(houseId);
                List<String> tagNames = new ArrayList<>();
                for (HouseTag tag : tags){
                    Integer tagId = tag.getTagId();
                    DicValue dicValue = dicValueMapper.selectById(tagId);
                    tagNames.add(dicValue.getName());
                }
                if (param.getProvinceId() != null && param.getCityId() != null && param.getAreaId() != null){
                    if (Objects.equals(houseAddress.getProvinceId(), param.getProvinceId()) && Objects.equals(houseAddress.getCityId(), param.getCityId()) && Objects.equals(houseAddress.getAreaId(), param.getAreaId())) {
                        Province province = provinceMapper.getByProvinceId(houseAddress.getProvinceId());
                        City city = cityMapper.getByCityId(houseAddress.getCityId());
                        Area area = areaMapper.getByAreaId(houseAddress.getAreaId());
                        BeanUtils.copyProperties(house,vo);
                        vo.setProvince(province.getProvince());
                        vo.setCity(city.getCity());
                        vo.setArea(area.getArea());
                        vo.setHouseArea(house.getArea());
                        vo.setTags(tagNames);
                        list.add(vo);
                    }
                }else {
                    BeanUtils.copyProperties(house,vo);
                    Province province = provinceMapper.getByProvinceId(houseAddress.getProvinceId());
                    City city = cityMapper.getByCityId(houseAddress.getCityId());
                    Area area = areaMapper.getByAreaId(houseAddress.getAreaId());
                    BeanUtils.copyProperties(house,vo);
                    vo.setProvince(province.getProvince());
                    vo.setCity(city.getCity());
                    vo.setArea(area.getArea());
                    vo.setHouseArea(house.getArea());
                    vo.setTags(tagNames);
                    list.add(vo);
                }
            }
        }

        return list;
    }

    /**
     * 查看房源是否正在出租，未出租的话，上架房源
     * @param house
     */
    @Override
    public void searchRent(House house){
        Integer houseId = house.getId();
        Rent latestByHouseId = rentMapper.getLatestByHouseId(houseId);

        if (latestByHouseId != null){
            //获取到期时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dueTime = LocalDateTime.parse(latestByHouseId.getDueTime(), formatter);

            if (LocalDateTime.now().isAfter(dueTime) && house.getRentStatus() == 1){
                //已过到期时间，上架房源
                if (house.getRentType() == 1){
                    //合租
                    if (house.getListingStatus() == 0){
                        house.setListingStatus(1);
                        house.setRentStatus(0);
                        house.setListTime(latestByHouseId.getDueTime());
                        house.setRentRoomNumber(1);
                    }else {
                        house.setRentRoomNumber(house.getRentRoomNumber() + 1);
                    }
                }else {
                    house.setListingStatus(1);
                    house.setRentStatus(0);
                    house.setListTime(latestByHouseId.getDueTime());
                }
                houseMapper.updateById(house);
            }
        }

    }
}
