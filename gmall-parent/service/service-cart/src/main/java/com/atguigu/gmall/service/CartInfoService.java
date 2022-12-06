package com.atguigu.gmall.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;
import java.util.Map;

/**
 * 购物车相关的接口类
 */
public interface CartInfoService {
    /**
     * 添加购物车：哪个人买哪个商品，买几个
     */
    void addCart(Long skuId,Integer num);

    /**
     * 查新用户的购物车数据
     * @return
     */
    List<CartInfo> getCartInfo();

    /**
     * 删除购物车
     * @param id
     */
    void removeCart(Long id);

    /**
     * 选中或者取消选中
     * @param status
     * @param id
     */
    void checkOrChecked(Short status,Long id);

    /**
     * 合并购物车（批量新增）
     * @param cartInfoList
     */
    void mergeCart(List<CartInfo> cartInfoList);

    /**
     * 查询订单页面选中的购物车数据
     * @return
     */
    public Map<String, Object> getCheckCart();

    /**
     * 清除用户选中的购物车数据: 时机-->订单生成成功后
     */
    public void removeCheckCart();
}
