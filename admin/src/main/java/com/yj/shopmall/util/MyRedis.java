package com.yj.shopmall.util;

import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.yj.shopmall.constant.Constant.REDIS_SESSION_Prefix;
import static com.yj.shopmall.constant.Constant.USER_MSG;

@Component
public class MyRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;//直接@Autowired注入静态会报空指针 改用setter注入

    //设置redis上的用户信息
    public void setUserToRedis(String userId, User user) {
        stringRedisTemplate.opsForValue().set(key(userId), JsonUtils.objectToJson(user), 30, TimeUnit.MINUTES);
        System.out.println("设置用户信息到redis");
    }

    //更新redis的时间
    public void updateTimeUserToRedis(String userId) {
        stringRedisTemplate.expire(key(userId), 30, TimeUnit.MINUTES);
    }

    //获取redis上的用户信息
    public User getUserFromRedis(String userId) {
        String s = stringRedisTemplate.opsForValue().get(key(userId));
        return JsonUtils.jsonToPojo(s, User.class);
    }

    //删除redis上的 用户信息
    public void delUserFromRedis(String userId) {
        stringRedisTemplate.delete(key(userId));
    }

    //判断是否存在key
    public boolean isHaveUserFromRedis(String userId) {
        Boolean aBoolean = stringRedisTemplate.hasKey(key(userId));
        return aBoolean;
    }


    //判断 Session
    public boolean isHaveSessionKey(String userId){
        return stringRedisTemplate.hasKey(sessionKey(userId));
    }
    //删除Session
    public void delSessionFromRedis(String userId){
        stringRedisTemplate.delete(sessionKey(userId));
    }



    private static String key(String userId){
        return (USER_MSG + "[" + userId + "]");
    }

    private static String sessionKey(String userId){
        return (REDIS_SESSION_Prefix +userId);
    }
}
