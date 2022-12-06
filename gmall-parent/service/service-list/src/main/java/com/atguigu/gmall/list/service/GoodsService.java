package com.atguigu.gmall.list.service;

/**
 * 搜索商品相关的接口类
 */
public interface GoodsService {
    /**
     * 将数据库中的商品写入es
     * @param skuId
     */
    void addGoodsFromDbToEs(Long skuId);

    /**
     * 从es中移除商品
     * @param goodsId
     */
    void removeGoodsFromEs(Long goodsId);

    /**
     * 为商品添加热度值
     * @param goodsId
     */
    void addScore(Long goodsId);
}
