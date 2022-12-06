package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.list.SearchFeign;
import com.atguigu.gmall.web.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 商品搜索控制层
 */
@Controller
@RequestMapping("/page/search")
public class SearchController {
    @Autowired
    private SearchFeign searchFeign;

    @Value("${page.url}")
    private String pageUrl; //商品详情页的地址

    @GetMapping
    public String list(@RequestParam Map<String, String> searchData, Model model) {
        //远程调用搜索的微服务获取商品数据
        Map<String, Object> map = searchFeign.search(searchData);
        model.addAllAttributes(map);
        model.addAttribute("searchData", searchData);
        //拼接当前的url
        String url = getUrl(searchData);
        model.addAttribute("url", url);

        //获取排序的url
        String softUrl = getSoftUrl(searchData);
        model.addAttribute("softUrl", softUrl);
        //获取真实的页码
        Integer pageNum = getPage(searchData.get("pageNum"));
        //总符合条件的数据量
        Long totalHits = Long.parseLong(map.get("totalHits").toString());
        //每页显示的条数
        Integer size=50;
        //分页对象初始化
        Page pageInfo = new Page<>(totalHits,pageNum,size);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("pageUrl",pageUrl);
        return "list";
    }

    /**
     * 计算页码
     *
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
     * 获取排序的url
     *
     * @param searchData
     * @return
     */
    private String getSoftUrl(Map<String, String> searchData) {
        //url地址初始化
        StringBuffer url = new StringBuffer("/page/search?");
        //遍历获取要拼接的参数
        searchData.entrySet().stream().forEach(entry -> {
            String key = entry.getKey();
            if (!key.equals("softRule") && !key.equals("softField") && !key.equals("pageNum")) {
                String value = entry.getValue();
                //拼接
                url.append(key + "=" + value + "&");
            }
        });
        //返回结果
        return url.toString().substring(0, url.toString().length() - 1);
    }

    /**
     * 获取地址栏的地址
     *
     * @param searchData
     * @return
     */
    private String getUrl(Map<String, String> searchData) {
        //url地址初始化
        StringBuffer url = new StringBuffer("/page/search?");
        //遍历获取要拼接的参数
        searchData.entrySet().stream().forEach(entry -> {
            String key = entry.getKey();
            if (!key.equals("pageNum")){
                String value = entry.getValue();
                //拼接
                url.append(key + "=" + value + "&");
            }
        });
        //返回结果
        return url.toString().substring(0, url.toString().length() - 1);
    }
}
