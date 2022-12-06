package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品详情的微服务接口类
 */
public interface ItemService {
    /**
     * 根据skuId查询sku的商品信息
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 使用缓存优化查询
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfoFromRedisOrMysql(Long skuId);

    /**
     * 根据三级分类id查询一级二级三级分类的信息
     * @param category3Id
     * @return
     */
    BaseCategoryView getCategory(Long category3Id);

    /**
     * 根据skuI查看图片列表信息
     * @param skuId
     * @return
     */
    List<SkuImage> getSkuImageList(Long skuId);

    /**
     * 查询商品的实时价格
     * @param skuId
     * @return
     */
    BigDecimal getPrice(Long skuId);

    /**
     * 根据skuId和spuId查询销售属性和值以及标识出当前的sku值是哪些
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttr(Long skuId, Long spuId);

    /**
     * 查询销售属性值的键值对
     * @param spuId
     * @return
     */
    Map getSkuSaleAttrList(Long spuId);

    /**
     * 查询品牌信息
     * @param id
     * @return
     */
    BaseTrademark getBaseTrademark(Long id);

    /**
     * 根据skuId查询sku的全部平台属性值和名字
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> getBaseAttrInfo(Long skuId);

    /**
     * 扣减库存
     * @param decountMap
     */
    public void decount(Map<String, Object> decountMap);
}
