package com.jiayan.quitsmoking.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 微信授权结果DTO
 */
@Data
@Builder
public class WechatAuthResult {
    
    private String openid;
    private String unionid;
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String scope;
    
    // 用户信息
    private String nickname;
    private String avatar;
    private Integer sex;
    private String province;
    private String city;
    private String country;
} 