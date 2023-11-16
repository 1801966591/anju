package com.anju.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.anju.springboot.entity.User;
import com.anju.springboot.entity.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("""
            select * from user where username=#{username};
            """)
    User getByUserName(@Param("username") String username);

    List<UserVo> getByCondition(@Param("username") String username,
                                @Param("nickname") String nickname,
                                @Param("realName") String realName,
                                @Param("pageNo") Integer pageNo,
                                @Param("pageSize") Integer pageSize);

    int getTotalByCondition(@Param("username") String username,
                            @Param("nickname") String nickname,
                            @Param("realName") String realName,
                            @Param("pageNo") Integer pageNo,
                            @Param("pageSize") Integer pageSize);

    @Select("""
            select count(u.id)
            from user u
            join user_role ur on u.id=ur.user_id
            join role r on r.id=ur.role_id
            where r.code!='ADMIN'
            """)
    int getTotalExistAdmin();
}
