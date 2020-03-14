package com.heima.migration.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan({"com.heima.common.common.init","com.heima.common.mongo","com.heima.common.mysql.core", "com.heima.common.quartz","com.heima.common.hbase","com.heima.common.kafka"})
@EnableScheduling
public class InitConfig {
}