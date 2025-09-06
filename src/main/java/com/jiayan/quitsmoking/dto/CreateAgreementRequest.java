package com.jiayan.quitsmoking.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建协议请求DTO
 */
@Data
public class CreateAgreementRequest {
    @NotBlank(message = "协议类型代码不能为空")
    private String typeCode;
    
    @NotBlank(message = "版本号不能为空")
    private String versionCode;
    
    private String versionName;
    
    @NotBlank(message = "协议标题不能为空")
    private String title;
    
    @NotBlank(message = "协议内容不能为空")
    private String content;
    
    private String summary;
    
    @NotBlank(message = "生效日期不能为空")
    private String effectiveDate;
    
    private String expiryDate;
    
    private Boolean isCurrent = false;
    
    private String createdBy;
} 