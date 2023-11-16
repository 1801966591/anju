package com.anju.springboot.controller;

import com.anju.springboot.common.AutoLog;
import com.anju.springboot.common.Constant;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Role;
import com.anju.springboot.entity.User;
import com.anju.springboot.entity.UserRole;
import com.anju.springboot.entity.param.Param;
import com.anju.springboot.entity.vo.UserVo;
import com.anju.springboot.exception.CustomException;
import com.anju.springboot.service.RoleService;
import com.anju.springboot.service.UserRoleService;
import com.anju.springboot.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-18
 */
@RestController
@RequestMapping("/user")
@Log4j2
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @PostMapping("/register")
    public Result register(@RequestBody Param param) throws Exception {
        return userService.register(param);
    }

    @PostMapping("/userLogin")
    public Result userLogin(@RequestBody Param param){
        String username = param.getUsername();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        User one = userService.getOne(queryWrapper);
        if (one == null){
            throw new CustomException("用户名不存在");
        }else {
            String role = roleService.getRoleByUserId(one.getId());
            if (!role.equals(Constant.ROLE_USER)){
                throw new CustomException("当前登录用户不是普通用户，请到管理端登录");
            }else {
                return userService.login(param);
            }
        }
    }

    @GetMapping("/sendEmail")
    public Result sendEmail(Param param){
        return userService.sendEmail(param);
    }

    @PutMapping("/findBackPassword")
    public Result findBackPassword(@RequestBody Param param) throws Exception {
        return userService.findBackPassword(param);
    }

    @PostMapping("/login")
    @AutoLog("登录系统")
    public Result login(@RequestBody Param param){
        String username = param.getUsername();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        User one = userService.getOne(queryWrapper);
        if (one == null){
            throw new CustomException("用户名不存在");
        }else {
            String role = roleService.getRoleByUserId(one.getId());
            if (role.equals(Constant.ROLE_USER)){
                throw new CustomException("当前登录用户是普通用户，请到用户端登录");
            }else {
                return userService.login(param);
            }
        }
    }

    @GetMapping("/search")
    public Result search(Param param){
        return userService.getByCondition(param);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Integer id){
        User user = userService.getById(id);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user,userVo);

        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,id);
        UserRole one = userRoleService.getOne(queryWrapper);
        Role role = roleService.getById(one.getRoleId());
        userVo.setRole(role.getName());

        return Result.success(userVo);
    }

    @DeleteMapping("/disableUserById/{id}")
    @AutoLog("禁用用户或启用用户")
    public Result disableUserById(@PathVariable("id") Integer id) throws Exception {

        String role = roleService.getRoleByUserId(id);
        if (role.equals(Constant.ROLE_ADMIN)){
            throw new CustomException("不能禁用管理员");
        }

        User user = userService.getById(id);
        Integer nowStatus = user.getStatus();
        int status = 0;
        //如果用户状态是禁用，则启用该用户
        if (nowStatus == 0){
            status = 1;
        }
        user.setStatus(status);
        boolean flag = userService.updateById(user);

        if (flag) {
            return Result.success(status);
        } else {
            throw new Exception();
        }
    }

    @DeleteMapping("/disableAdminByIds/{ids}")
    @AutoLog("批量禁用用户或批量启用用户")
    public Result disableAdminByIds(@PathVariable("ids") List<Integer> ids) throws Exception {
        return userService.disableAdminByIds(ids);
    }

    @PutMapping("/update")
    public Result update(@RequestBody User user) throws Exception {
        boolean flag = userService.updateById(user);

        if (flag){
            return Result.success();
        }else {
            throw new Exception();
        }
    }

    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Param param) throws Exception {
        return userService.updatePassword(param);
    }

    @GetMapping("/getTotalExistAdmin")
    public Result getTotalExistAdmin(){
        return userService.getTotalExistAdmin();
    }

    @GetMapping("/loginStatus")
    public Result loginStatus(){
        return Result.success();
    }

}
