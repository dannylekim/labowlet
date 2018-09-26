package com.danken.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.danken.interceptors.*;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new ValidSessionInterceptor())
                .excludePathPatterns("/players");

        registry.addInterceptor(new RoomExistenceInterceptor())
                .addPathPatterns("/host/rooms")
                .addPathPatterns("/teams")
                .addPathPatterns("teams/*");

        registry.addInterceptor(new HostAuthInterceptor()).addPathPatterns("/host/rooms");



    }
}