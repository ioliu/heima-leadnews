package com.heima.behavior.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.heima.common.common.init","com.heima.common.kafka"})
public class InitConfig {
}
