package com.atguigu.gmall.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * 购物车微服务提供的feign接口
 */
@FeignClient(name = "service-cart", path = "/api/cart", contextId = "cartFeign")
public interface CartFeign {

    /**
     * 查询指定用户选中的购物车数据
     * @return
     */
    @GetMapping(value = "/getAddOrderCart")
    public Map<String, Object> getCheckCart();

    /**
     * 清除用户选中的购物车数据: 时机-->订单生成成功后
     */
    @GetMapping(value = "/removeCheckCart")
    public void removeCheckCart();
}
