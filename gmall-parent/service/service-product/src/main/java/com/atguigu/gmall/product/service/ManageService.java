package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface ManageService {
    /**
     * 查询所有一级分类
     * @return
     */
    List<BaseCategory1> getBaseCategory1();

    /**
     * 查询所有二级分类的信息
     * @return
     */
    List<BaseCategory2> getBaseCategory2(Long c1Id);

    /**
     * 根据二级分类id查询三级分类的信息
     * @param c2Id
     * @return
     */
    List<BaseCategory3> getBaseCategory3(Long c2Id);

    /**
     * 新增平台属性值表
     * @param baseAttrInfo
     */
    void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 查询平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> selectBaseAttrInfoByCategoryId(Long category1Id,
                                                      Long category2Id,
                                                      Long category3Id);

    /**
     * 查看平台属性值列表
     * @param attrId
     * @return
     */
    List<BaseAttrValue> getBaseAttrValue(Long attrId);

    /**
     * 查询品牌
     * @return
     */
    List<BaseTrademark> getBaseTrademark();

    /**
     * 查询销售属性
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttr();

    /**
     * 新增spu的信息
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 分页条件查询
     *
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    IPage<SpuInfo> getSpuInfoList(Integer page, Integer size, Long category3Id);

    /**
     * 根据spuId查询销售属性的信息
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrBySpuId(Long spuId);

    /**
     * 根据spuId查询图片信息
     * @param spuId
     * @return
     */
    List<SpuImage> getSpuImageList(Long spuId);

    /**
     * 保存sku
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 分页查询sku的信息
     *
     * @param page
     * @param size
     * @return
     */
    IPage<SkuInfo> list(Integer page, Integer size);

    /**
     *上架或者下架
     * @param skuId
     * @param status
     */
    void upOrDown(Long skuId,Short status);
}
