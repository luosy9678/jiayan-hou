package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简单测试Controller - 使用和AdminController相同的路径格式
 */
@RestController
@RequestMapping("/simple-test")
@Slf4j
public class SimpleTestController {

    @GetMapping("/hello")
    public ApiResponse<String> hello() {
        log.info("=== SimpleTestController hello方法被调用===");
        return ApiResponse.success("Hello from SimpleTestController!");
    }
} 
