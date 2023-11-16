package com.anju.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Message;
import com.anju.springboot.entity.User;
import com.anju.springboot.exception.TokenException;
import com.anju.springboot.service.MessageService;
import com.anju.springboot.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/getUnReadAmount/{receiverId}")
    public Result getUnReadAmount(@PathVariable("receiverId") Integer receiverId) {

        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getReceiverId,receiverId);
        queryWrapper.eq(Message::getReadStatus,0);
        List<Message> list = messageService.list(queryWrapper);

        return Result.success(list.size());

    }

    @GetMapping("/getMessage/{receiverId}")
    public Result getMessage(@PathVariable("receiverId") Integer receiverId) {

        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getReceiverId,receiverId)
                .orderByDesc(Message::getCreateTime);
        List<Message> list = messageService.list(queryWrapper);

        return Result.success(list);
    }

    @PutMapping("/read/{id}")
    public Result read(@PathVariable("id") Integer id) throws Exception {

        Message message = messageService.getById(id);
        message.setReadStatus(1);
        boolean flag = messageService.updateById(message);

        if (flag){
            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @PutMapping("/readAll")
    public Result readAll() throws Exception {
        User user = JwtTokenUtils.getCurrentUser();
        if (user == null){
            throw new TokenException("登录失效");
        }
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getReceiverId,user.getId())
                .eq(Message::getReadStatus,0);
        List<Message> list = messageService.list(queryWrapper).stream().map(item -> {
            item.setReadStatus(1);
            return item;
        }).collect(Collectors.toList());

        boolean flag = messageService.updateBatchById(list);

        if (flag){
            return Result.success();
        }else {
            throw new Exception();
        }

    }

    @GetMapping("/getTotal")
    public Result getTotal(){
        User user = JwtTokenUtils.getCurrentUser();
        if (user == null) {
            throw new TokenException("登录失效");
        }
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getReceiverId,user.getId());
        List<Message> list = messageService.list(queryWrapper);

        return Result.success(list.size());

    }

}
