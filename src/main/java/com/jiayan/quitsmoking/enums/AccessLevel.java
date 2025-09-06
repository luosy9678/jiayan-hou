package com.jiayan.quitsmoking.enums;

/**
 * 访问权限级别枚举
 */
public enum AccessLevel {
    
    FREE("free", "免费"),
    MEMBER("member", "会员"),
    PREMIUM("premium", "高级会员");
    
    private final String code;
    private final String description;
    
    AccessLevel(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AccessLevel fromCode(String code) {
        for (AccessLevel level : values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown access level: " + code);
    }
} 