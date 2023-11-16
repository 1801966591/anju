package com.anju.springboot.config;

import com.anju.springboot.interceptor.JwtInterceptor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: ZhongZhen
 * @PackageName: com.anju.springboot.config
 * @ClassName: WebConfig
 * @Time: 2023/9/19 12:15
 * @Description: TODO
 * @Version: 1.0
 */

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        log.info("指定controller统一的接口前缀");
        // 指定controller统一的接口前缀
        configurer.addPathPrefix("/api", clazz -> clazz.isAnnotationPresent(RestController.class));
    }

    @Resource
    private JwtInterceptor jwtInterceptor;

    // 加自定义拦截器JwtInterceptor，设置拦截规则
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("加自定义拦截器JwtInterceptor，设置拦截规则");
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/register")
                .excludePathPatterns("/api/user/userLogin")
                .excludePathPatterns("/api/user/login")
                .excludePathPatterns("/api/code")
                .excludePathPatterns("/api/getCode")
                .excludePathPatterns("/api/user/sendEmail")
                .excludePathPatterns("/api/user/findBackPassword")
                .excludePathPatterns("/api/upload")
                .excludePathPatterns("/api/files/**")
                .excludePathPatterns("/api/alipay/**")
                .excludePathPatterns("/api/getLocation")
                .excludePathPatterns("/api/house/userSearch")
                .excludePathPatterns("/api/house/detail/**")
                .excludePathPatterns("/api/getLonAndLatByAddress/**");
    }
}
