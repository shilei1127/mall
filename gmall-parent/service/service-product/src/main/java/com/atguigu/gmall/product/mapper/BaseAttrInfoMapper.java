package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 平台属性表的mapper映射
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    List<BaseAttrInfo> selectBaseAttrInfoByCategoryId(@Param("category1Id") Long category1Id,
                                                      @Param("category2Id") Long category2Id,
                                                      @Param("category3Id") Long category3Id);

    /**
     * 根据skuId查询sku的全部平台属性值和名字(一对一)
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> selectAttrInfoBySkuId(@Param("skuId") Long skuId);
}
