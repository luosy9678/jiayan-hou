package com.jiayan.quitsmoking.enums;

/**
 * 评论状态枚举
 */
public enum CommentStatus {
    
    ACTIVE("active", "正常"),
    HIDDEN("hidden", "隐藏"),
    DELETED("deleted", "删除"),
    BANNED("banned", "封禁");
    
    private final String code;
    private final String description;
    
    CommentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static CommentStatus fromCode(String code) {
        for (CommentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown comment status: " + code);
    }
} 