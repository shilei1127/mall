package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页使用的接口实现类
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;

    /**
     * 获取首页的分类信息
     *
     * @return
     */
    @Override
    public List<JSONObject> getIndexCategory() {
        //查询出所有的一级二级三级分类的信息:包含全部的信息
        List<BaseCategoryView> baseCategoryView1List = baseCategoryViewMapper.selectList(null);
        //基于全部的分类信息，以一级分类为单位进行分组
        Map<Long, List<BaseCategoryView>> category1Map =
                baseCategoryView1List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //遍历这个map获取每个一级分类的id和名字以及对应的二三级分类的信息
        return category1Map.entrySet().stream().map(category1Entry -> {
            JSONObject category1Json = new JSONObject();
            //获取一级分类的id
            Long category1Id = category1Entry.getKey();
            category1Json.put("category1Id", category1Id);
            //获取每个一级分类对应的全部二级和三级,一级分类都一样，二级分类重复
            List<BaseCategoryView> categoryView2List = category1Entry.getValue();
            //获取一级分类的名字
            String category1Name = categoryView2List.get(0).getCategory1Name();
            category1Json.put("category1Name", category1Name);
            //再根据二级分类分桶/分组
            Map<Long, List<BaseCategoryView>> category2Map =
                    categoryView2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            //遍历category2Map，获取每个二级分类对应的三级分类信息
            List<JSONObject> category2JsonList = category2Map.entrySet().stream().map(category2Entry -> {
                //二级分类对象的初始化
                JSONObject category2Json = new JSONObject();
                //获取每个二级分类的id
                Long category2Id = category2Entry.getKey();
                category2Json.put("category2Id", category2Id);
                //获取每个二级对应的全部三级分类列表：每个三级分类都不重复，一级二级都一样
                List<BaseCategoryView> categoryView3List = category2Entry.getValue();
                //获取二级分类的名字
                String category2Name = categoryView3List.get(0).getCategory2Name();
                category2Json.put("category2Name", category2Name);
                //获取每个三级分类信息
                List<JSONObject> category3JsonList = categoryView3List.stream().map(baseCategoryView -> { //map有返回值而foreach没有返回值
                    //三级分类对象的初始化
                    JSONObject category3Json = new JSONObject();
                    //获取三级分类的id
                    Long category3Id = baseCategoryView.getCategory3Id();
                    category3Json.put("category3Id", category3Id);
                    //获取三级分类的名字
                    String category3Name = baseCategoryView.getCategory3Name();
                    category3Json.put("category3Name", category3Name);
                    //返回这个包装好的三级分类
                    return category3Json;
                }).collect(Collectors.toList());
                //保存这个二级分类和对应三级分类的关系
                category2Json.put("childCategory", category3JsonList);
                //返回
                return category2Json;
            }).collect(Collectors.toList());
            //保存这个一级分类和对应二级分类的关系
            category1Json.put("childCategory",category2JsonList);
            return category1Json;
        }).collect(Collectors.toList());
    }
}
