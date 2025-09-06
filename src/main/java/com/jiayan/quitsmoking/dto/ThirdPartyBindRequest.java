package com.jiayan.quitsmoking.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ThirdPartyBindRequest {

    @NotBlank(message = "平台类型不能为空")
    private String platform; // wechat/qq/weibo

    @NotBlank(message = "openid不能为空")
    private String openid;

    private String nickname;
    private String avatar;
    private Integer sex;
    private String province;
    private String city;
    private String unionid;
} 