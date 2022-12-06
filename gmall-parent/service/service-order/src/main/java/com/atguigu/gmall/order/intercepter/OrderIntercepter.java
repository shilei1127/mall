package com.atguigu.gmall.order.intercepter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 订单微服务的拦截器
 */
@Component
public class OrderIntercepter implements RequestInterceptor {

    /**
     * feign的拦截器方法
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //将主线程的请求对象获取
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        //获取主线程的request对象
        HttpServletRequest request = servletRequestAttributes.getRequest();
        //获取请求头中所有的参数
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            //获取每个请求头参数的名字
            String name = headerNames.nextElement();
            //获取值
            String value = request.getHeader(name);
            //存储
            requestTemplate.header(name, value);
        }
    }
}
