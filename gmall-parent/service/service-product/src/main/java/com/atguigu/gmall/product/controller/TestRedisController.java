package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.TestRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 测试线程安全的控制层
 */
@RestController
@RequestMapping("/testRedis/product")
public class TestRedisController {

    @Resource
    private TestRedisService testRedisService;

    @GetMapping
    public Result testRedis(){
        testRedisService.setRedis();
        return Result.ok();
    }
}
