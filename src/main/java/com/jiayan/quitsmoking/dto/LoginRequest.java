package com.jiayan.quitsmoking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求DTO
 */
@Data
public class LoginRequest {

    @NotBlank(message = "手机号不能为空")
    private String phone; // 手机号

    @NotBlank(message = "密码不能为空")
    private String password;
} 