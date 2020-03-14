package com.heima.images.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.heima.common.kafka","com.heima.common.kafkastream"})
public class KafkaConfig {
}
