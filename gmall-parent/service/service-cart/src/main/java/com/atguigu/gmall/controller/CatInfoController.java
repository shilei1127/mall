package com.atguigu.gmall.controller;

import com.atguigu.gmall.common.cache.SlGmallCache;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.service.CartInfoService;
import com.atguigu.gmall.util.CartThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车相关的控制层
 */
@RestController
@RequestMapping("/api/cart")
public class CatInfoController {
    public static final short CHECKED = 1;
    public static final short UN_CHECKED = 0;
    @Autowired
    private CartInfoService cartInfoService;

    /**
     * 添加购物车
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/addCart")
    public Result addCart(Long skuId,Integer num){
        cartInfoService.addCart(skuId,num);
        return Result.ok();
    }

    /**
     * 查询购物车的数据
     * @return
     */
    @GetMapping("/getCartInfo")
    @SlGmallCache(prefix = "cartInfo:")
    public Result getCartInfo(){
        return Result.ok(cartInfoService.getCartInfo());
    }

    /**
     * 删除购物车
     * @param id
     * @return
     */
    @DeleteMapping("/removeCart")
    public Result removeCart(Long id){

        cartInfoService.removeCart(id);
        return Result.ok();
    }

    /**
     * 选中
     * @param id
     * @return
     */
    @GetMapping("/check")
    public Result check(Long id){

        cartInfoService.checkOrChecked(CHECKED,id);
        return Result.ok();
    }

    /**
     * 取消选中
     * @param id
     * @return
     */
    @GetMapping("/uncheck")
    public Result UnCheck(Long id){

        cartInfoService.checkOrChecked(UN_CHECKED,id);
        return Result.ok();
    }

    /**
     * 合并购物车
     * @param cartInfoList
     * @return
     */
    @PostMapping("/mergeCart")
    public Result mergeCart(@RequestBody List<CartInfo> cartInfoList){

        cartInfoService.mergeCart(cartInfoList);
        return Result.ok();
    }

    /**
     * 查询指定用户选中的购物车数据
     * @return
     */
    @GetMapping(value = "/getCheckCart")
    public Result getCheckCart(){
        return Result.ok(cartInfoService.getCheckCart());
    }

    /**
     * 测试本地线程
     * @return
     */
    @GetMapping("/test")
    public String getUsername(){
        return CartThreadLocalUtil.get();
    }
}
