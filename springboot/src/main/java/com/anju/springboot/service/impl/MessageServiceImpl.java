package com.anju.springboot.service.impl;

import com.anju.springboot.entity.Message;
import com.anju.springboot.mapper.MessageMapper;
import com.anju.springboot.service.MessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

}
