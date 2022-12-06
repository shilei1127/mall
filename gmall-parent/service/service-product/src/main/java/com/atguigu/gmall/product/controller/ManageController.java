package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.ProductConstant;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理使用的api控制层
 */
@RestController
@RequestMapping("/admin/product")
public class ManageController {
    @Autowired
    private ManageService manageService;

    /**
     * 查看所有的一级分类
     * @return
     */
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        return Result.ok(manageService.getBaseCategory1());
    }

    /**
     * 根据一级分类id查询所有二级分类
     * @param c1Id
     * @return
     */
    @GetMapping("/getCategory2/{c1Id}")
    public Result getCategory2(@PathVariable("c1Id") Long c1Id){
        return Result.ok(manageService.getBaseCategory2(c1Id));
    }

    /**
     * 根据二级分类id查询所有三级分类
     * @param c2Id
     * @return
     */
    @GetMapping("/getCategory3/{c2Id}")
    public Result getCategory3(@PathVariable("c2Id") Long c2Id){
        return Result.ok(manageService.getBaseCategory3(c2Id));
    }

    /**
     * 新增平台属性表
     * @param baseAttrInfo
     * @return
     */
    @PostMapping ("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveBaseAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 查询平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable Long category1Id,
                               @PathVariable Long category2Id,
                               @PathVariable Long category3Id){
        return Result.ok(manageService.selectBaseAttrInfoByCategoryId(category1Id,category2Id,category3Id));
    }

    /**
     * 查询平台属性值列表
     * @param attrId
     * @return
     */
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId")Long attrId){
        return Result.ok(manageService.getBaseAttrValue(attrId));
    }

    /**
     * 查询品牌
     * @return
     */
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        return Result.ok(manageService.getBaseTrademark());
    }

    /**
     * 查询销售属性
     * @return
     */
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
        return Result.ok(manageService.getBaseSaleAttr());
    }

    /**
     * 保存spu的信息
     * @param spuInfo
     * @return
     */
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    /**
     * 条件分页查询
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    @GetMapping("/{page}/{size}")
    public Result getSpuInfoList(@PathVariable(value = "page") Integer page,
                                 @PathVariable(value = "size") Integer size,
                                 Long category3Id){
        return Result.ok(manageService.getSpuInfoList(page, size, category3Id));
    }

    /**
     * 根据spuId查询销售属性信息
     * @param spuId
     * @return
     */
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable(value = "spuId") Long spuId){
        return Result.ok(manageService.getSpuSaleAttrBySpuId(spuId));
    }

    /**
     * 根据spuId获取图片信息
     * @param spuId
     * @return
     */
    @GetMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable(value = "spuId") Long spuId){
        return Result.ok(manageService.getSpuImageList(spuId));
    }

    /**
     * 保存sku
     * @param skuInfo
     * @return
     */
    @PostMapping(value = "/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    /**
     * 分页查询sku的信息
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{page}/{size}")
    public Result list(@PathVariable("page") Integer page,@PathVariable("size") Integer size){
        return Result.ok(manageService.list(page,size));
    }

    /**
     * 上架
     * @param skuId
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        manageService.upOrDown(skuId, ProductConstant.UP_SALE);
        return Result.ok();
    }

    /**
     * 下架
     * @param skuId
     * @return
     */
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        manageService.upOrDown(skuId,ProductConstant.DOWN_SALE);
        return Result.ok();
    }
}
