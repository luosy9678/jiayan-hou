package com.jiayan.quitsmoking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 最简单的Controller - 不依赖任何其他类
 */
@RestController
public class MinimalController {

    // 添加启动日志
    {
        System.out.println("=== MinimalController 已加载 ===");
    }

    @GetMapping("/minimal")
    public String minimal() {
        return "Minimal Controller Works!";
    }
} 