package com.anju.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.User;
import com.anju.springboot.entity.param.Param;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
public interface UserService extends IService<User> {

    Result register(Param param) throws Exception;

    Result login(Param param);

    Result getByCondition(Param param);

    Result saveUser(Param param) throws Exception;

    Result disableAdminByIds(List<Integer> ids) throws Exception;

    Result updatePassword(Param param) throws Exception;

    Result sendEmail(Param param);

    Result findBackPassword(Param param) throws Exception;

    Result getTotalExistAdmin();

}
