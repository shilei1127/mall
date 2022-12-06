package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.cache.SlGmallCache;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品详情页使用的控制层
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Resource
    private ItemService itemService;

    /**
     * 根据skuId查询sku商品
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    @SlGmallCache(prefix = "getSkuInfo:")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
        return itemService.getSkuInfo(skuId);
    }

    /**
     * 根据三级id查询一级二级三级信息
     * @param category3Id
     * @return
     */
    @GetMapping("/getCategory/{category3Id}")
    @SlGmallCache(prefix = "getCategoryView:")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id){
        return itemService.getCategory(category3Id);
    }

    /**
     * 根据skuId查看图片列表
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuImageList/{skuId}")
    @SlGmallCache(prefix = "getSkuImageList:")
    public List<SkuImage> getSkuImageList(@PathVariable("skuId") Long skuId){
        return itemService.getSkuImageList(skuId);
    }

    /**
     * 根据skuId查看实时价格
     * @param skuId
     * @return
     */
    @GetMapping("/getPrice/{skuId}")
    @SlGmallCache(prefix = "getPrice:")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId){
        return itemService.getPrice(skuId);
    }

    /**
     * 根据skuId和spuId查询销售属性和值以及标识出当前的sku值是哪些
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping("/getSpuSaleAttr/{spuId}/{skuId}")
    @SlGmallCache(prefix = "getSpuSaleAttr:")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable("spuId")Long spuId
                                            ,@PathVariable("skuId")Long skuId){
        return itemService.getSpuSaleAttr(skuId,spuId);
    }

    /**
     * 查询销售属性值和id的键值对
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuSaleAttrInfo/{spuId}")
    @SlGmallCache(prefix = "getSkuSaleAttrInfo:")
    public Map getSkuSaleAttrInfo(@PathVariable("spuId") Long spuId){
        return itemService.getSkuSaleAttrList(spuId);
    }

    /**
     * 根据品牌id查询品牌信息
     * @param id
     * @return
     */
    @GetMapping("/getBaseTrademark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable("id") Long id){
        return itemService.getBaseTrademark(id);
    }

    /**
     * 根据skuId查询sku的全部平台属性值和名字
     * @param skuId
     * @return
     */
    @GetMapping("/getBaseAttrInfoList/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfoList(@PathVariable("skuId") Long skuId){
        return itemService.getBaseAttrInfo(skuId);
    }

    /**
     * 扣减库存
     * @param decountMap
     */
    @GetMapping(value = "/decount")
    public void decount(@RequestParam Map<String, Object> decountMap){
        itemService.decount(decountMap);
    }
}
