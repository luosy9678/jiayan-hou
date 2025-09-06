package com.jiayan.quitsmoking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 最简单的Controller - 不依赖任何其他类
 */
@RestController
public class UltraMinimalController {

    // 添加启动日志
    {
        System.out.println("=== UltraMinimalController 已加载 ===");
        System.out.println("=== UltraMinimalController 路径: /ultra ===");
        System.out.println("=== UltraMinimalController 注解: @RestController ===");
    }

    @GetMapping("/ultra")
    public String ultra() {
        return "Ultra Minimal Controller Works!";
    }
} 