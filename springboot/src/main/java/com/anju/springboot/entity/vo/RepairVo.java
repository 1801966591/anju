package com.anju.springboot.entity.vo;

import com.anju.springboot.entity.Repair;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot.entity.vo
 * @ClassName: RepairVo
 * @Time: 2023/10/19 21:27
 * @Description: TODO
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RepairVo extends Repair {

    private String houseName;
    private String name;
    private String mobile;

    private List<RepairVo> records;
    private Integer total;
}
