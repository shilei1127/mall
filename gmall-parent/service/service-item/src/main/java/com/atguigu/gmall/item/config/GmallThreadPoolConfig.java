package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 商品详情页使用的线程池配置类
 */
@Configuration
public class GmallThreadPoolConfig {
    /**
     * 自定义线程池
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(8,
                8,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000));
    }
}
