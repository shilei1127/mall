package com.atguigu.gmall.feign;

import com.atguigu.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "service-product",path = "/api/item") //声明提供者是哪个服务,path提供通用路径属性
public interface ProductFeign {

    //指定调用者提供哪个方法
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 查看分类信息
     * @param category3Id
     * @return
     */
    @GetMapping("/getCategory/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id);

    /**
     * 根据skuId查看图片列表
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuImageList/{skuId}")
    public List<SkuImage> getSkuImageList(@PathVariable("skuId") Long skuId);

    /**
     * 查看实时价格
     * @param skuId
     * @return
     */
    @GetMapping("/getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId和spuId查询销售属性和值以及标识出当前的sku值是哪些
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping("/getSpuSaleAttr/{spuId}/{skuId}")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable("spuId")Long spuId
            , @PathVariable("skuId")Long skuId);

    /**
     * 查询销售属性值和id的键值对
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuSaleAttrInfo/{spuId}")
    public Map getSkuSaleAttrInfo(@PathVariable("spuId") Long spuId);

    /**
     * 根据品牌id查询品牌信息
     * @param id
     * @return
     */
    @GetMapping("/getBaseTrademark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable("id") Long id);

    /**
     * 根据skuId查询sku的全部平台属性值和名字
     * @param skuId
     * @return
     */
    @GetMapping("/getBaseAttrInfoList/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfoList(@PathVariable("skuId") Long skuId);

    /**
     * 扣减库存
     * @param decountMap
     */
    @GetMapping(value = "/decount")
    public void decount(@RequestParam Map<String, Object> decountMap);
}
