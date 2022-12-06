package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Resource
    private ItemService itemService;

    /**
     * 查看商品详情页的全部信息
     * @param skuId
     * @return
     */
    @GetMapping("/getItemInfo/{skuId}")
    public Map<String,Object> getItemInfo(@PathVariable("skuId") Long skuId){
        return itemService.getItemInfo(skuId);
    }
}
