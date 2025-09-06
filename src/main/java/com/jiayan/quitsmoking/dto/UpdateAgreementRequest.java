package com.jiayan.quitsmoking.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 更新协议请求DTO
 */
@Data
public class UpdateAgreementRequest {
    private String versionName;
    
    @NotBlank(message = "协议标题不能为空")
    private String title;
    
    @NotBlank(message = "协议内容不能为空")
    private String content;
    
    private String summary;
    
    @NotNull(message = "生效日期不能为空")
    private LocalDateTime effectiveDate;
    
    private LocalDateTime expiryDate;
    
    private Boolean isCurrent = false;
} 