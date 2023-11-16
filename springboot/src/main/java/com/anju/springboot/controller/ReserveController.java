package com.anju.springboot.controller;

import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Reserve;
import com.anju.springboot.service.ReserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-06
 */
@RestController
@RequestMapping("/reserve")
public class ReserveController {

    @Autowired
    private ReserveService reserveService;

    @PostMapping("/save")
    public Result reserve(@RequestBody Reserve reserve) throws Exception{
        return reserveService.reserve(reserve);
    }

    @PutMapping("/update")
    public Result update(@RequestBody Reserve reserve) throws Exception{
        return reserveService.updateReserve(reserve);
    }

    @DeleteMapping("cancelReserve/{id}")
    public Result cancelReserve(@PathVariable Integer id) throws Exception{
        return reserveService.cancelReserve(id);
    }

}
