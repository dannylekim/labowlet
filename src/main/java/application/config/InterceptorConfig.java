package application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import interceptors.*;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new HostAuthInterceptor()).addPathPatterns("/host/rooms");
        registry.addInterceptor(new RoomExistenceInterceptor())
        .addPathPatterns("/rooms")
        .addPathPatterns("/host/rooms")
        .addPathPatterns("/rooms/*")
        .addPathPatterns("/teams")
        .addPathPatterns("teams/*"); 
    }
}