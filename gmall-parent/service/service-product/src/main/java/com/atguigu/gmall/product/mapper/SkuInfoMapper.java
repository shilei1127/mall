package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * skuInfo表的mapper映射
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {
    /**
     * 商品上下架的第二种方案
     * @param skuId
     * @param status
     */
    @Update("update sku_info set is_sale = #{status} where id = #{skuId}}")
    int upOrDown(@Param("skuId") Long skuId,@Param("status") Short status);

    /**
     * 扣减库存
     * @param skuId
     * @param num
     * @return
     */
    @Update("update sku_info set stock = stock - #{num} where id = #{skuId} and stock >= #{num}")
    public int decount(@Param("skuId") Long skuId,
                       @Param("num") Integer num);
}
