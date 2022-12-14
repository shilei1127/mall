package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表的mappper映射
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
