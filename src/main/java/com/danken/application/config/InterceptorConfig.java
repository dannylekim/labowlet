package com.danken.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.danken.interceptors.*;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private RoomExistenceInterceptor roomExistenceInterceptor;
    private HostAuthInterceptor hostAuthInterceptor;


    public InterceptorConfig(RoomExistenceInterceptor roomExistenceInterceptor, HostAuthInterceptor hostAuthInterceptor) {
        this.roomExistenceInterceptor = roomExistenceInterceptor;
        this.hostAuthInterceptor = hostAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(roomExistenceInterceptor)
                .addPathPatterns("/host/rooms")
                .addPathPatterns("/teams")
                .addPathPatterns("teams/*");

        registry.addInterceptor(hostAuthInterceptor).addPathPatterns("/host/rooms");


    }
}