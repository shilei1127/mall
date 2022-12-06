package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * 用户收货地址信息的接口类
 */
public interface UserAddressService {
    /**
     * 查询指定用户的收货地址
     * @param username
     * @return
     */
    List<UserAddress> getUserAddressList();
}
