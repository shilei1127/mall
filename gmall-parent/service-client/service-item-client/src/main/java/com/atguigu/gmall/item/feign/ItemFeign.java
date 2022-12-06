package com.atguigu.gmall.item.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 商品详情微服务的feign接口
 */
@FeignClient(name = "service-item",path = "/item")
public interface ItemFeign {
    /**
     * 查看商品详情页使用的全部信息
     * @param skuId
     * @return
     */
    @GetMapping("/getItemInfo/{skuId}")
    public Map<String,Object> getItemInfo(@PathVariable("skuId") Long skuId);
}
