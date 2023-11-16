package com.anju.springboot.controller;

import com.anju.springboot.common.Result;
import com.anju.springboot.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@RestController
@RequestMapping("/userRole")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @GetMapping("/getCount")
    public Result getCount(){
        return userRoleService.getCount();
    }

}
