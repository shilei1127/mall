package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

/***
 * 订单相关的接口类
 */
public interface OrderService {

    /**
     * 新增普通订单
     * @param orderInfo
     */
    public void addOrder(OrderInfo orderInfo);
}
