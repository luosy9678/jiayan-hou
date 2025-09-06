package com.jiayan.quitsmoking.dto;

import com.jiayan.quitsmoking.entity.Agreement;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 协议列表响应DTO
 */
@Data
public class AgreementListResponse {
    private Long id;
    private String typeCode;
    private String typeName;
    private String versionCode;
    private String versionName;
    private String title;
    private String content;
    private String summary;
    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private Boolean isCurrent;
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 从Agreement实体转换为DTO
     */
    public static AgreementListResponse fromEntity(Agreement agreement) {
        AgreementListResponse response = new AgreementListResponse();
        response.setId(agreement.getId());
        response.setTypeCode(agreement.getAgreementType().getTypeCode());
        response.setTypeName(agreement.getAgreementType().getTypeName());
        response.setVersionCode(agreement.getVersionCode());
        response.setVersionName(agreement.getVersionName());
        response.setTitle(agreement.getTitle());
        response.setContent(agreement.getContent());
        response.setSummary(agreement.getSummary());
        response.setEffectiveDate(agreement.getEffectiveDate());
        response.setExpiryDate(agreement.getExpiryDate());
        response.setIsCurrent(agreement.getIsCurrent());
        response.setIsActive(agreement.getIsActive());
        response.setCreatedBy(agreement.getCreatedBy());
        response.setCreatedAt(agreement.getCreatedAt());
        response.setUpdatedAt(agreement.getUpdatedAt());
        return response;
    }
} 