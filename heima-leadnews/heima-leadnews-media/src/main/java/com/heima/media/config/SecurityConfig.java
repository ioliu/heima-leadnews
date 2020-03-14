package com.heima.media.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ServletComponentScan("com.heima.common.web.wm.security")
public class SecurityConfig {
}
