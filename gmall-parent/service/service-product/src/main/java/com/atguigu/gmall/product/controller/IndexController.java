package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页使用的控制层
 */
@RestController
@RequestMapping("/index/product")
public class IndexController {
    @Autowired
    private IndexService indexService;

    /**
     * 获取首页的分类信息
     * @return
     */
    @GetMapping
    public Result getIndexCategory(){
        return Result.ok(indexService.getIndexCategory());
    }
}
