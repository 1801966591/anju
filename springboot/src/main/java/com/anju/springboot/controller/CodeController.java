package com.anju.springboot.controller;

import com.anju.springboot.common.Result;
import com.anju.springboot.utils.CodeUtils;
import com.anju.springboot.utils.RedisUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author: ZhongZhen
 * @PackageName: com.anju.springboot.controller
 * @ClassName: CodeController
 * @Time: 2023/7/13 11:30
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@CrossOrigin
public class CodeController {

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/code")
    public void code(HttpServletResponse response) throws IOException {

        int width = 200;
        int height = 60;
        String imgType = "jpg";
        ServletOutputStream outputStream = response.getOutputStream();
        String code = CodeUtils.create(width, height, imgType, outputStream);

        // 存储验证码到 Redis，设置有效期为2分钟
        redisUtil.set("AnJu_code", code);
        redisUtil.expire("AnJu_code",120);

    }

    @GetMapping("/getCode")
    public Result getCode(){

        if (redisUtil.hasKey("AnJu_code")){
            String code = redisUtil.get("AnJu_code").toString();
            return Result.success(code);
        }else {
            return Result.error("验证码失效");
        }
    }

}
