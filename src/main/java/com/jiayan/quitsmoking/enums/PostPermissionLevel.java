package com.jiayan.quitsmoking.enums;

/**
 * 发帖权限级别枚举
 */
public enum PostPermissionLevel {
    
    NONE("none", "无权限"),
    LIMITED("limited", "有限权限"),
    FULL("full", "完全权限");
    
    private final String code;
    private final String description;
    
    PostPermissionLevel(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static PostPermissionLevel fromCode(String code) {
        for (PostPermissionLevel level : values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown post permission level: " + code);
    }
} 