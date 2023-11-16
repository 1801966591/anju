package com.anju.springboot.controller;

import com.alipay.api.AlipayApiException;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.service.RentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 租赁信息表 前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-18
 */
@RestController
@RequestMapping("/rent")
public class RentController {

    @Autowired
    private RentService rentService;

    @GetMapping("/search")
    public Result search(Param param){
        return rentService.search(param);
    }

    @GetMapping("/rentOut/{id}")
    public Result rentOut(@PathVariable Integer id)throws AlipayApiException {
        return rentService.rentOut(id);
    }

    @GetMapping("/getCount")
    public Result getCount(){
        return rentService.getCount();
    }

}
