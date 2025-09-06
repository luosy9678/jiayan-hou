package com.jiayan.quitsmoking.config;

import org.springframework.context.annotation.Configuration;

/**
 * Swagger API文档配置
 * TODO: 添加Swagger依赖后启用
 */
@Configuration
public class SwaggerConfig {
    
    // TODO: 添加Swagger依赖后启用以下配置
    /*
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("戒烟助手论坛系统 API")
                        .description("戒烟助手论坛系统的REST API接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@jiayan.com")
                                .url("https://jiayan.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("本地开发环境"),
                        new Server()
                                .url("https://api.jiayan.com")
                                .description("生产环境")
                ));
    }
    */
} 