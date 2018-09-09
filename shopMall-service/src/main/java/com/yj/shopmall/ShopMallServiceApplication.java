package com.yj.shopmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//开启Cache缓存
@EnableCaching
@MapperScan("com.yj.shopmall.mapper")
@ComponentScan(basePackages = {"org.n3r.idworker","com.yj.shopmall"})
@EnableScheduling //开启基于定时任务的注解功能
public class ShopMallServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopMallServiceApplication.class, args);
	}
}
