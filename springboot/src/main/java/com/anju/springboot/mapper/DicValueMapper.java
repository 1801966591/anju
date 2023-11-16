package com.anju.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.anju.springboot.entity.DicValue;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Mapper
public interface DicValueMapper extends BaseMapper<DicValue> {

    @Delete("""
            delete from dic_value where code=#{code}
            """)
    int deleteByCode(@Param("code") String code);

    @Select("""
            select * from dic_value where name=#{name}
            """)
    DicValue getByName(@Param("name") String name);

    @Select("""
            select * from dic_value where value=#{value}
            """)
    DicValue getByValue(@Param("value") String value);
}
