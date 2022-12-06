package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestRedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TestRedisServiceImpl implements TestRedisService {

    @Resource
    private RedisTemplate redisTemplate;
    /**
     * 对redis进行操作
     */
    //@Override
    public synchronized void setRedis1() {
        //从redis中获取某个key的值
        Integer i = (Integer) redisTemplate.opsForValue().get("java");
        //判空
        if (i != null){
            i++;
            redisTemplate.opsForValue().set("java",i);
        }
    }
    @Override
    public void setRedis(){
        String uuId = UUID.randomUUID().toString().replace("-", "");
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent("lock", "123456",3, TimeUnit.SECONDS);
        if (isLock) {
            //从redis中获取某个值
            Integer i = (Integer) redisTemplate.opsForValue().get("java");
            if (i != null) {
                i++;
                redisTemplate.opsForValue().set("java", i);
            }
            //初始化脚本对象
            DefaultRedisScript redisScript = new DefaultRedisScript();
            //设置lua脚本
            redisScript.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
            //设置返回类型---数值类型
            redisScript.setResultType(Long.class);
            //通过执行脚本释放锁
            redisTemplate.execute(redisScript, Arrays.asList("lock"),uuId);
        }else {
            try {
                Thread.sleep(100);
                //失败重试
                setRedis();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
