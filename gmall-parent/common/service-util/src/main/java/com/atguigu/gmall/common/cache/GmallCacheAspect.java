package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 定义的切面类
 */
@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 增强方法
     * @param point
     * @return
     */
    @Around("@annotation(com.atguigu.gmall.common.cache.SlGmallCache)") //监听这个注解
    public Object cacheAroundAdvice(ProceedingJoinPoint point){
        //返回结果初始化
        Object result = null;
        try {
            //获取方法的参数
            Object[] args = point.getArgs();
            //获取方法的签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            //获取方法的注解对象
            SlGmallCache gmallCache = signature.getMethod().getAnnotation(SlGmallCache.class);
            // 前缀(就是itemController上的SlGmallCache注解中的前缀)
            String prefix = gmallCache.prefix();
            // 从缓存中获取数据 --key=getSkuInfo:[1]
            String key = prefix+Arrays.asList(args).toString();
            // 获取缓存数据
            result = cacheHit(signature, key);
            if (result!=null){
                // 缓存有数据
                return result;
            }
            // 初始化分布式锁:加锁用的是线程id(此处报异常是加锁的key和商品的key相同，导致放锁时的对象不一致)
            RLock lock = redissonClient.getLock(key + "lock");
            boolean flag = lock.tryLock(100, 100, TimeUnit.SECONDS);
            if (flag){
               try {
                   try {
                       //执行itemController上的方法(查询数据库)
                       result = point.proceed(point.getArgs());
                       // 防止缓存穿透
                       if (null==result){
                           // 并把结果放入缓存
                           Object o = new Object();
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(o),300,TimeUnit.SECONDS);
                           return null;
                       }
                   } catch (Throwable throwable) {
                       throwable.printStackTrace();
                   }
                   // 并把结果放入缓存
                   this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(result),24*60*60,TimeUnit.SECONDS);
                   return result;
               }catch (Exception e){
                   e.printStackTrace();
               }finally {
                   // 释放锁
                   lock.unlock();
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //boolean flag = lock.tryLock(10L, 10L, TimeUnit.SECONDS);
        return result;
    }
    // 获取缓存数据
    private Object cacheHit(MethodSignature signature, String key) {
        // 1. 查询缓存
        String cache = (String)redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(cache)) {
            // 有，则反序列化，直接返回
            Class returnType = signature.getReturnType(); // 获取方法返回类型
            // 不能使用parseArray<cache, T>，因为不知道List<T>中的泛型
            return JSONObject.parseObject(cache, returnType);
        }
        return null;
    }

}
