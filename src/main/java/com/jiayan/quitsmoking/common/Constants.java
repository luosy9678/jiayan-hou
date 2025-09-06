package com.jiayan.quitsmoking.common;

/**
 * 系统常量
 */
public class Constants {
    
    // Redis Key前缀
    public static final String REDIS_SMS_CODE = "sms:code:";
    public static final String REDIS_USER_TOKEN = "user:token:";
    public static final String REDIS_USER_INFO = "user:info:";
    
    // Token相关
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    
    // 文件相关
    public static final String AVATAR_PATH = "avatar/";
    public static final String AUDIO_PATH = "audio/";
    public static final String IMAGE_PATH = "image/";
    
    // 会员相关
    public static final String MEMBER_BASIC = "basic";
    public static final String MEMBER_PREMIUM = "premium";
    
    // 第三方平台
    public static final String PLATFORM_WECHAT = "wechat";
    public static final String PLATFORM_QQ = "qq";
    public static final String PLATFORM_WEIBO = "weibo";
    
    // 验证码相关
    public static final String SMS_TYPE_REGISTER = "register";
    public static final String SMS_TYPE_LOGIN = "login";
    public static final String SMS_TYPE_RESET = "reset";
} 