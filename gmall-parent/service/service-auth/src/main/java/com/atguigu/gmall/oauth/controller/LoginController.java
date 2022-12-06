package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import com.sun.net.httpserver.HttpsServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user/login")
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 自定义登录
     * @param username
     * @param password
     * @return
     */
    @GetMapping
    public Result login(HttpServletRequest request, String username, String password){
        //获取登录令牌
        AuthToken token = loginService.login(username, password);
        //获取本地的ipv6地址
        String ipAddress = IpUtil.getIpAddress(request);
        //将令牌存到redis中，和ip地址进行绑定
        stringRedisTemplate.opsForValue().set(ipAddress,token.getAccessToken());
        //调用购物车合并的接口---TODO（不能使用feign，要用mq解耦）
        return Result.ok(token);
    }
}
