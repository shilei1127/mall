package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 平台属性相关的接口类的实现类
 */
@Service
public class BaseAttrInfoServiceImpl implements BaseAttrInfoService {
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    /**
     * 主键查询
     *
     * @param id
     * @return
     */
    @Override
    public BaseAttrInfo getBaseAttrInfo(Long id) {
        return baseAttrInfoMapper.selectById(id);
    }

    /**
     * 查询全部
     *
     * @return
     */
    @Override
    public List<BaseAttrInfo> findAll() {
        return baseAttrInfoMapper.selectList(null);
    }

    /**
     * 新增
     *
     * @param baseAttrInfo
     */
    @Override
    public void add(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (baseAttrInfo == null || baseAttrInfo.getAttrName() == null) {
            throw new RuntimeException("参数错误");
        }
        //新增操作
        int insert = baseAttrInfoMapper.insert(baseAttrInfo);
        if (insert <=0){
            throw new RuntimeException("新增数据失败,请重试");
        }
    }

    /**
     * 修改
     *
     * @param baseAttrInfo
     */
    @Override
    public void update(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo == null || baseAttrInfo.getAttrName()==null){
            throw new RuntimeException("参数错误");
        }
        //修改操作
        int update = baseAttrInfoMapper.updateById(baseAttrInfo);
        if (update <0){
            throw new RuntimeException("修改失败，请重试");
        }
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        if (id == null){
            return;
        }
        //删除操作
        int i = baseAttrInfoMapper.deleteById(id);
        if (i <=0){
            throw new RuntimeException("删除失败，请重试");
        }
    }

    /**
     * 条件查询
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (baseAttrInfo ==null){
            //若没有任何条件，则查询全部数据
            baseAttrInfoMapper.selectList(null);
        }
        //声明构造器
        LambdaQueryWrapper<BaseAttrInfo> wrapper = QueryParams(baseAttrInfo);
        //执行查询返回结果
        return baseAttrInfoMapper.selectList(wrapper);
    }


    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage Page(Integer page, Integer size) {
        return baseAttrInfoMapper.selectPage(new Page<>(page,size),null);
    }

    /**
     * 分页条件查询
     *
     * @param page
     * @param size
     * @param baseAttrInfo
     * @return
     */
    @Override
    public IPage search(Integer page, Integer size, BaseAttrInfo baseAttrInfo) {

        LambdaQueryWrapper<BaseAttrInfo> wrapper = QueryParams(baseAttrInfo);
        return baseAttrInfoMapper.selectPage(new Page<>(page,size),wrapper);
    }

    /**
     * 构建查询条件
     * @param baseAttrInfo
     * @return
     */
    private LambdaQueryWrapper<BaseAttrInfo> QueryParams(BaseAttrInfo baseAttrInfo) {
        LambdaQueryWrapper<BaseAttrInfo> wrapper = new LambdaQueryWrapper<>();
        //若id不为空，则设置为查询条件
        if (baseAttrInfo.getId() != null){
            wrapper.eq(BaseAttrInfo::getId, baseAttrInfo.getId());
        }
        //属性名字不为空，设置为查询条件(名字为模糊查询)
        if (!StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            wrapper.like(BaseAttrInfo::getAttrName, baseAttrInfo.getAttrName());
        }
        //若分类id不为空，设置为查询条件
        if (baseAttrInfo.getCategoryId() != null){
            wrapper.eq(BaseAttrInfo::getCategoryId, baseAttrInfo.getCategoryId());
        }
        //若分层等级不为空，设置为查询条件
        if (baseAttrInfo.getCategoryLevel() != null){
            wrapper.eq(BaseAttrInfo::getCategoryLevel, baseAttrInfo.getCategoryLevel());
        }
        return wrapper;
    }
}
