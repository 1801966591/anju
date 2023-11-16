package com.anju.springboot.entity.param;

import lombok.Data;

import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.zz.springboot.entity.param
 * @ClassName: param
 * @Time: 2023/9/18 20:36
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class Param {
    private Integer id;
    private String username;
    private String nickname;
    private String realName;
    private Integer gender;
    private String password;
    private String mobile;
    private String email;
    private String avatar;

    private String oldPassword;
    private String newPassword;

    private String role;
    private Integer roleId;
    private String name;

    private String code;

    private String title;

    private List<Integer> ids;
    private List<Integer> menuIds;

    private Integer currentPage;
    private Integer pageSize;

    private String address;
    private String locationKey;

    private Integer userId;
    private Integer houseId;
    private String time;
    private String remark;

}
