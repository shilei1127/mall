package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.feign.ProductFeign;
import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 搜索商品相关的接口类的实现类
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 将数据库中的商品写入es
     *
     * @param skuId
     */
    @Override
    public void addGoodsFromDbToEs(Long skuId) {
        //参数校验
        if (skuId == null)
            throw new RuntimeException("参数有误");
        //查询商品的信息-->SkuInfo对象
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        if (skuInfo == null || skuInfo.getId() == null)
            throw new RuntimeException("商品不存在");
        //将SkuInfo对象转换为ES的pojo对象
        Goods goods = new Goods();
        goods.setId(skuInfo.getId());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        //远程调用查询价格的feign
        BigDecimal price = productFeign.getPrice(skuId);
        goods.setPrice(price.doubleValue());
        goods.setCreateTime(new Date());
        //远程调用品牌trademark的接口
        BaseTrademark baseTrademark = productFeign.getBaseTrademark(skuInfo.getTmId());
        goods.setTmId(baseTrademark.getId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        //远程调用查询分类信息的接口
        BaseCategoryView categoryView = productFeign.getCategoryView(skuInfo.getCategory3Id());
        goods.setCategory1Id(categoryView.getCategory1Id());
        goods.setCategory1Name(categoryView.getCategory1Name());
        goods.setCategory2Id(categoryView.getCategory2Id());
        goods.setCategory2Name(categoryView.getCategory2Name());
        goods.setCategory3Id(categoryView.getCategory3Id());
        goods.setCategory3Name(categoryView.getCategory3Name());
        //查询sku的平台属性列表--TODO
        List<BaseAttrInfo> baseAttrInfoList = productFeign.getBaseAttrInfoList(skuId);
        List<SearchAttr> searchList = baseAttrInfoList.stream().map(baseAttrInfo -> {
            //初始化
            SearchAttr searchAttr = new SearchAttr();
            searchAttr.setAttrId(baseAttrInfo.getId());
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            //拿到第0个值
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            //返回值
            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(searchList);
        //将数据写入到es中去--TODO
        goodsDao.save(goods);
    }

    /**
     * 从es中移除商品
     *
     * @param goodsId
     */
    @Override
    public void removeGoodsFromEs(Long goodsId) {
        goodsDao.deleteById(goodsId);
    }

    /**
     * 为商品添加热度值
     *
     * @param goodsId
     */
    @Override
    public void addScore(Long goodsId) {
        //参数校验
        if (goodsId == null)
            return;
        //使用redis进行++操作
//        Long score = redisTemplate.opsForValue().increment("GoodsScore_" + goodsId, 1);
        Double score = redisTemplate.opsForZSet().incrementScore("GoodsScore_" + goodsId, goodsId, 1);
        //每200分同步一次
        if (score % 200 ==0){
            //将数据写回es
            Optional<Goods> goodsOptional = goodsDao.findById(goodsId);
            if (goodsOptional.isPresent()){
                Goods goods = goodsOptional.get();
                //更新分数
                goods.setHotScore(score.longValue());
                goodsDao.save(goods);
            }
        }
    }
}
