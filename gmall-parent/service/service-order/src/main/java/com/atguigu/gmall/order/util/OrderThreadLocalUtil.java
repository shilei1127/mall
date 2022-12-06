package com.atguigu.gmall.order.util;

/**
 * 用户微服务使用的本地线程工具类
 */
public class OrderThreadLocalUtil {

    //定义一个全局变量
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     * 获取本地线程中存储的值
     * @return
     */
    public static String get(){
        return threadLocal.get();
    }

    /**
     * 存储用户名到本地线程类
     * @param username
     */
    public static void set(String username){
        threadLocal.set(username);
    }
}
