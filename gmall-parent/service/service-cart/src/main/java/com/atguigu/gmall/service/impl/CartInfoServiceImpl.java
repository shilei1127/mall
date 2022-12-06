package com.atguigu.gmall.service.impl;

import com.atguigu.gmall.feign.ProductFeign;
import com.atguigu.gmall.mapper.CartInfoMapper;
import com.atguigu.gmall.model.base.BaseEntity;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.service.CartInfoService;
import com.atguigu.gmall.util.CartThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 购物车接口的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class CartInfoServiceImpl implements CartInfoService {
    @Resource
    private CartInfoMapper cartInfoMapper;
    @Resource
    private ProductFeign productFeign;

    /**
     * 添加购物车：哪个人买哪个商品，买几个
     *
     * @param skuId
     * @param num
     */
    @Override
    public void addCart(Long skuId, Integer num) {
        //参数校验
        if (skuId == null || num == null) {
            throw new RuntimeException("参数有误~");
        }
        //查询商品是否存在
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        if (Objects.isNull(skuInfo) || skuInfo.getId() == null) {
            throw new RuntimeException("商品不存在");
        }
        //查询购物车是否有商品
        CartInfo cartInfo = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, CartThreadLocalUtil.get())
                .eq(CartInfo::getSkuId, skuId));
        if (cartInfo == null || cartInfo.getId() == null) {
            //新增
            //查询商品价格
            BigDecimal price = productFeign.getPrice(skuId);
            //包装购物车对象
            cartInfo = new CartInfo();
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setCartPrice(price);
            cartInfo.setUserId(CartThreadLocalUtil.get());
            cartInfo.setSkuId(skuId);
            //判断购物车数量
            if (num <= 0) {
                throw new RuntimeException("购物车数量不能小于等于0");
            }

            cartInfo.setSkuNum(num >= 200 ? 199 : num);
            //新增购物车数据
            int insert = cartInfoMapper.insert(cartInfo);
            if (insert <= 0) {
                throw new RuntimeException("购物车数据添加失败");
            }
        } else {
            //计算和并后的购物车数量
            num = cartInfo.getSkuNum() + num;
            if (num <= 0) {
                cartInfoMapper.deleteById(cartInfo);
            } else {
                //合并购物车的数量---TODO--存在并发问题--乐观锁
                cartInfo.setSkuNum(num >= 200 ? 199 : num);
                //修改
                int i = cartInfoMapper.updateById(cartInfo);
                if (i < 0) {
                    throw new RuntimeException("新增购物车失败");
                }
            }
        }
    }

    /**
     * 查询用户的购物车数据
     *
     * @return
     */
    @Override
    public List<CartInfo> getCartInfo() {
        List<CartInfo> cartInfos =
                cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>().eq(CartInfo::getUserId, CartThreadLocalUtil.get()));
        return cartInfos;
    }

    /**
     * 删除购物车
     *
     * @param id
     */
    @Override
    public void removeCart(Long id) {
        CartInfo cartInfo = cartInfoMapper.selectById(id);
        if (cartInfo != null && cartInfo.getId() != null) {
            if (!CartThreadLocalUtil.get().equals(cartInfo.getUserId())) {
                log.error("用户只能删除自己的购物车商品");
            }
        }
        int i = cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                .eq(BaseEntity::getId, id)
                .eq(CartInfo::getUserId,CartThreadLocalUtil.get()));
        if (i < 0) {
            throw new RuntimeException("删除购物车失败");
        }
    }

    /**
     * 购物车的选中或者取消选中
     *
     * @param status
     * @param id
     */
    @Override
    public void checkOrChecked(Short status, Long id) {
        int result = 0;
        //全选，id为空
        if (id == null){
            result=cartInfoMapper.checkAll(CartThreadLocalUtil.get(), status);
        }
        else {
            result = cartInfoMapper.checkOne(CartThreadLocalUtil.get(), status, id);
        }
        if (result < 0){
            return;
        }
    }

    /**
     * 合并购物车（批量新增）
     *
     * @param cartInfoList
     */
    @Override
    public void mergeCart(List<CartInfo> cartInfoList) {
        cartInfoList.stream().forEach( cartInfo -> {
            this.addCart(cartInfo.getSkuId(), cartInfo.getSkuNum());
        });
    }

    /**
     * 查询订单确认页面需要的选中购物车数据
     *
     * @return
     */
    @Override
    public Map<String, Object> getCheckCart() {
        //返回结果初始化
        Map<String, Object> result = new HashMap<>();
        //查询本次用户需要购买的全部购物车数据
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(
                new LambdaQueryWrapper<CartInfo>()
                        .eq(CartInfo::getUserId, CartThreadLocalUtil.get())
                        .eq(CartInfo::getIsChecked, 1));
        //判断用户是否一个都没选
        if(cartInfoList == null || cartInfoList.isEmpty()){
            throw new RuntimeException("没有选中购物车数据,不允许下单!");
        }
        //计算总数量,计算总金额
        AtomicInteger totalNum = new AtomicInteger(0);
        AtomicDouble totalMoney = new AtomicDouble(0);
        //为每一个商品获取实时价格
        List<CartInfo> cartInfoListNew = cartInfoList.stream().map(cartInfo -> {
            //获取商品id
            Long skuId = cartInfo.getSkuId();
            //查询实时价格
            BigDecimal price = productFeign.getPrice(skuId);
            //保存实时价格
            cartInfo.setSkuPrice(price);
            //获取当前这笔购物车的商品数量
            Integer skuNum = cartInfo.getSkuNum();
            //累加总数量
            totalNum.getAndAdd(skuNum);
            //累加总金额
            totalMoney.getAndAdd(price.doubleValue()*skuNum);
            //返回
            return cartInfo;
        }).collect(Collectors.toList());
        //返回
        result.put("cartInfoList", cartInfoListNew);
        result.put("totalNum", totalNum);
        result.put("totalMoney",  totalMoney);
        return result;
    }


    /**
     * 清除用户选中的购物车数据: 时机-->订单生成成功后
     */
    @Override
    public void removeCheckCart() {
        int delete = cartInfoMapper.delete(
                new LambdaQueryWrapper<CartInfo>()
                        .eq(CartInfo::getUserId, CartThreadLocalUtil.get())
                        .eq(CartInfo::getIsChecked, 1));
        if(delete < 0){
            throw new RuntimeException("清除购物车失败!!");
        }
    }
}
