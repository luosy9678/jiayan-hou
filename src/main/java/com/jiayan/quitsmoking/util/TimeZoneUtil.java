package com.jiayan.quitsmoking.util;

import lombok.extern.slf4j.Slf4j;

import java.time.*;

/**
 * 时区工具类
 * 用于处理UTC时间和北京时间的转换
 */
@Slf4j
public class TimeZoneUtil {
    
    /**
     * 北京时区（UTC+8）
     */
    public static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");
    
    /**
     * UTC时区
     */
    public static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    
    /**
     * 将UTC时间转换为北京时间
     */
    public static LocalDateTime convertUTCToBeijing(LocalDateTime utcTime) {
        if (utcTime == null) {
            return null;
        }
        
        try {
            ZonedDateTime utcZoned = utcTime.atZone(UTC_ZONE);
            ZonedDateTime beijingZoned = utcZoned.withZoneSameInstant(BEIJING_ZONE);
            return beijingZoned.toLocalDateTime();
        } catch (Exception e) {
            log.warn("UTC时间转换为北京时间失败: {}", e.getMessage());
            return utcTime; // 转换失败时返回原时间
        }
    }
    
    /**
     * 将北京时间转换为UTC时间
     */
    public static LocalDateTime convertBeijingToUTC(LocalDateTime beijingTime) {
        if (beijingTime == null) {
            return null;
        }
        
        try {
            ZonedDateTime beijingZoned = beijingTime.atZone(BEIJING_ZONE);
            ZonedDateTime utcZoned = beijingZoned.withZoneSameInstant(UTC_ZONE);
            return utcZoned.toLocalDateTime();
        } catch (Exception e) {
            log.warn("北京时间转换为UTC时间失败: {}", e.getMessage());
            return beijingTime; // 转换失败时返回原时间
        }
    }
    
    /**
     * 获取当前北京时间
     */
    public static LocalDateTime getCurrentBeijingTime() {
        return LocalDateTime.now(BEIJING_ZONE);
    }
    
    /**
     * 获取当前UTC时间
     */
    public static LocalDateTime getCurrentUTCTime() {
        return LocalDateTime.now(UTC_ZONE);
    }
} 