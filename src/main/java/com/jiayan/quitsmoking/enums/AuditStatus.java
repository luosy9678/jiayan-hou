package com.jiayan.quitsmoking.enums;

/**
 * 审核状态枚举
 */
public enum AuditStatus {
    
    PENDING("pending", "待审核"),
    APPROVED("approved", "已通过"),
    REJECTED("rejected", "已拒绝");
    
    private final String code;
    private final String description;
    
    AuditStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AuditStatus fromCode(String code) {
        for (AuditStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown audit status: " + code);
    }
} 