package com.anju.springboot.entity.vo;

import com.anju.springboot.entity.Reserve;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot.entity.vo
 * @ClassName: ReserveVo
 * @Time: 2023/10/7 18:22
 * @Description: TODO
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReserveVo extends Reserve {

    private String houseName;

    private Integer auditId;
    private Integer reserveId;
    private Integer status;
    private String content;

    private List<ReserveVo> records;
    private int total;

}
