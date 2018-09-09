package com.yj.shopmall.redis;

import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.controller.OrderController;
import com.yj.shopmall.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.yj.shopmall.constant.Constant.USER_MSG;

@Component
public class MyRedis {
    private Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private StringRedisTemplate stringRedisTemplate;//直接@Autowired注入静态会报空指针 改用setter注入

    //设置redis上的用户信息
    public void setUserToRedis(String userId, User user) {
        stringRedisTemplate.opsForValue().set(USER_MSG + "[" + userId + "]", JsonUtils.objectToJson(user), 30, TimeUnit.MINUTES);
        logger.info("登录成功，设置用户信息到redis");
    }

    //更新redis的时间
    public void updateTimeUserToRedis(String userId) {
        stringRedisTemplate.expire(USER_MSG + "[" + userId + "]", 30, TimeUnit.MINUTES);
        logger.info("更新redis用户信息时间");
    }

    //获取redis上的用户信息
    public User getUserFromRedis(String userId) {
        String s = stringRedisTemplate.opsForValue().get(USER_MSG + "[" + userId + "]");
        logger.info("获取redis上用户信息=[{}]",s);
        return JsonUtils.jsonToPojo(s, User.class);
    }

    //删除redis上的 用户信息
    public void delUserFromRedis(String userId) {
        stringRedisTemplate.delete(USER_MSG + "[" + userId + "]");
        logger.info("删除redis上用户信息=[{}]",userId);
    }

    //判断是否存在key
    public boolean isHaveUserFromRedis(String userId) {
        Boolean aBoolean = stringRedisTemplate.hasKey(USER_MSG + "[" + userId + "]");
        return aBoolean;
    }



    //判断登录session是否存在
    public boolean isHaveKey(String userId){
        return stringRedisTemplate.hasKey(userId);
    }

    public void delRedisKey(String key){
        stringRedisTemplate.delete(key);
    }
}
