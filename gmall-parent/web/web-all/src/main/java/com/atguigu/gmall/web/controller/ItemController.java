package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.item.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 商品详情页的前端页面控制层
 */
@Controller
@RequestMapping("/page/item")
public class ItemController {
    @Autowired
    private ItemFeign itemFeign;
    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 打开商品详情页
     * @param skuId
     * @return
     */
    @GetMapping
    public String item(Long skuId, Model model){
        //远程调用微服务商品详情，查询商品详情的6块数据
        Map<String, Object> result = itemFeign.getItemInfo(skuId);
        //存入model
        model.addAllAttributes(result);
        //打开页面
        return "item";
    }

    @GetMapping("/createSkuHtml")
    @ResponseBody
    public String createSkuHtml(Long skuId) throws Exception{
        Map<String, Object> result = itemFeign.getItemInfo(skuId);
        if (result == null)
            return "静态页面生成失败，商品不存在";
        //初始化数据容器
        Context context = new Context();
        context.setVariables(result);
        //初始化数据流
        PrintWriter writer = new PrintWriter(
                new File("F:\\SGG_JAVA",skuId+".html"),
                "UTF-8");
        /**
         *生成静态页面
         * 1.使用哪个页面作为模板
         * 2.使用哪个容器装
         * 3.保存到哪里去
         */
        templateEngine.process("itemModel",context,writer);
        //关闭输出流
        writer.flush();
        writer.close();
        return skuId+"的静态页面生成成功!";
    }
}
