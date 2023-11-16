package com.anju.springboot;

import com.anju.springboot.utils.HashUtils;
import com.anju.springboot.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot
 * @ClassName: SaltHashPasswordTest
 * @Time: 2023/9/24 3:50
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootTest
public class SaltHashPasswordTest {

    @Autowired
    private HashUtils hashUtils;

    @Autowired
    private RedisUtil redisUtil;

    @Test
    void testSaltHashPassword() {
        String s = hashUtils.sha256Hash("123456",null);
        System.out.println("salt = " + redisUtil.get("Anju_salt"));
        System.out.println("s = " + s);
    }
}
