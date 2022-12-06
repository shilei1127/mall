package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu销售属性名称表的mapper映射
 */
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    /**
     * 根据spu的id查询这个spu的全部销售属性和每个销售属性对应的值列表
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> selectSpuSaleAttrBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据skuId和spuId查询销售属性和值以及标识出当前的sku值是哪些
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrBySpuAndSku(@Param("spuId") Long spuId,
                                                @Param("skuId") Long skuId);
}
