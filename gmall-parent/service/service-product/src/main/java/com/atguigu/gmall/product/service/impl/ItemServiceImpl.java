package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 提供给商品详情微服务内部调用的接口类
 */
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private BaseTrademarkMapper trademarkMapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    /**
     * 根据skuId查询sku的商品信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }

    /**
     * 使用缓存优化查询
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfoFromRedisOrMysql(Long skuId) {
        //1.从redis中获取数据  key===>skuInfo:1:info
        SkuInfo skuInfo =
                (SkuInfo) redisTemplate.opsForValue().get("skuInfo:" + skuId + ":info");
        //若redis中有数据，直接将结果返回
        if (skuInfo != null)
            return skuInfo;
        //若redis中没有
        RLock lock = redissonClient.getLock("skuInfo:" + skuId + ":lock");
        //获取锁，只有获取锁成功的线程才去查数据库，其他的线程等待
        try {
            if (lock.tryLock(100, 100, TimeUnit.SECONDS)) {
                try {
                    //查询数据库
                    skuInfo = skuInfoMapper.selectById(skuId);
                    if (skuInfo == null || skuInfo.getSpuId() == null) {
                        //若数据库也没有--->穿透
                        skuInfo = new SkuInfo(); //新建一对象返回(注意：对象不为空，但是里面的属性是空的)
                        //DB没有值就在Redis中写入一个值(注意：要设置一个短暂的过期时间)
                        redisTemplate.opsForValue().set("skuInfo:" + skuId + ":info", skuInfo,300,TimeUnit.SECONDS);
                    }else {
                        //数据库有，写入redis
                        redisTemplate.opsForValue().set("skuInfo:"+skuId+":info",skuInfo,24*60*60,TimeUnit.SECONDS);
                    }
                    //返回
                    return skuInfo;
                } catch (Exception e) {
                    log.error("加锁成功，但是操作出现异常，异常内容为：" + e.getMessage());
                } finally {
                    //释放锁
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("加锁失败，异常信息:" + e.getMessage());
        }
        return null;
    }

    /**
     * 根据三级分类id查询一级二级三级分类的信息
     *
     * @param category3Id
     * @return
     */
    @Override
    public BaseCategoryView getCategory(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    /**
     * 根据skuI查看图片列表信息
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getSkuImageList(Long skuId) {
        return skuImageMapper.selectList(
                new LambdaQueryWrapper<SkuImage>()
                        .eq(SkuImage::getSkuId, skuId));
    }

    /**
     * 查询商品的实时价格
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getPrice(Long skuId) {
        return skuInfoMapper.selectById(skuId).getPrice();
    }

    /**
     * 根据skuId和spuId查询销售属性和值以及标识出当前的sku值是哪些
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(Long skuId, Long spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrBySpuAndSku(spuId, skuId);
    }

    /**
     * 查询销售属性值的键值对
     *
     * @param spuId
     * @return
     */
    @Override
    public Map getSkuSaleAttrList(Long spuId) {
        List<Map> maps = skuSaleAttrValueMapper.selectSkuValuesList(spuId);
        Map result = new ConcurrentHashMap();
        maps.stream().forEach(m -> {
            //获取sku的id
            Object skuId = m.get("sku_id");
            //获取sku对应的销售属性值的组合
            Object valuesId = m.get("values_id");
            //放到map中保存
            result.put(valuesId, skuId);
        });
        return result;
    }

    /**
     * 根据品牌id查询品牌信息
     * @param id
     * @return
     */
    @Override
    public BaseTrademark getBaseTrademark(Long id) {
        return trademarkMapper.selectById(id);
    }

    /**
     * 根据skuId查询sku的全部平台属性值和名字
     *
     * @param skuId
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId) {
        return baseAttrInfoMapper.selectAttrInfoBySkuId(skuId);
    }

    /**
     * 扣减库存
     *
     * @param decountMap
     */
    @Override
    public void decount(Map<String, Object> decountMap) {
        //遍历扣减库存
        decountMap.entrySet().stream().forEach(entry ->{
            /**
             * 超卖问题--->库存混乱问题!!
             */
            //获取商品id
            long skuId = Long.parseLong(entry.getKey());
            //获取扣减的数量
            int num = Integer.parseInt(entry.getValue().toString());
            //扣减库存
            int decount = skuInfoMapper.decount(skuId, num);
            if(decount <= 0){
                throw new RuntimeException("扣减库存失败,下单失败!");
            }
        });
    }
}
