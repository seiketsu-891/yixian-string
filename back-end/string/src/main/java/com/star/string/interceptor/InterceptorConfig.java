package com.star.string.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private final TokenInterceptor tokenInterceptor;

    //构造方法
    private InterceptorConfig interceptorConfig;

    public InterceptorConfig(TokenInterceptor tokenInterceptor){
        this.tokenInterceptor = tokenInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(tokenInterceptor)
                .excludePathPatterns("/account/login")
                .excludePathPatterns("/account/register")
                .excludePathPatterns("/account/resetpswd-forgot")
                .excludePathPatterns("/account/sms/send")
                .excludePathPatterns("/account/logout");
        WebMvcConfigurer.super.addInterceptors(registry);

    }

}
