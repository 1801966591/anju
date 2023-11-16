package com.anju.springboot.entity.param;

import lombok.Data;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot.entity.param
 * @ClassName: AliPay
 * @Time: 2023/10/13 11:49
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class AliPay {
    private String subject;
    private String traceNo;
    private String totalAmount;
    private String alipayTraceNo;

}
