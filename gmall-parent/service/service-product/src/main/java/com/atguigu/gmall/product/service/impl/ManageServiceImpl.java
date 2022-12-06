package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.ProductConstant;
import com.atguigu.gmall.list.GoodsFeign;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
@Service
@Transactional(rollbackFor = Exception.class)
public class ManageServiceImpl implements ManageService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;
    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;
    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    private BaseAttrInfoValueMapper baseAttrInfoValueMapper;
    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;
    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Resource
    private SpuImageMapper spuImageMapper;
    @Resource
    private SpuInfoMapper spuInfoMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Resource
    private GoodsFeign goodsFeign;
    /**
     * 查询所有一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getBaseCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 根据一级id查询所有二级分类的信息
     *
     * @return
     */
    @Override
    public List<BaseCategory2> getBaseCategory2(Long c1Id) {
        return baseCategory2Mapper.selectList(
                new LambdaQueryWrapper<BaseCategory2>()
                        .eq(BaseCategory2::getCategory1Id,c1Id));
    }

    /**
     * 根据二级分类id查询三级分类的信息
     *
     * @param c2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getBaseCategory3(Long c2Id) {
        return baseCategory3Mapper.selectList(
                new LambdaQueryWrapper<BaseCategory3>()
                        .eq(BaseCategory3::getCategory2Id,c2Id));
    }

    /**
     * 新增平台属性值表
     *
     * @param baseAttrInfo
     */
    @Override
    public void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo) {
        //校验参数
        if (baseAttrInfo ==null || StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            throw new RuntimeException("新增参数有误");
        }
        //判断新增还是修改
        if (baseAttrInfo.getId() == null){
            //新增平台属性名称表
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            //新增成功，就能获取平台属性的id
            if (insert <=0){
                throw new RuntimeException("新增平台属性名称失败，请重试");
            }

        }else {
            //修改平台属性名称表
            int update = baseAttrInfoMapper.updateById(baseAttrInfo);
            if (update < 0){
                throw new RuntimeException("修改平台属性名称失败");
            }
            //将旧的平台属性值全部删除
            int delete = baseAttrInfoValueMapper.delete(
                    new LambdaQueryWrapper<BaseAttrValue>()
                            .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
            if (delete < 0){
                throw new RuntimeException("修改平台属性名称失败");
            }
        }
        Long attrId = baseAttrInfo.getId();
        //将id补充到平台属性每个值的对象中去
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        attrValueList.stream().forEach(baseAttrValue -> {
            //补充平台属性的id
            baseAttrValue.setAttrId(attrId);
            //保存平台属性值的列表
            int insert1 = baseAttrInfoValueMapper.insert(baseAttrValue);
            if(insert1 <= 0){
                throw new RuntimeException("新增平台属性名失败！");
            }
        });
    }

    /**
     * 查询平台属性列表
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> selectBaseAttrInfoByCategoryId(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoByCategoryId(category1Id,category2Id,category3Id);
    }

    /**
     * 查看平台属性值列表
     *
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getBaseAttrValue(Long attrId) {
        return baseAttrInfoValueMapper.selectList(
                new LambdaQueryWrapper<BaseAttrValue>()
                        .eq(BaseAttrValue::getAttrId,attrId));
    }

    /**
     * 查询品牌
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getBaseTrademark() {
        return baseTrademarkMapper.selectList(new LambdaQueryWrapper<BaseTrademark>(null));
    }

    /**
     * 查询销售属性
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttr() {
        return baseSaleAttrMapper.selectList(new LambdaQueryWrapper<BaseSaleAttr>(null));
    }

    /**
     * 新增spu的信息
     *
     * @param spuInfo
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //1.校验参数
        if (spuInfo == null){
            throw new RuntimeException("参数有误,操作失败");
        }
        //2.保存spuinfo的信息，判断是新增还是修改
        if (spuInfo.getId()==null){
            //新增
            int insert = spuInfoMapper.insert(spuInfo);
            if (insert <=0){
                throw new RuntimeException("参数有误，新增失败！");
            }
        }else {
            //修改
            int update = spuInfoMapper.updateById(spuInfo);
            if (update < 0){
                throw new RuntimeException("参数有误，修改失败");
            }
            //附属表需要处理，删除图片表
            int delete = spuImageMapper.delete(
                    new LambdaQueryWrapper<SpuImage>()
                            .eq(SpuImage::getSpuId, spuInfo.getId()));
            //删除销售属性名称表
            int delete1 = spuSaleAttrMapper.delete(
                    new LambdaQueryWrapper<SpuSaleAttr>()
                            .eq(SpuSaleAttr::getSpuId, spuInfo.getId()));
            //删除销售属性值表
            int delete2 = spuSaleAttrValueMapper.delete(
                    new LambdaQueryWrapper<SpuSaleAttrValue>()
                            .eq(SpuSaleAttrValue::getSpuId, spuInfo.getId()));
            if (delete<0 || delete1<0 || delete2<0){
                throw new RuntimeException("参数错误，删除操作失败");
            }
        }

        //3.操作完成以后获取spu的id
        Long spuId = spuInfo.getId();
        //4.保存spu的图片
        saveSpuImage(spuId,spuInfo.getSpuImageList());
        //5.保存spu的销售属性和值的信息
        saveSpuSaleAttr(spuId,spuInfo.getSpuSaleAttrList());
    }

    /**
     * 分页条件查询
     *
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    @Override
    public IPage<SpuInfo> getSpuInfoList(Integer page, Integer size, Long category3Id) {
        return spuInfoMapper.selectPage(
                new Page<>(page,size),
                new LambdaQueryWrapper<SpuInfo>()
                        .eq(SpuInfo::getCategory3Id,category3Id));
    }

    /**
     * 根据spuId查询销售属性的信息
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrBySpuId(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrBySpuId(spuId);
    }

    /**
     * 根据spuId查询图片信息
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        return spuImageMapper.selectList(
                new LambdaQueryWrapper<SpuImage>()
                        .eq(SpuImage::getSpuId,spuId));
    }

    /**
     * 保存sku
     *
     * @param skuInfo
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //1.参数校验
        if (skuInfo == null || StringUtils.isEmpty(skuInfo.getSkuDefaultImg())){
            throw new RuntimeException("参数错误");
        }
        //2.判断新增还是修改
        if (skuInfo.getId() == null){
            //新增
            int insert = skuInfoMapper.insert(skuInfo);
            if (insert <= 0){
                throw new RuntimeException("参数有误，新增失败");
            }
        }else {
            //修改
            int update = skuInfoMapper.updateById(skuInfo);
            if (update <0){
                throw new RuntimeException("参数有误，修改失败");
            }
            //删除sku的图片信息
            int delete = skuImageMapper.delete(
                    new LambdaQueryWrapper<SkuImage>()
                            .eq(SkuImage::getSkuId, skuInfo.getId()));
            //删除sku的销售信息
            int delete1 = skuSaleAttrValueMapper.delete(
                    new LambdaQueryWrapper<SkuSaleAttrValue>()
                            .eq(SkuSaleAttrValue::getSkuId, skuInfo.getId()));
            //删除sku的平台属性
            int delete2 = skuAttrValueMapper.delete(
                    new LambdaQueryWrapper<SkuAttrValue>()
                            .eq(SkuAttrValue::getSkuId, skuInfo.getId()));
            if (delete <0 || delete1 <0 || delete2 < 0){
                throw new RuntimeException("修改sku属性失败");
            }
        }
        //3.获取skuId
        Long skuId = skuInfo.getId();
        //4.保存sku的图片信息
        saveSkuImage(skuId,skuInfo.getSkuImageList());
        //5.保存sku的销售属性信息
        saveSkuSaleAttrValue(skuId,skuInfo.getSkuSaleAttrValueList(),skuInfo.getSpuId());
        //6.保存sku的平台属性信息
        saveSkuAttrValue(skuId,skuInfo.getSkuAttrValueList());
    }

    /**
     * 分页查询sku的信息
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SkuInfo> list(Integer page, Integer size) {
        return skuInfoMapper.selectPage(new Page<>(page,size),null);
    }

    /**
     * 商品的上架或者下架
     *
     * @param skuId
     * @param status
     */
    @Override
    public void upOrDown(Long skuId, Short status) {
        //1.校验参数
        if (skuId == null)
            return;
        //2.查寻sku信息
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo == null || skuInfo.getId() == null)
            return;
        //3.设置状态值
        skuInfo.setIsSale(status);
        //4.执行修改
        int i = skuInfoMapper.updateById(skuInfo);
        if (i <= 0)
            throw new RuntimeException("上架或者下架失败");
        //判断商品是上架还是下架--------------存在数据不一致的情况待优化----TODO---异步处理
        if (status.equals(ProductConstant.UP_SALE)){
            //上架
            goodsFeign.addGoodsFromDbToEs(skuId);
        }else {
            //下架
            goodsFeign.removeGoodsFromEs(skuId);
        }
/*
        方案二
        int update = skuInfoMapper.upOrDown(skuId,status);
        if (update < 0)
            throw new RuntimeException("sku的上下架失败");
*/
    }

    private void saveSkuAttrValue(Long skuId, List<SkuAttrValue> skuAttrValueList) {
        skuAttrValueList.stream().forEach(skuAttrValue -> {
            skuAttrValue.setSkuId(skuId);
            int insert = skuAttrValueMapper.insert(skuAttrValue);
            if (insert <= 0){
                throw new RuntimeException("参数错误，保存sku平台属性失败");
            }
        });
    }

    /**
     * 保存sku的销售属性
     * @param skuId
     * @param skuSaleAttrValueList
     * @param spuId
     */
    private void saveSkuSaleAttrValue(Long skuId, List<SkuSaleAttrValue> skuSaleAttrValueList, Long spuId) {
        skuSaleAttrValueList.stream().forEach(skuSaleAttrValue ->{
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(spuId);
            int insert = skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            if (insert <= 0){
                throw new RuntimeException("参数有误，保存sku的销售属性信息失败");
            }
        });
    }

    private void saveSkuImage(Long skuId, List<SkuImage> skuImageList) {
        skuImageList.stream().forEach(skuImage -> {
            skuImage.setSkuId(skuId);
            int insert = skuImageMapper.insert(skuImage);
            if (insert <= 0){
                throw new RuntimeException("参数有误，sku图片保存失败");
            }
        });
    }

    /**
     * 新增spu销售属性信息
     * @param spuId
     * @param spuSaleAttrList
     */
    private void saveSpuSaleAttr(Long spuId, List<SpuSaleAttr> spuSaleAttrList) {
        spuSaleAttrList.stream().forEach(spuSaleAttr -> {
            spuSaleAttr.setSpuId(spuId);
            int insert = spuSaleAttrMapper.insert(spuSaleAttr);
            if (insert <= 0){
                throw new RuntimeException("新增spu销售属性失败");
            }
            //保存spu销售属性的值
            saveSpuSaleAttrValue(spuId,spuSaleAttr.getSpuSaleAttrValueList(),spuSaleAttr.getSaleAttrName());
        });
    }

    /**
     * 保存spu的销售属性值
     *
     * @param spuId
     * @param spuSaleAttrValueList
     * @param saleAttrName
     */
    private void saveSpuSaleAttrValue(Long spuId, List<SpuSaleAttrValue> spuSaleAttrValueList, String saleAttrName) {
        spuSaleAttrValueList.stream().forEach(spuSaleAttrValue -> {
            spuSaleAttrValue.setSpuId(spuId);
            spuSaleAttrValue.setSaleAttrName(saleAttrName);
            int insert = spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            if (insert <= 0){
                throw new RuntimeException("新增spu销售属性值失败");
            }
        });
    }

    /**
     * 新增spu的图片属性
     * @param spuId
     * @param spuImageList
     */
    private void saveSpuImage(Long spuId, List<SpuImage> spuImageList) {
        spuImageList.stream().forEach(spuImage -> {
            spuImage.setSpuId(spuId);
            int insert = spuImageMapper.insert(spuImage);
            if (insert <= 0){
                throw new RuntimeException("新增spu图片失败");
            }
        });
    }
}
