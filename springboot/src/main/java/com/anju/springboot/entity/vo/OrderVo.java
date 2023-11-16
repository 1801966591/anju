package com.anju.springboot.entity.vo;

import com.anju.springboot.entity.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot.entity.vo
 * @ClassName: RentOrderVo
 * @Time: 2023/10/13 11:00
 * @Description: TODO
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderVo extends Order {
    private String houseName;
    private String img;
    private String mobile;
    private String remark;
    private List<OrderVo> records;
    private Integer total;
}
