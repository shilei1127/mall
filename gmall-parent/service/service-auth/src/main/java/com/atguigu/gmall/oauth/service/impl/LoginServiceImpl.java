package com.atguigu.gmall.oauth.service.impl;

import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Base64;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    /**
     * 测试用户登录
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public AuthToken login(String username, String password) {
        //参数校验
        if (StringUtils.isEmpty(username)
                || StringUtils.isEmpty(password)) {
            throw new RuntimeException("参数或密码不能为空!");
        }
        //发送post请求
        ServiceInstance serviceInstance = loadBalancerClient.choose("service-oauth");
        String uri = serviceInstance.getUri().toString();
        String url = uri + "/oauth/token";
        MultiValueMap<String, String> headers = new HttpHeaders();
        String headersParam = getHeadersParam();
        headers.set("Authorization",headersParam);
        MultiValueMap<String, String> body = new HttpHeaders();
        body.set("username",username);
        body.set("password",password);
        body.set("grant_type","password");
        HttpEntity httpEntity = new HttpEntity(body,headers);
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        //返回结果
        Map<String,String> map = exchange.getBody();
        AuthToken authToken = new AuthToken();
        String access_token = map.get("access_token");
        authToken.setAccessToken(access_token);
        String refresh_token = map.get("refresh_token");
        authToken.setRefreshToken(refresh_token);
        String jti = map.get("jti");
        authToken.setJti(jti);
        return authToken;
    }

    private String getHeadersParam(){
       String result = "Basic ";
       //拼接
        String a = clientId + ":" + clientSecret;
        //base64加密
        byte[] encode = Base64.getEncoder().encode(a.getBytes());
        return result + new String(encode);
    }
}
