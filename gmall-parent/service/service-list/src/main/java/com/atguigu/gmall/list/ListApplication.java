package com.atguigu.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 搜索微服务的启动类
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign")
@ComponentScan("com.atguigu.gmall")
public class ListApplication {
    public static void main(String[] args) {
        SpringApplication.run(ListApplication.class,args);
    }
}
