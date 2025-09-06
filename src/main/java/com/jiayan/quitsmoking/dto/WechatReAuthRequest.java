package com.jiayan.quitsmoking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信重新授权登录请求DTO
 */
@Data
public class WechatReAuthRequest {
    
    @NotBlank(message = "微信授权码不能为空")
    private String code;
    
    @NotBlank(message = "openid不能为空")
    private String openid;
    
    // 可选：用户信息（如果微信返回了用户信息）
    private String nickname;
    private String avatar;
    private Integer sex;
    private String province;
    private String city;
    private String unionid;
} 