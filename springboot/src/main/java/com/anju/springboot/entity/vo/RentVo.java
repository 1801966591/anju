package com.anju.springboot.entity.vo;

import com.anju.springboot.entity.Rent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot.entity.vo
 * @ClassName: RentOrderVo
 * @Time: 2023/10/18 10:46
 * @Description: TODO
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RentVo extends Rent {

    private String houseName;
    private String img;

    private List<RentVo> records;
    private Integer total;
}
