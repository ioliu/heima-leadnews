package com.heima.article.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan({"com.heima.common.mysql.core","com.heima.common.common.init","com.heima.common.quartz","com.heima.common.kafka","com.heima.common.kafkastream"})
@EnableScheduling
public class InitConfig {
}
