package com.yj.shopmall.MQConfig;

import com.yj.shopmall.pojo.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisCacheConf {
    @Bean
    public RedisTemplate<String,Product> myRedisTemplate(
            RedisConnectionFactory redisConnectionFactory
    )throws Exception{
        RedisTemplate<String,Product> template = new RedisTemplate<String,Product>();
        template.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer<Product> JRS = new Jackson2JsonRedisSerializer<Product>(Product.class);

        template.setDefaultSerializer(JRS);
        return template;
    }

    //自定义多个 cacheManager 需指定默认的
    @Primary
    @Bean                                      //将RedisTemplate注入
    public RedisCacheManager myCacheManager(RedisTemplate<String,Product> myRedisTemplate){
        RedisCacheManager cacheManager = new RedisCacheManager(myRedisTemplate);
        //将 key 多加一个前缀 cacheName
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }
}
