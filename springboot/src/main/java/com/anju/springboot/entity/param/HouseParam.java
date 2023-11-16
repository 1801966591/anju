package com.anju.springboot.entity.param;

import com.anju.springboot.entity.House;
import lombok.Data;

import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot.entity.param
 * @ClassName: HouseParam
 * @Time: 2023/9/26 16:38
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class HouseParam extends House {
    private Integer provinceId;
    private Integer cityId;
    private Integer areaId;
    private String address;

    private String viewTime;

    private List<Integer> config;
    private List<Integer> tag;
    private List<String> fileList;

    private Double minRent;
    private Double maxRent;
    private Double minArea;
    private Double maxArea;

    private String orderBy;
    private String order;

    private Integer currentPage;
    private Integer pageSize;




}
