package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 自定义错误控制器
 * 处理Spring Boot默认错误页面的请求
 */
@RestController
public class CustomErrorController implements ErrorController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);
    
    @RequestMapping("/error")
    public ApiResponse<String> handleError(HttpServletRequest request) {
        try {
            // 获取错误相关的属性
            Integer statusCode = getAttributeSafely(request, "javax.servlet.error.status_code", Integer.class);
            String errorMessage = getAttributeSafely(request, "javax.servlet.error.message", String.class);
            String requestUri = getAttributeSafely(request, "javax.servlet.error.request_uri", String.class);
            String exceptionType = getAttributeSafely(request, "javax.servlet.error.exception_type", String.class);
            String exceptionMessage = getAttributeSafely(request, "javax.servlet.error.exception", String.class);
            
            // 安全处理状态码，避免空指针异常
            int code = statusCode != null ? statusCode : 500;
            String message = errorMessage != null ? errorMessage : "服务器内部错误";
            
            // 获取更多调试信息
            String servletName = getAttributeSafely(request, "javax.servlet.error.servlet_name", String.class);
            Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
            
            // 记录详细的错误日志
            logger.warn("错误请求详情 - URI: {}, 状态码: {}, 错误信息: {}, 异常类型: {}, 异常信息: {}, Servlet: {}, Throwable: {}", 
                       requestUri, code, message, exceptionType, exceptionMessage, servletName, 
                       throwable != null ? throwable.getClass().getSimpleName() : "null");
            
            // 如果有异常，记录完整的堆栈信息
            if (throwable != null) {
                logger.error("异常堆栈信息", throwable);
            }
            
            // 记录请求头信息（调试用）
            if (logger.isDebugEnabled()) {
                logRequestHeaders(request);
            }
            
            // 根据状态码返回不同的错误信息
            return getErrorResponse(code, message, requestUri);
            
        } catch (Exception e) {
            logger.error("错误控制器处理异常时发生错误", e);
            return ApiResponse.error(500, "服务器内部错误");
        }
    }
    
    /**
     * 安全获取请求属性
     */
    @SuppressWarnings("unchecked")
    private <T> T getAttributeSafely(HttpServletRequest request, String attributeName, Class<T> type) {
        try {
            Object attribute = request.getAttribute(attributeName);
            if (attribute != null && type.isInstance(attribute)) {
                return (T) attribute;
            }
        } catch (Exception e) {
            logger.debug("获取属性 {} 失败: {}", attributeName, e.getMessage());
        }
        return null;
    }
    
    /**
     * 根据状态码返回相应的错误响应
     */
    private ApiResponse<String> getErrorResponse(int code, String message, String requestUri) {
        switch (code) {
            case 400:
                return ApiResponse.error(400, "请求参数错误");
            case 401:
                return ApiResponse.error(401, "未授权，请先登录");
            case 403:
                return ApiResponse.error(403, "访问被拒绝，权限不足");
            case 404:
                return ApiResponse.error(404, "请求的接口不存在: " + (requestUri != null ? requestUri : "未知路径"));
            case 405:
                return ApiResponse.error(405, "请求方法不允许");
            case 408:
                return ApiResponse.error(408, "请求超时");
            case 413:
                return ApiResponse.error(413, "请求实体过大");
            case 415:
                return ApiResponse.error(415, "不支持的媒体类型");
            case 429:
                return ApiResponse.error(429, "请求过于频繁，请稍后重试");
            case 500:
                return ApiResponse.error(500, "服务器内部错误");
            case 502:
                return ApiResponse.error(502, "网关错误");
            case 503:
                return ApiResponse.error(503, "服务不可用");
            case 504:
                return ApiResponse.error(504, "网关超时");
            default:
                return ApiResponse.error(code, message);
        }
    }
    
    /**
     * 记录请求头信息（仅调试模式）
     */
    private void logRequestHeaders(HttpServletRequest request) {
        try {
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                logger.debug("请求头信息:");
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    logger.debug("  {}: {}", headerName, headerValue);
                }
            }
        } catch (Exception e) {
            logger.debug("记录请求头信息失败: {}", e.getMessage());
        }
    }
} 