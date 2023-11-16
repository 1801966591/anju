package com.anju.springboot.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: ZhongZhen
 * @PackageName: com.anju.springboot.common
 * @ClassName: Result
 * @Time: 2023/9/18 22:23
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Result {



    private String code;
    private String msg;
    private Object data;


    public static Result success() {
        return new Result(Constant.CODE_SUCCESS, "请求成功", null);
    }

    public static Result success(Object data) {
        return new Result(Constant.CODE_SUCCESS, "请求成功", data);
    }

    public static Result error(String msg) {
        return new Result(Constant.CODE_SYS_ERROR, msg, null);
    }

    public static Result error(String code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result error() {
        return new Result(Constant.CODE_SYS_ERROR, "系统错误", null);
    }

}
