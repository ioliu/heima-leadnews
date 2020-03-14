package com.heima.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan({"com.heima.common.mysql.core","com.heima.common.common.init","com.heima.common.quartz"})
@MapperScan("com.heima.admin.dao")
@EnableScheduling
public class InitConfig {
}
