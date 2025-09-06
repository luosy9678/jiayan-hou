package com.jiayan.quitsmoking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 知行戒烟APP主应用程序类
 */
@SpringBootApplication(scanBasePackages = "com.jiayan.quitsmoking")
@EnableJpaRepositories
public class QuitSmokingApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuitSmokingApplication.class, args);
        System.out.println("======================================");
        System.out.println("知行戒烟APP后端服务启动成功！");
        System.out.println("API文档: http://localhost:8081/api/v1");
        System.out.println("======================================");
    }
} 