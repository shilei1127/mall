package com.atguigu.gmall.filter;

import com.atguigu.gmall.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关的全局过滤器
 */
@Component
public class GmallFilter implements GlobalFilter, Ordered {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 自定义的过滤器逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String token = request.getQueryParams().getFirst("token");
        if (StringUtils.isEmpty(token)){
            token = request.getHeaders().getFirst("token");
            if (StringUtils.isEmpty(token)){
                HttpCookie httpCookie = request.getCookies().getFirst("token");
                if (httpCookie != null){
                    token = httpCookie.getValue();
                }
            }
        }
        //判断token是否为空
        if (StringUtils.isEmpty(token)){
            response.setStatusCode(HttpStatus.PAYLOAD_TOO_LARGE);
            //拒绝请求
            return response.setComplete();
        }
        //获取网关的ip地址
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        //从redis中拿到键为ip的token值
        String redisToken = stringRedisTemplate.opsForValue().get(gatwayIpAddress);
        if (StringUtils.isEmpty(redisToken)
                || !token.equals(redisToken)){
            response.setStatusCode(HttpStatus.PAYLOAD_TOO_LARGE);
            //拒绝请求
            return response.setComplete();
        }
        //否则将token以固定的参数key和固定的格式存入请求的请求头中
        request.mutate().header("Authorization","bearer " +token);
        //放行
        return chain.filter(exchange);
    }

    /**
     * 全局过滤器的执行顺序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
