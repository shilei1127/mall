package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.cart.feign.CartFeign;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.util.OrderThreadLocalUtil;
import com.atguigu.gmall.feign.ProductFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 订单相关的接口类的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartFeign cartFeign;

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeign productFeign;
    /**
     * 新增普通订单
     *
     * @param orderInfo
     */
    @Override
    public void addOrder(OrderInfo orderInfo) {
        //参数校验
        if(orderInfo == null){
            return;
        }
        //获取用户名
        String username = OrderThreadLocalUtil.get();
        //设置用户下单的计数器
        Long increment =
                redisTemplate.opsForValue().increment("User_Add_Order_Cart_" + username, 1);
        if(increment > 1){
            return;
        }
        try {
            //设置这个标识位3秒失效
            redisTemplate.expire("User_Add_Order_Cart_" + username, 10, TimeUnit.SECONDS);
            //远程调用购物车微服务,查询本次购买的全部数据
            Map<String, Object> result = cartFeign.getCheckCart();
            //获取总金额
            Double totalAmount =
                    Double.valueOf(result.get("totalMoney").toString());
            //补全orderInfo的信息
            orderInfo.setTotalAmount(new BigDecimal(totalAmount + ""));
            orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
            orderInfo.setUserId(username);
            orderInfo.setCreateTime(new Date());
            orderInfo.setExpireTime(new Date(System.currentTimeMillis() + 1800000));
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
            //保存orderInfo---service-order(事务一,本地事务)
            int insert = orderInfoMapper.insert(orderInfo);
            if(insert <= 0){
                throw new RuntimeException("新增订单失败!");
            }
            //获取订单号
            Long orderId = orderInfo.getId();
            //保存订单的详情信息---service-order(事务一,本地事务)
            List cartInfoList =
                    (List)result.get("cartInfoList");
            Map<String, Object> decountMap =
                    saveOrderDetail(cartInfoList, orderId);
            //清空本次购买的购物车信息----service-cart(事务二,本地事务)
//            cartFeign.removeCheckCart();
            //扣减库存-----service-product(事务三,本地事务)
            productFeign.decount(decountMap);
        }catch (Exception e){
            throw new RuntimeException("新增订单失败!!");
        }finally {
            redisTemplate.delete("User_Add_Order_Cart_" + username);
        }
    }

    @Resource
    private OrderDetailMapper orderDetailMapper;
    /**
     *  @param cartInfoList
     * @param orderId
     * @return
     */
    private Map<String, Object> saveOrderDetail(List cartInfoList, Long orderId) {
        //需要扣减商品的id和库存
        Map<String, Object> decountMap = new ConcurrentHashMap();
        //遍历保存
        cartInfoList.stream().forEach(o -> {
            //序列化
            String s = JSONObject.toJSONString(o);
            //反序列化
            CartInfo cartInfo = JSONObject.parseObject(s, CartInfo.class);
            //初始化订单详情对象
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(
                    cartInfo.getSkuPrice().multiply(
                            new BigDecimal(cartInfo.getSkuNum())));
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            //保存
            int insert = orderDetailMapper.insert(orderDetail);
            if(insert <= 0){
                throw new RuntimeException("新增订单详情失败!");
            }
            decountMap.put(cartInfo.getSkuId() + "", cartInfo.getSkuNum());
        });
        //返回
        return decountMap;
    }
}
