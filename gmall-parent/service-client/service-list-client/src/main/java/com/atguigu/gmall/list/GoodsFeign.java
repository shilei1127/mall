package com.atguigu.gmall.list;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商品搜索相关的feign接口
 */
@FeignClient(name = "service-list",path = "/api/goods",contextId = "goodsFeign")
public interface GoodsFeign {

    /**
     * 将数据库商品写入es中
     * @param skuId
     * @return
     */
    @GetMapping("/addGoods/{skuId}")
    public Boolean addGoodsFromDbToEs(@PathVariable("skuId") Long skuId);

    /**
     * 将数据从es中移除
     * @param goodsId
     * @return
     */
    @DeleteMapping("/removeGoods/{goodsId}")
    public Boolean removeGoodsFromEs(@PathVariable("goodsId") Long goodsId);
}
