package com.anju.springboot.exception;

import com.anju.springboot.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.anju.springboot.common.Constant.CODE_AUTH_ERROR;

@ControllerAdvice(basePackages={"com.anju.springboot.controller","com.anju.springboot.interceptor"})
@Log4j2
public class GlobalExceptionHandler {

    //统一异常处理@ExceptionHandler,主要用于Exception
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(HttpServletRequest request, Exception e){
        log.error("异常信息：",e);
        return Result.error("系统异常");
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result customError(HttpServletRequest request, CustomException e){
        return Result.error(e.getMsg());
    }

    @ExceptionHandler(TokenException.class)
    @ResponseBody
    public Result tokenError(HttpServletRequest request, TokenException e){
        return Result.error(CODE_AUTH_ERROR, e.getMsg());
    }

}