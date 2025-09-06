package com.jiayan.quitsmoking.service;

import com.jiayan.quitsmoking.dto.*;

public interface ThirdPartyAuthService {

    /**
     * 微信获取Access Token
     */
    WechatAccessTokenResponse getWechatAccessToken(String code);

    /**
     * 微信获取用户信息
     */
    WechatUserInfoResponse getWechatUserInfo(String accessToken, String openid);

    /**
     * 第三方登录绑定
     */
    AuthResponse thirdPartyBind(ThirdPartyBindRequest request);
} 