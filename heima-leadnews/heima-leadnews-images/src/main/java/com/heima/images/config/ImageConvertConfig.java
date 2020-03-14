package com.heima.images.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;

@Configuration
public class ImageConvertConfig {

    /**
     * 返回图片转换器
     * @return
     */
    @Bean
    public BufferedImageHttpMessageConverter converter(){
        return new BufferedImageHttpMessageConverter();
    }

}
