package com.danken.application.config;

import com.danken.filters.ExpireSessionsFilter;
import com.danken.filters.XAuthTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/***
 * This class will include all the filter beans and all of their setups, paths and behaviours
 *
 */
@Component
public class FilterConfig {


    //Expire Sessions Filter to all paths
    @Bean
    public FilterRegistrationBean<ExpireSessionsFilter> expireSessionsFilter(){
        FilterRegistrationBean<ExpireSessionsFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ExpireSessionsFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<XAuthTokenFilter> xAuthTokenFilter(){
        FilterRegistrationBean<XAuthTokenFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new XAuthTokenFilter());
        registrationBean.addUrlPatterns("/rooms/*", "/rooms", "/teams", "/teams/*", "/host/*");
        return registrationBean;
    }

}
