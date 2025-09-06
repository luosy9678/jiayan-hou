package com.jiayan.quitsmoking.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class WechatUserInfoRequest {

    @NotBlank(message = "access_token不能为空")
    private String accessToken;

    @NotBlank(message = "openid不能为空")
    private String openid;
} 