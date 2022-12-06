package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/address")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 查询指定用户的收货地址信息
     * @return
     */
    @GetMapping("/getUserAddress")
    public Result getUserAddress(){
        return Result.ok(userAddressService.getUserAddressList());
    }
}
