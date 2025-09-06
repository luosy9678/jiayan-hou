package com.jiayan.quitsmoking.service;

import com.jiayan.quitsmoking.entity.Agreement;
import com.jiayan.quitsmoking.dto.CreateAgreementRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 协议服务接口
 */
public interface AgreementService {
    
    /**
     * 分页获取协议列表
     */
    Page<Agreement> getAgreements(Pageable pageable, String typeCode, Boolean isCurrent);
    
    /**
     * 根据ID获取协议详情
     */
    Agreement getAgreementById(Long id);
    
    /**
     * 创建协议
     */
    Agreement createAgreement(CreateAgreementRequest request);
    
    /**
     * 更新协议
     */
    Agreement updateAgreement(Long id, Agreement agreement);
    
    /**
     * 设置协议为当前版本
     */
    Agreement setAgreementCurrent(Long id, Boolean isCurrent);
    
    /**
     * 删除协议
     */
    void deleteAgreement(Long id);
    
    /**
     * 根据类型代码获取当前版本协议
     */
    Agreement getCurrentAgreementByType(String typeCode);
} 