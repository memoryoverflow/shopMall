package com.yj.shopmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yj.shopmall.mapper")
public class ShopMallStockServerceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShopMallStockServerceApplication.class, args);
	}
}
