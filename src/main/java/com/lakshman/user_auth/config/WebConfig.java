package com.lakshman.user_auth.config;

import com.lakshman.user_auth.interceptor.RequestAuditInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final RequestAuditInterceptor requestAuditInterceptor;
    
    public WebConfig(RequestAuditInterceptor requestAuditInterceptor) {
        this.requestAuditInterceptor = requestAuditInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestAuditInterceptor)
                .addPathPatterns("/**");
    }
}
