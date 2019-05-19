package com.lygzbkj.elemonitor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan(basePackages="com.lygzbkj.elemonitor.mapper")
public class EleMonitor2Application {
	
	public static void main(String[] args) {
		SpringApplication.run(EleMonitor2Application.class, args);
	}
}
