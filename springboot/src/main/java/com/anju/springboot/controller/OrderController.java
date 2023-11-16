package com.anju.springboot.controller;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 租赁订单表 前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/search")
    public Result search(Param param){
        return orderService.search(param);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable("id") Integer id){
        System.out.println(id);
        System.out.println(123);
        return orderService.detail(id);
    }

    @DeleteMapping("/cancelOrder/{id}")
    public Result cancelOrder(@PathVariable("id") Integer id) throws Exception{
        return orderService.cancelOrder(id);
    }

    @GetMapping("/getMoney")
    public Result getMoney(){
        return orderService.getMoney();
    }

    @GetMapping("/getCount")
    public Result getCount(){
        return orderService.getCount();
    }

}
