package com.anju.springboot.entity.vo;

import com.anju.springboot.entity.HouseAudit;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot.entity.vo
 * @ClassName: HouseAuditVo
 * @Time: 2023/9/29 17:24
 * @Description: TODO
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HouseAuditVo extends HouseAudit {

    private String name;
    private String author;

    private List<HouseAuditVo> records;
    private Integer total;

}
