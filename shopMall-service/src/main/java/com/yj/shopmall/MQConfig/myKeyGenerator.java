package com.yj.shopmall.MQConfig;/*
package com.yj.shopmall.MQConfig;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;

@Configuration
public class myKeyGenerator {
    @Bean("myKeyGenerator")
    public KeyGenerator keyGenerator(){
        new KeyGenerator(){
            public Object generate(Object o, Method method, Object... params) {
                return method.getName()+"-["+ Arrays.asList(params).toString()+"]";
            }
        };
        return  null;
    }
}
*/
