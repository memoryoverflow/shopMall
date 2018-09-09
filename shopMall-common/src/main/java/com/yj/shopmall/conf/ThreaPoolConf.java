package com.yj.shopmall.conf;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
public class ThreaPoolConf {
    private static final int CorePoolSize =5;
    private static final int MaximumPoolSize =10;
    private static final long keepAliveTime =60;


    @Bean
    public ThreadPoolExecutor threadPoolExecutor(@Qualifier("Handler") RejectedExecutionHandler rejectedExecutionHandler){
        ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(
                CorePoolSize,
                MaximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50),
                rejectedExecutionHandler
        );

        return threadPoolExecutor;
    }

    @Bean("Handler")
    public RejectedExecutionHandler rejectedExecutionHandler(){
        return new ThreadPoolExecutor.AbortPolicy();
    }
}
