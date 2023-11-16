package com.anju.springboot.service.impl;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Log;
import com.anju.springboot.entity.User;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.mapper.LogMapper;
import com.anju.springboot.service.LogService;
import com.anju.springboot.utils.JwtTokenUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public Result getLastLoginTime() {
        User user = JwtTokenUtils.getCurrentUser();
        if (user == null) {
            throw new TokenException("登录失效");
        }
        return Result.success(logMapper.getLastLoginTime(user.getUsername()));
    }
}
