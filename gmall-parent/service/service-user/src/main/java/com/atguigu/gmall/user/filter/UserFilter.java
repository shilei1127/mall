package com.atguigu.gmall.user.filter;

import com.atguigu.gmall.user.util.TokenUtil;
import com.atguigu.gmall.user.util.UserThreadLocalUtil;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 用户微服务的过滤器：解析令牌中的用户名进行存储
 */

@Order(1)//过滤器的执行顺序
@WebFilter(filterName = "userFilter", urlPatterns = "/*")
public class UserFilter extends GenericFilterBean {

    /**
     * 自定义的过滤器逻辑
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        //获取请求头中的令牌: jwt
        String token =
                request.getHeader("Authorization").replace("bearer ", "");
        //使用工具类解析
        Map<String, String> map = TokenUtil.dcodeToken(token);
        if(map != null){
            //获取用户名
            String username = map.get("username");
            //存储用户名--本地线程对象
            UserThreadLocalUtil.set(username);
        }
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }
}