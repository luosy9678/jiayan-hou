package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试协议Controller - 最简单的版本
 */
@RestController
@RequestMapping("/api/v1/test-agreements")
@Slf4j
public class TestAgreementController {

    @GetMapping("/hello")
    public ApiResponse<String> hello() {
        log.info("=== 测试协议Controller hello方法 ===");
        return ApiResponse.success("Hello from TestAgreementController!");
    }
    
    @GetMapping("/types")
    public ApiResponse<String> getTypes() {
        log.info("=== 测试协议Controller types方法 ===");
        return ApiResponse.success("协议类型列表 - 测试数据");
    }
} 