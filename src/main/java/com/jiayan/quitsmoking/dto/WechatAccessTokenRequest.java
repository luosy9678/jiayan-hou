package com.jiayan.quitsmoking.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class WechatAccessTokenRequest {

    @NotBlank(message = "微信授权码不能为空")
    private String code;
} 