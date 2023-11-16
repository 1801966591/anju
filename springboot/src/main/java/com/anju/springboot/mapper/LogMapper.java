package com.anju.springboot.mapper;

import com.anju.springboot.entity.Log;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 操作日志表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {

    @Select("""
            select * from log
            where username=#{username} and name='登录系统'
            order by time desc
            limit 1
            """)
    Log getLastLoginTime(@Param("username") String username);
}
