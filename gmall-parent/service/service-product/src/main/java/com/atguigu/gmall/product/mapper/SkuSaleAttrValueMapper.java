package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性表的mapper映射
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    /**
     * 根据spu查询这个spu下所有sku的id和销售属性值的键值对
     * @param spuId
     * @return
     */
    @Select("SELECT sku_id,GROUP_CONCAT(DISTINCT sale_attr_value_id " +
            "ORDER BY sale_attr_value_id SEPARATOR '|') AS values_id " +
            "FROM sku_sale_attr_value WHERE spu_id = 1 GROUP BY sku_id")
    List<Map> selectSkuValuesList(@Param("spuId") Long spuId);
}
