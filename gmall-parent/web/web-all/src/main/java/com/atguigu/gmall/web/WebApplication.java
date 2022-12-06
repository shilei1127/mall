package com.atguigu.gmall.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 前端页面工程的启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.atguigu.gmall.item.feign","com.atguigu.gmall.list"})
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class,args);
    }
}
