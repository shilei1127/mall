package com.atguigu.gmall.list.service;

import java.util.Map;

/**
 * 商品搜索的接口类
 */
public interface SearchService {
    /**
     * 商品搜索
     *
     * @param searchData
     * @return
     */
    Map<String, Object> search(Map<String,String> searchData);
}
