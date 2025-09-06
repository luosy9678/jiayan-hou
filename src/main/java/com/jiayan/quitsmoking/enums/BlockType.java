package com.jiayan.quitsmoking.enums;

/**
 * 内容块类型枚举
 */
public enum BlockType {
    
    TEXT("text", "文本"),
    IMAGE("image", "图片");
    
    private final String code;
    private final String description;
    
    BlockType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static BlockType fromCode(String code) {
        for (BlockType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown block type: " + code);
    }
} 