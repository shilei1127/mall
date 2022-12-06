package com.atguigu.gmall.controller;

import com.atguigu.gmall.service.CartInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 供内部调用使用的api控制层
 */
@RestController
@RequestMapping(value = "/api/cart")
public class CartApiController {

    @Autowired
    private CartInfoService cartInfoService;

    /**
     * 查询指定用户选中的购物车数据
     * @return
     */
    @GetMapping(value = "/getAddOrderCart")
    public Map<String, Object> getAddOrderCart(){
        return cartInfoService.getCheckCart();
    }

    /**
     * 清除用户选中的购物车数据: 时机-->订单生成成功后
     */
    @GetMapping(value = "/removeCheckCart")
    public void removeCheckCart(){
        cartInfoService.removeCheckCart();
    }
}
