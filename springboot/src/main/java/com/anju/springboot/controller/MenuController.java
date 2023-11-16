package com.anju.springboot.controller;

import com.anju.springboot.common.AutoLog;
import com.anju.springboot.common.Result;
import com.anju.springboot.entity.Menu;
import com.anju.springboot.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public Result search(Menu param){
        return Result.success(menuService.getMenus(param.getName()));
    }

    @GetMapping("/ids")
    public Result findAllIds() {
        return Result.success(menuService.list().stream().map(Menu::getId));
    }

    @PostMapping("/save")
    @AutoLog("新增菜单信息")
    public Result saveMenu(@RequestBody Menu menu) throws Exception {

        boolean flag = menuService.save(menu);

        if (flag) {
            return Result.success();
        }else {
            throw new Exception();
        }

    }

    @PutMapping("/update")
    @AutoLog("修改菜单信息")
    public Result update(@RequestBody Menu menu) throws Exception {

        boolean flag = menuService.updateById(menu);

        if (flag) {
            return Result.success();
        }else {
            throw new Exception();
        }

    }

    @DeleteMapping("/deleteById/{id}")
    @AutoLog("删除菜单信息")
    public Result deleteById(@PathVariable("id") Integer id) throws Exception {
        return menuService.deleteById(id);
    }

    @DeleteMapping("/deleteByIds/{ids}")
    @AutoLog("批量删除菜单信息")
    public Result deleteById(@PathVariable("ids") List<Integer> ids) throws Exception {
        return menuService.deleteByIds(ids);
    }

}
