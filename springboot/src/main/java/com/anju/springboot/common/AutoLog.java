package com.anju.springboot.common;

import java.lang.annotation.*;

/**
 * @Author: ZhongZhen
 * @PackageName: com.anju.springboot.common
 * @ClassName: AutoLog
 * @Time: 2023/9/19 10:48
 * @Description: TODO
 * @Version: 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoLog {
    String value() default "";
}
