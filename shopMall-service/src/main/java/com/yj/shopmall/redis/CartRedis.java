package com.yj.shopmall.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.yj.shopmall.constant.Constant.SHOP_CART;

@Component
public class CartRedis {
    @Autowired
    StringRedisTemplate redisTemplate;

    public boolean addCartToRedis(String uname,String obj,double v){
        return redisTemplate.opsForZSet().add(getKey(uname),obj,v);
    }


    public double incrementNum (String uname,String obj,double v){
        return redisTemplate.opsForZSet().incrementScore(getKey(uname), obj, v);
    }


    public Long delCartMsg(String name,String obj){
        return redisTemplate.opsForZSet().remove(getKey(name),obj);
    }

    public Long getAllCartHashNum(String name){
        Long aLong = redisTemplate.opsForZSet().zCard(getKey(name));
        return aLong;
    }


    public Set<ZSetOperations.TypedTuple<String>> getAllCartMsg(String name, long start, long end){
        return redisTemplate.opsForZSet().rangeWithScores(getKey(name), start, end);
    }


    public boolean isHsahCartKey(String name){
        return redisTemplate.hasKey(getKey(name));
    }


    private static String getKey(String name){
        return (SHOP_CART+name);
    }


    public void delCartAll(String name) {
        redisTemplate.delete(getKey(name));
    }
}
