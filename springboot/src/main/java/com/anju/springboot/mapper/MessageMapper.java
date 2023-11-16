package com.anju.springboot.mapper;

import com.anju.springboot.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Insert("""
            insert into message (title, content, receiver_id, sender_id, read_status, create_time) values (
                #{title},#{content},#{receiverId},#{senderId},#{readStatus},#{createTime}
            );
            """)
    void saveMessage(@Param("title") String title,
                     @Param("content") String content,
                     @Param("receiverId") Integer receiverId,
                     @Param("senderId") Integer senderId,
                     @Param("readStatus") Integer readStatus,
                     @Param("createTime") String createTime);

}
