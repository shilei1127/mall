package com.atguigu.gmall.oauth.service;

import com.atguigu.gmall.oauth.util.AuthToken;

public interface LoginService {
    /**
     * 测试用户登录
     *
     * @param username
     * @param password
     * @return
     */
    AuthToken login(String username, String password);
}
