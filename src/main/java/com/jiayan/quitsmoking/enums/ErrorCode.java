package com.jiayan.quitsmoking.enums;

/**
 * 错误码枚举
 */
public enum ErrorCode {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    
    // 用户相关错误
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    VERIFICATION_CODE_ERROR(1003, "验证码错误"),
    USER_ALREADY_EXISTS(1004, "用户已存在"),
    TOKEN_EXPIRED(1005, "Token已过期"),
    PHONE_ALREADY_EXISTS(1006, "手机号已存在"),
    EMAIL_ALREADY_EXISTS(1007, "邮箱已存在"),
    
    // 支付相关错误
    PAYMENT_FAILED(2001, "支付失败"),
    ORDER_NOT_FOUND(2002, "订单不存在"),
    ORDER_STATUS_ERROR(2003, "订单状态错误"),
    
    // 文件相关错误
    FILE_UPLOAD_FAILED(3001, "文件上传失败"),
    FILE_FORMAT_NOT_SUPPORTED(3002, "文件格式不支持"),
    FILE_SIZE_EXCEEDED(3003, "文件大小超过限制"),
    
    // 日记相关错误
    DIARY_ERROR(4001, "日记操作失败"),
    DIARY_NOT_FOUND(4002, "日记不存在"),
    DIARY_NO_PERMISSION(4003, "无权限访问日记"),
    USER_NOT_AUTHENTICATED(4004, "用户未认证"),
    
    // 如果缺少以下常量，需要添加：
    INVALID_PASSWORD(1002, "密码错误"),
    USER_DISABLED(1003, "用户已被禁用");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
} 