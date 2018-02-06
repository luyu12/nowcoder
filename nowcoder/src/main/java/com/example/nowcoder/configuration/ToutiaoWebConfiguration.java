package com.example.nowcoder.configuration;

import com.example.nowcoder.interceptor.LoginRequiredInterceptor;
import com.example.nowcoder.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class ToutiaoWebConfiguration extends WebMvcConfigurerAdapter{
    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(passportInterceptor);
        //有些页面是需要权限（只有login才能访问setting系列的网页）
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/setting*");
        super.addInterceptors(registry);
    }
}
