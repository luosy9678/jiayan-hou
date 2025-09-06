package com.jiayan.quitsmoking.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.jiayan.quitsmoking.enums.QuitMode;

import java.io.IOException;

/**
 * QuitMode枚举的自定义反序列化器
 * 支持小写字符串到枚举值的转换
 */
public class QuitModeDeserializer extends JsonDeserializer<QuitMode> {
    
    @Override
    public QuitMode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        
        if (value == null || value.trim().isEmpty()) {
            return QuitMode.BOTH; // 默认值
        }
        
        // 转换为大写并尝试匹配
        String upperValue = value.trim().toUpperCase();
        
        try {
            return QuitMode.valueOf(upperValue);
        } catch (IllegalArgumentException e) {
            // 如果直接匹配失败，尝试通过code匹配
            for (QuitMode mode : QuitMode.values()) {
                if (mode.getCode().equalsIgnoreCase(value.trim())) {
                    return mode;
                }
            }
            
            // 如果都失败，返回默认值
            return QuitMode.BOTH;
        }
    }
} 