package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface BaseAttrInfoService {
    /**
     * 主键查询
     * @param id
     * @return
     */
    BaseAttrInfo getBaseAttrInfo(Long id);

    /**
     * 查询全部
     * @return
     */
    List<BaseAttrInfo> findAll();

    /**
     * 新增
     * @param baseAttrInfo
     */
    void add(BaseAttrInfo baseAttrInfo);

    /**
     * 修改
     * @param baseAttrInfo
     */
    void update(BaseAttrInfo baseAttrInfo);

    /**
     * 删除
     * @param id
     */
    void delete(Long id);

    /**
     * 条件查询
     * @param baseAttrInfo
     * @return
     */
    List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo);

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    IPage Page(Integer page, Integer size);

    /**
     * 分页条件查询
     *
     * @param page
     * @param size
     * @param baseAttrInfo
     * @return
     */
    IPage search(Integer page, Integer size, BaseAttrInfo baseAttrInfo);
}
