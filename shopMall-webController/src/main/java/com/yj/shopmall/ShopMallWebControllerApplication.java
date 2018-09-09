package com.yj.shopmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.n3r.idworker","com.yj.shopmall"})

public class ShopMallWebControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopMallWebControllerApplication.class, args);
	}
}
