package com.jiayan.quitsmoking.enums;

/**
 * 戒烟方式枚举
 */
public enum QuitMode {
    
    REDUCTION("reduction", "减量戒烟"),
    ABSTINENCE("abstinence", "戒断戒烟"),
    BOTH("both", "两者都适合");
    
    private final String code;
    private final String description;
    
    QuitMode(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static QuitMode fromCode(String code) {
        for (QuitMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        return BOTH; // 默认返回两者都适合
    }
} 