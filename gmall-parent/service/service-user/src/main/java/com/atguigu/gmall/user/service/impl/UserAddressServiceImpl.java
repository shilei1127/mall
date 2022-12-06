package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.dao.UserAddressMapper;
import com.atguigu.gmall.user.service.UserAddressService;
import com.atguigu.gmall.user.util.UserThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserAddressServiceImpl implements UserAddressService {
    @Autowired
    private UserAddressMapper userAddressMapper;
    /**
     * 查询指定用户的收货地址
     * @return
     */
    @Override
    public List<UserAddress> getUserAddressList() {
        String username = UserThreadLocalUtil.get();
        return userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId,username));
    }
}
