package com.jiayan.quitsmoking.exception;

import com.jiayan.quitsmoking.enums.ErrorCode;

/**
 * 日记相关业务异常
 */
public class DiaryException extends BusinessException {
    
    public DiaryException(String message) {
        super(ErrorCode.DIARY_ERROR.getCode(), message);
    }
    
    public DiaryException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }
    
    public static DiaryException diaryNotFound(Long diaryId) {
        return new DiaryException(ErrorCode.DIARY_NOT_FOUND, 
                String.format("日记不存在，ID: %d", diaryId));
    }
    
    public static DiaryException noPermission(Long diaryId) {
        return new DiaryException(ErrorCode.DIARY_NO_PERMISSION, 
                String.format("无权限访问日记，ID: %d", diaryId));
    }
    
    public static DiaryException userNotAuthenticated() {
        return new DiaryException(ErrorCode.USER_NOT_AUTHENTICATED, 
                "用户未认证或token无效");
    }
} 