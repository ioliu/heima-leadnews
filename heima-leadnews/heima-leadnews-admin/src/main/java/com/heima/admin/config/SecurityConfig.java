package com.heima.admin.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ServletComponentScan("com.heima.common.web.admin.security")
public class SecurityConfig {
}
