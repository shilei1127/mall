package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 商品的控制层
 */
@RestController
@RequestMapping("/api/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    /**
     * 创建索引 映射 类型
     * @return
     */
    @GetMapping("/createIndexAndMapping")
    public Result createIndexAndMapping(){
        //创建索引
        elasticsearchRestTemplate.createIndex(Goods.class);
        //创建映射
        elasticsearchRestTemplate.putMapping(Goods.class);
        //返回
        return Result.ok();
    }

    /**
     * 将数据库商品写入es中
     * @param skuId
     * @return
     */
    @GetMapping("/addGoods/{skuId}")
    public Boolean addGoodsFromDbToEs(@PathVariable("skuId") Long skuId){
        goodsService.addGoodsFromDbToEs(skuId);
        return true;
    }

    /**
     * 将数据从es中移除
     * @param goodsId
     * @return
     */
    @DeleteMapping("/removeGoods/{goodsId}")
    public Boolean removeGoodsFromEs(@PathVariable("goodsId") Long goodsId){
        goodsService.removeGoodsFromEs(goodsId);
        return true;
    }

    /**
     * 新增商品的热度值
     * @param goodsId
     * @return
     */
    @GetMapping("/addScore/{goodsId}")
    public Boolean addScore(@PathVariable("goodsId") Long goodsId){
        goodsService.addScore(goodsId);
        return true;
    }
}
