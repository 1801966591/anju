package com.anju.springboot.service.impl;

import com.anju.springboot.entity.Notice;
import com.anju.springboot.mapper.NoticeMapper;
import com.anju.springboot.service.NoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统公告表 服务实现类
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

}
