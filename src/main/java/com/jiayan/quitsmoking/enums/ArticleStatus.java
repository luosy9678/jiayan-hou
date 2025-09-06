package com.jiayan.quitsmoking.enums;

/**
 * 文章状态枚举
 */
public enum ArticleStatus {
    
    DRAFT("draft", "草稿"),
    PENDING("pending", "待审核"),
    PUBLISHED("published", "已发布"),
    REJECTED("rejected", "已拒绝"),
    ARCHIVED("archived", "已归档"),
    BANNED("banned", "已禁用");
    
    private final String code;
    private final String description;
    
    ArticleStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ArticleStatus fromCode(String code) {
        for (ArticleStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown article status: " + code);
    }
} 