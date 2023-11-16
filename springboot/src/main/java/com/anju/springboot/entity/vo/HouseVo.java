package com.anju.springboot.entity.vo;

import com.anju.springboot.entity.DicValue;
import com.anju.springboot.entity.House;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot.entity.vo
 * @ClassName: HouseVo
 * @Time: 2023/9/28 16:31
 * @Description: TODO
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HouseVo extends House {

    private List<String> imgs;

    private Integer auditStatus;
    private String content;

    private String houseArea;

    private Integer provinceId;
    private Integer cityId;
    private Integer areaId;
    private String province;
    private String city;
    private String area;
    private String address;

    List<DicValue> configs;
    List<Integer> configIds;
    List<String> tags;
    List<Integer> tagIds;

    private String author;
    private String avatar;
    private String nickname;

    private Integer reserveStatus;

    private List<HouseVo> records;
    private Integer total;

}
