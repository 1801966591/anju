package com.anju.springboot.entity.vo;

import com.anju.springboot.entity.RentAudit;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot.entity.vo
 * @ClassName: RentAuditVo
 * @Time: 2023/10/12 18:42
 * @Description: TODO
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RentAuditVo extends RentAudit {

    private String houseName;

    private List<RentAuditVo> records;
    private Integer total;

}
