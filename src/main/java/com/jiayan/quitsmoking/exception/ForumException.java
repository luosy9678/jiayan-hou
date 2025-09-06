package com.jiayan.quitsmoking.exception;

/**
 * 论坛相关异常
 */
public class ForumException extends BusinessException {
    
    public ForumException(String message) {
        super(message);
    }
    
    public ForumException(Integer code, String message) {
        super(code, message);
    }
} 