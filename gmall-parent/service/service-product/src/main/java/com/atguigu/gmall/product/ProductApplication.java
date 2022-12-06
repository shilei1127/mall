package com.atguigu.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
@EnableFeignClients("com.atguigu.gmall.list")
//商品管理微服务的启动类
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }
}