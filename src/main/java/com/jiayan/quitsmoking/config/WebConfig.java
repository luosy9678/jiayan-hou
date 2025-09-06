package com.jiayan.quitsmoking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/*.html")
                .addResourceLocations("classpath:/static/");
        
        registry.addResourceHandler("/uploads/backgrounds/**")
                .addResourceLocations("file:uploads/backgrounds/");
        
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:uploads/avatars/");
        
        registry.addResourceHandler("/uploads/audios/**")
                .addResourceLocations("file:uploads/audios/");
        
        registry.addResourceHandler("/uploads/diaries/**")
                .addResourceLocations("file:uploads/diaries/");
        
        registry.addResourceHandler("/uploads/articles/**")
                .addResourceLocations("file:uploads/articles/");
        
        registry.addResourceHandler("/uploads/category-icons/**")
                .addResourceLocations("file:uploads/category-icons/");
    }
}