package com.mby.wiki.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//用来出来跨域问题
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {//CorsRegistry实现跨域资源共享登记
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedHeaders(CorsConfiguration.ALL)
                .allowedMethods(CorsConfiguration.ALL)//允许使用get post put等方式请求
                .allowCredentials(true)//允许前端携带cookie信息
                .maxAge(3600); // 1小时内不需要再预检（发OPTIONS请求）前端会先发送一个OPTIONS请求，检查一遍这个接口是否存在
    }

}
