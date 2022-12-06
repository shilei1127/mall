package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.feign.ProductFeign;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 查询商品详情页的全部信息
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getItemInfo(Long skuId) {
        Map<String, Object> result = new ConcurrentHashMap<>();
        //1.参数校验
        if(skuId == null)
            return result;
        //2.必须先查询skuInfo的信息
        CompletableFuture<SkuInfo> future1 = CompletableFuture.supplyAsync(()->{
            SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
            //3.判断商品是否存在，若不存在直接结束
            if (skuInfo ==null || skuInfo.getSpuId() ==null){
                return null;
            }
            result.put("skuInfo",skuInfo);
            return skuInfo;
        },threadPoolExecutor);
        //4.查询分类的信息
        CompletableFuture<Void> future2 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在，不存在直接结束
            if (skuInfo == null || skuInfo.getId() == null)
                return;
            Long category3Id = skuInfo.getCategory3Id();
            BaseCategoryView category = productFeign.getCategoryView(category3Id);
            result.put("category", category);
        },threadPoolExecutor);
        //5.查看图片列表
        CompletableFuture<Void> future3 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在，不存在直接结束
            if (skuInfo == null || skuInfo.getId() == null)
                return;
            List<SkuImage> skuImageList = productFeign.getSkuImageList(skuInfo.getId());
            result.put("skuImageList", skuImageList);
        },threadPoolExecutor);
        //6.查询价格
        CompletableFuture<Void> future4 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在，不存在直接结束
            if (skuInfo == null || skuInfo.getId() == null) {
                return;
            }
            BigDecimal price = productFeign.getPrice(skuInfo.getId());
            result.put("price", price);
        },threadPoolExecutor);
        //7.销售属性信息，标识出当前sku的销售属性值是哪几个
        CompletableFuture<Void> future5 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在，不存在直接结束
            if (skuInfo == null || skuInfo.getId() == null) {
                return;
            }
            List<SpuSaleAttr> spuSaleAttrList = productFeign.getSpuSaleAttr(skuInfo.getSpuId(), skuInfo.getId());
            result.put("spuSaleAttrList", spuSaleAttrList);
        },threadPoolExecutor);
        //将键值对组合封装到result集合中
        CompletableFuture<Void> future6 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在，不存在直接结束
            if (skuInfo == null || skuInfo.getId() == null) {
                return;
            }
            Map skuSaleAttrInfo = productFeign.getSkuSaleAttrInfo(skuInfo.getSpuId());
            result.put("skuSaleAttrInfo", skuSaleAttrInfo);
        },threadPoolExecutor);
        //等待全部任务结束才能返回
        CompletableFuture.allOf(future1,future2,future3,future4,future5,future6).join();
        //返回结果
        return result;
    }
}
