package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 平台属性相关的控制层
 */
@RestController
@RequestMapping("/api/baseAttrInfo")
public class BaseAttrInfoController {
    @Autowired
    private BaseAttrInfoService baseAttrInfoService;

    @GetMapping("/getBaseAttrInfo/{id}")
    public Result getBaseAttrInfo(@PathVariable("id") Long id){
        return Result.ok(baseAttrInfoService.getBaseAttrInfo(id));
    }

    @GetMapping("/findAll")
    public Result findAll(){
        return Result.ok(baseAttrInfoService.findAll());
    }

    @PostMapping
    public Result add(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.add(baseAttrInfo);
        return Result.ok();
    }

    @PutMapping
    public Result update(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.update(baseAttrInfo);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Long id){
        baseAttrInfoService.delete(id);
        return Result.ok();
    }

    /**
     * 条件查询
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/search")
    public Result search(@RequestBody BaseAttrInfo baseAttrInfo){

        return Result.ok(baseAttrInfoService.search(baseAttrInfo));
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result page(@PathVariable("page") Integer page,
                       @PathVariable("size") Integer size){
        return Result.ok(baseAttrInfoService.Page(page,size));
    }

    /**
     * 条件分页查询
     * @param page
     * @param size
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result search(@PathVariable("page") Integer page,
                         @PathVariable("size") Integer size,
                         @RequestBody BaseAttrInfo baseAttrInfo){
        return Result.ok(baseAttrInfoService.search(page,size,baseAttrInfo));
    }
}