package com.anju.springboot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.xx.springboot.entity.vo
 * @ClassName: LocationVo
 * @Time: 2023/9/14 16:33
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceVo {

    private String value;
    private String label;
    private List<CityVo> children;

}
