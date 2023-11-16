package com.anju.springboot.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.anju.springboot.entity.User;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author: ZhongZhen
 * @PackageName: com.xx.springboot.interceptor
 * @ClassName: JwtInterceptor
 * @Time: 2023/8/18 12:28
 * @Description: TODO
 * @Version: 1.0
 */



/**
 * jwt拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    @Resource
    private UserService adminService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 从http请求的header中获取token
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            // 如果没拿到，我再去参数里面拿一波试试  /api/admin?token=xxxxx
            token = request.getParameter("token");
        }
        // 2. 开始执行认证
        if (StrUtil.isBlank(token)) {
            throw new TokenException("无token，请重新登录");
        }
        // 获取 token 中的userId
        String userId;
        User user;
        try {
            userId = JWT.decode(token).getAudience().get(0);
            // 根据token中的userid查询数据库
            user = adminService.getById(Integer.parseInt(userId));
        } catch (Exception e) {
            String errMsg = "登录过期，请重新登录";
            log.error(errMsg + ", token=" + token, e);
            throw new TokenException(errMsg);
        }
        if (user == null) {
            throw new TokenException("用户不存在，请重新登录");
        }
        try {
            // 用户密码加签验证 token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
            jwtVerifier.verify(token); // 验证token
        } catch (JWTVerificationException e) {
            throw new TokenException("登录过期，请重新登录");
        }
        return true;
    }
}
