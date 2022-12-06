package com.atguigu.gmall.common.cache;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SlGmallCache {
    String prefix() default "cache";
}
