package com.anju.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Area;
import com.anju.springboot.entity.City;
import com.anju.springboot.entity.Province;
import com.anju.springboot.entity.vo.AreaVo;
import com.anju.springboot.entity.vo.CityVo;
import com.anju.springboot.entity.vo.ProvinceVo;
import com.anju.springboot.service.AreaService;
import com.anju.springboot.service.CityService;
import com.anju.springboot.service.ProvinceService;
import com.anju.springboot.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot.controller
 * @ClassName: LocationController
 * @Time: 2023/9/18 19:16
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@Slf4j
public class LocationController {

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private CityService cityService;

    @Autowired
    private AreaService areaService;

    @Autowired
    RedisUtil redisUtil;

    @GetMapping("/getLocation")
    public Result getLocation(){
        if(redisUtil.get("Anju_location") != null){
            return Result.success(redisUtil.get("Anju_location"));
        }else {
            // 创建一个空列表，用于存储包含省份、城市和区域信息的树形结构
            List<ProvinceVo> list = new ArrayList<>();

            // 获取所有省份的列表
            List<Province> provinceList = provinceService.list();

            // 遍历每个省份
            for (Province province : provinceList) {

                ProvinceVo provinceVo = new ProvinceVo();
                provinceVo.setValue(province.getProvinceID());
                provinceVo.setLabel(province.getProvince());

                // 创建查询条件，用于查找该省份下的城市列表
                LambdaQueryWrapper<City> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(City::getFather, province.getProvinceID());

                // 获取该省份下的城市列表
                List<City> cityList = cityService.list(queryWrapper);

                // 创建一个空列表，用于存储城市及其下属的区域信息
                List<CityVo> provinceChildren = new ArrayList<>();

                // 遍历每个城市
                for (City city : cityList) {

                    CityVo cityVo = new CityVo();
                    cityVo.setValue(city.getCityID());
                    cityVo.setLabel(city.getCity());

                    // 创建查询条件，用于查找该城市下的区域列表
                    LambdaQueryWrapper<Area> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(Area::getFather, city.getCityID());

                    // 获取该城市下的区域列表
                    List<Area> areaList = areaService.list(queryWrapper1);
                    List<AreaVo> areaVoList = new ArrayList<>();

                    for (Area area : areaList){
                        AreaVo areaVo = new AreaVo(area.getAreaID(),area.getArea());
                        areaVoList.add(areaVo);
                    }

                    cityVo.setChildren(areaVoList);

                    // 将城市添加到城市列表中
                    provinceChildren.add(cityVo);
                }

                // 将城市列表设置为省份的子节点
                provinceVo.setChildren(provinceChildren);

                // 将省份添加到最终的结果列表中
                list.add(provinceVo);
            }
            // 将位置列表存入Redis中
            redisUtil.set("Anju_location",list,-1);
            log.info("已将位置信息存入redis中");
            return Result.success(list);
        }

    }

    @GetMapping("/getLonAndLatByAddress/{address}")
    public Result getLonAndLatByAddress(@PathVariable("address") String address){

        String location = "";
        String GD_URL = "https://restapi.amap.com/v3/geocode/geo?address="+ address +"&key="+ Constant.LOCATION_KEY;
        //高德接口返回的是JSON格式的字符串
        String queryResult = getResponse(GD_URL);
        JSONObject obj = JSONObject.parseObject(queryResult);
        if(String.valueOf(obj.get("status")).equals(Constant.SUCCESS_FLAG)){
            JSONObject jobJSON = JSONObject.parseObject(obj.get("geocodes").toString().substring(1, obj.get("geocodes").toString().length() - 1));
            location = String.valueOf(jobJSON.get("location"));
        }else{
            throw new RuntimeException("地址转换经纬度失败，错误码：" + obj.get("infocode"));
        }
        return Result.success(location);
    }

    /**
     * 发送请求
     *
     * @param serverUrl 请求地址
     */
    private static String getResponse(String serverUrl) {
        // 用JAVA发起http请求，并返回json格式的结果
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(serverUrl);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            in.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result.toString();
    }

}
