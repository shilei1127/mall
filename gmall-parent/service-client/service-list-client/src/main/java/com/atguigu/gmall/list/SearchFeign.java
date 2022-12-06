package com.atguigu.gmall.list;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "service-list",path = "/api/search",contextId = "searchFeign") //contextId指定容器的名字
public interface SearchFeign {
    /**
     * 商品搜索
     *
     * @param searchData
     * @return
     */
    @GetMapping
    public Map<String, Object> search(@RequestParam Map<String,String> searchData);
}
