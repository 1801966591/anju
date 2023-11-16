package com.anju.springboot.exception;

import lombok.Getter;

/**
 * @Author: ZhongZhen
 * @PackageName: com.xx.springboot.exception
 * @ClassName: CustomException
 * @Time: 2023/8/18 12:25
 * @Description: TODO
 * @Version: 1.0
 */
@Getter
public class CustomException extends RuntimeException {
    private final String msg;

    public CustomException(String msg) {
        this.msg = msg;
    }

}
