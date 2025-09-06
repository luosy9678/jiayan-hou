package com.jiayan.quitsmoking.enums;

/**
 * 记录类型枚举
 */
public enum RecordType {
    SMOKING("smoking", "吸烟记录"),
    TRAINING("training", "训练记录");

    private final String code;
    private final String description;

    RecordType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RecordType fromCode(String code) {
        for (RecordType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown record type: " + code);
    }
} 