package com.anju.springboot.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhongZhen
 * @PackageName: com.anju.springboot.config
 * @ClassName: AliPayConfig
 * @Time: 2023/10/13 11:32
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "alipay")
@Log4j2
public class AliPayConfig {
    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
}
