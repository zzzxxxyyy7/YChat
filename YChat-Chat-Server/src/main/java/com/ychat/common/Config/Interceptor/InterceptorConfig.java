package com.ychat.common.Config.Interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Autowired
    private CollectorInterceptor CollectorInterceptor;

    @Autowired
    private BlackInterceptor blacklistInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(CollectorInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(blacklistInterceptor)
                .addPathPatterns("/**");
    }

}
