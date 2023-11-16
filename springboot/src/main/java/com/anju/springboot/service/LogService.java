package com.anju.springboot.service;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Log;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 操作日志表 服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
public interface LogService extends IService<Log> {

    Result getLastLoginTime();
}
