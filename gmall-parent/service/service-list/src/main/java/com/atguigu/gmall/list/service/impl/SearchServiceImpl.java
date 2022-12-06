package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品搜索接口的实现类
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    public static final Integer size = 50;  //分页
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 商品搜索
     *
     * @param searchData
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchData) {
        try {
            //条件拼接
            SearchRequest searchRequest = buildQueryParams(searchData);
            //执行搜索
            SearchResponse searchResponse =
                    restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析结果
            return getSearchResult(searchResponse);
        } catch (IOException e) {
            log.error("商品搜索发生异常，异常的内容如下" + e.getMessage());
        }
        return null;
    }

    /**
     * 构建查询条件
     *
     * @param searchData
     * @return
     */
    private SearchRequest buildQueryParams(Map<String, String> searchData) {
        //初始化搜索请求体
        SearchRequest searchRequest = new SearchRequest("goods");
        //初始化条件构造器
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //构建组合查询构造器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //关键字查询
        String keywords = searchData.get("keywords");
        if (!StringUtils.isEmpty(keywords)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", keywords));
        }
        //查询分类
        String category3Id = searchData.get("category3Id");
        if (!StringUtils.isEmpty(category3Id)) {
            boolQueryBuilder.must(QueryBuilders.termQuery("category3Id", category3Id));
        }
        //品牌查询 1:oppo
        String tradeMark = searchData.get("tradeMark");
        if (!StringUtils.isEmpty(tradeMark)) {
            String[] split = tradeMark.split(":");
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId", split[0]));
        }
        //平台属性查询 ：一个或者多个
        searchData.entrySet().stream().forEach(entry -> {
            //获取参数key
            String key = entry.getKey();
            //判断
            if (key.startsWith("attr_")) {
                //获取平台属性的参数值 1:2匹
                String value = entry.getValue();
                //切分为平台属性id和用户选中的值
                String[] split = value.split(":");
                //拼接条件
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("attrs.attrId", split[0]))
                        .must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
                //设置nested对象查询条件
                boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None));
            }
        });
        //价格条件：500-1000元 3000元以上
        String price = searchData.get("price");
        //检验价格不为空
        if (!StringUtils.isEmpty(price)) {
            price = price.replace("元", "").replace("以上", "");
            String[] split = price.split("-");
            //大于等于第一个值
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            //判断是否有第二个值
            if (split.length > 1) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }
        }
        //存储条件
        builder.query(boolQueryBuilder);
        //设置聚合条件：品牌聚合条件(terms是取别名)
        builder
                .aggregation(AggregationBuilders.terms("aggTmId").field("tmId")
                        .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                        .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
                        .size(100));    //设置显示100条
        //设置聚合条件：平台属性聚合条件
        builder.aggregation(
                AggregationBuilders.nested("aggAttrs", "attrs")
                        .subAggregation(
                                AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                        .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                                        .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                                        .size(100) //设置显示100条
                        )
        );
        //设置排序
        String softRule = searchData.get("softRule");
        String softField = searchData.get("softField");
        //检验参数
        if (!StringUtils.isEmpty(softRule) && !StringUtils.isEmpty(softField)) {
            //指定排序
            builder.sort(softField, SortOrder.valueOf(softRule));
        } else {
            //默认
            builder.sort("id", SortOrder.DESC);
        }
        //分页(获取页码)
        String pageNum = searchData.get("PageNum");
        //计算页码
        Integer page = getPage(pageNum);
        //设置每页多少条
        /**
         * 1   0-49
         * 2   50-99
         * 3   100-149
         */
        builder.size(size);
        builder.from((page-1)*size);
        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font style=color:red>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.field("title");
        builder.highlighter(highlightBuilder);
        //设置条件
        searchRequest.source(builder);
        return searchRequest;
    }

    /**
     * 计算页码
     * @param pageNum
     * @return
     */
    private Integer getPage(String pageNum) {
        try {
            int i = Integer.parseInt(pageNum);
            return i > 0 ? i : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * 解析查询结果
     *
     * @param searchResponse
     * @return
     */
    private Map<String, Object> getSearchResult(SearchResponse searchResponse) {
        //返回结果初始化
        Map<String, Object> result = new HashMap<>();
        //商品列表初始化
        List<Goods> goodsList = new ArrayList<>();
        //拿到命中的数据
        SearchHits searchHits = searchResponse.getHits();
        //一共有多少条数据符合条件
        long totalHits = searchHits.totalHits;
        result.put("totalHits",totalHits);
        Iterator<SearchHit> iterator = searchHits.iterator();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            //获取json类型的字符串
            String sourceAsString = next.getSourceAsString();
            //反序列，将拿到的string类型的字符串，转成对象
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);
            //获取高亮的数据
            HighlightField highlightField = next.getHighlightFields().get("title");
            if (highlightField != null){
                //拿到所有高亮数据
                Text[] fragments = highlightField.getFragments();
                if(fragments != null && fragments.length >0){
                    String title = "";
                    for (Text fragment : fragments) {
                        title += fragment;
                    }
                    //替换
                    goods.setTitle(title);
                }
            }
            //保存
            goodsList.add(goods);
        }
        //保存商品列表
        result.put("goodsList", goodsList);
        // 全部聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        // 解析品牌的聚合结果
        List<SearchResponseTmVo> tmAggResultList = getTmAggResult(aggregations);
        // 保存品牌列表
        result.put("tmAggResultList", tmAggResultList);
        //解析平台属性的聚合结果
        List<SearchResponseAttrVo> attrInfoAggResult = getAttrInfoAggResult(aggregations);
        //保存平台属性的聚合结果
        result.put("attrInfoAggResult", attrInfoAggResult);
        return result;
    }

    private List<SearchResponseAttrVo> getAttrInfoAggResult(Aggregations aggregations) {
        //获取nested类型的聚合结果
        ParsedNested aggAttrs = aggregations.get("aggAttrs");
        //获取子聚合的结果
        ParsedLongTerms aggAttrId = aggAttrs.getAggregations().get("aggAttrId");
        return aggAttrId.getBuckets().stream().map(bucket -> {
            //初始化结果
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            long attrId = ((Terms.Bucket) bucket).getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //获取平台属性名
            ParsedStringTerms aggAttrName = ((Terms.Bucket) bucket).getAggregations().get("aggAttrName");
            List<? extends Terms.Bucket> aggAttrNameBuckets = aggAttrName.getBuckets();
            if (aggAttrNameBuckets != null && aggAttrNameBuckets.size() > 0) {
                String attrName = aggAttrNameBuckets.get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrName);
            }
            //获取平台属性值
            ParsedStringTerms aggAttrValue = ((Terms.Bucket) bucket).getAggregations().get("aggAttrValue");
            List<? extends Terms.Bucket> aggAttrValueBuckets = aggAttrValue.getBuckets();
            if (aggAttrValueBuckets != null && aggAttrValueBuckets.size() > 0) {
                List<String> attrValueList = aggAttrValueBuckets.stream().map(aggAttrValueBucket -> {
                    return ((Terms.Bucket) aggAttrValueBucket).getKeyAsString();
                }).collect(Collectors.toList());
                searchResponseAttrVo.setAttrValueList(attrValueList);
            }
            return searchResponseAttrVo;
        }).collect(Collectors.toList());
    }

    /**
     * 解析品牌的聚合结果
     *
     * @param aggregations
     * @return
     */
    private List<SearchResponseTmVo> getTmAggResult(Aggregations aggregations) {
        //通过别名获取品牌id的聚合结果: tmId=1
        ParsedLongTerms aggTmId = aggregations.get("aggTmId");
        //遍历获取每个品牌的id和子聚合的结果
        return aggTmId.getBuckets().stream().map(bucket -> {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取品牌的id
            long tmId = ((Terms.Bucket) bucket).getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            //获取子聚合的结果: apple 苹果
            ParsedStringTerms aggTmName =
                    ((Terms.Bucket) bucket).getAggregations().get("aggTmName");
            //确认品牌的名字不为空获取
            List<? extends Terms.Bucket> tmNamebuckets = aggTmName.getBuckets();
            if (tmNamebuckets != null && tmNamebuckets.size() > 0) {
                //获取品牌的名字
                String tmName = tmNamebuckets.get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            ParsedStringTerms aggTmLogoUrl =
                    ((Terms.Bucket) bucket).getAggregations().get("aggTmLogoUrl");
            //确认logo不为空
            List<? extends Terms.Bucket> logoUrlBuckets = aggTmLogoUrl.getBuckets();
            if (logoUrlBuckets != null && logoUrlBuckets.size() > 0) {
                //获取品牌的logourl
                String tmLogoUrl = logoUrlBuckets.get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            //返回
            return searchResponseTmVo;
        }).collect(Collectors.toList());
    }
}
