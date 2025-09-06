package com.jiayan.quitsmoking.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.jiayan.quitsmoking.enums.AccessLevel;

import java.io.IOException;

/**
 * AccessLevel枚举反序列化器
 * 支持从小写字符串反序列化为枚举值
 */
public class AccessLevelDeserializer extends JsonDeserializer<AccessLevel> {
    
    @Override
    public AccessLevel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.trim().isEmpty()) {
            return AccessLevel.FREE; // 默认值
        }
        
        try {
            return AccessLevel.fromCode(value.toLowerCase());
        } catch (IllegalArgumentException e) {
            // 如果无法解析，返回默认值
            return AccessLevel.FREE;
        }
    }
} 