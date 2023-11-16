package com.anju.springboot.mapper;

import com.anju.springboot.entity.DicType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Mapper
public interface DicTypeMapper extends BaseMapper<DicType> {

    @Update("""
            update dic_type 
            set name=#{name},description=#{description}
            where code=#{code}
            """)
    int updateDicType(DicType dicType);

    @Delete("""
            delete from dic_type where code=#{code}
            """)
    int deleteByCode(@Param("code") String code);

    @Select("""
            select * from dic_type where name=#{name}
            """)
    DicType getByName(@Param("name") String name);

    @Select("""
            select * from dic_type where code=#{code}
            """)
    DicType getByCode(@Param("code") String code);
}
