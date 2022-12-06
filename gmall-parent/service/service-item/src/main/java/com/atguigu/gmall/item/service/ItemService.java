package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.product.SkuInfo;

import java.util.Map;

/**
 * 商品详情页使用的接口类
 */
public interface ItemService {

    /**
     * 查询商品详情页的全部信息
     * @param skuId
     * @return
     */
    Map<String,Object> getItemInfo(Long skuId);

}
