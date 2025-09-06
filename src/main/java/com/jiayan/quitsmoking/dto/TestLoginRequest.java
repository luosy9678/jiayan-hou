package com.jiayan.quitsmoking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 测试登录请求DTO
 */
@Data
public class TestLoginRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
} 