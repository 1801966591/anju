package com.anju.springboot.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.anju.springboot.entity.Log;
import com.anju.springboot.entity.User;
import com.anju.springboot.service.LogService;
import com.anju.springboot.utils.JwtTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Author: ZhongZhen
 * @PackageName: com.anju.springboot.common
 * @ClassName: logAspect
 * @Time: 2023/9/19 11:12
 * @Description: TODO
 * @Version: 1.0
 */
@Component
@Aspect
@Log4j2
public class logAspect {

    @Autowired
    private LogService logService;

    @Around("@annotation(autoLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, AutoLog autoLog) throws Throwable {
        log.info("操作内容：{}",autoLog.value());
        //操作内容
        String name = autoLog.value();
        //操作时间
        String time = DateUtil.now();
        //操作人
        String username = "";
        User user = JwtTokenUtils.getCurrentUser();
        if (ObjectUtil.isNotNull(user)){
            username = user.getUsername();
        }
        //操作IP
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getRemoteAddr();

        //执行具体的接口
        Result result = (Result) joinPoint.proceed();

        Object data = result.getData();
        if (data instanceof User loginUser){
            username = loginUser.getUsername();
        }

        //再去往操作日志表中新增一条记录
        Log log = new Log(null,name,time,username,ip);
        logService.save(log);

        return result;

    }

}
