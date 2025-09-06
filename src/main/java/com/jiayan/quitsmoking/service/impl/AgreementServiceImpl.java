package com.jiayan.quitsmoking.service.impl;

import com.jiayan.quitsmoking.entity.Agreement;
import com.jiayan.quitsmoking.entity.AgreementType;
import com.jiayan.quitsmoking.exception.BusinessException;
import com.jiayan.quitsmoking.repository.AgreementRepository;
import com.jiayan.quitsmoking.repository.AgreementTypeRepository;
import com.jiayan.quitsmoking.service.AgreementService;
import com.jiayan.quitsmoking.dto.CreateAgreementRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 协议服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgreementServiceImpl implements AgreementService {
    
    private final AgreementRepository agreementRepository;
    private final AgreementTypeRepository agreementTypeRepository;
    
    @Override
    public Page<Agreement> getAgreements(Pageable pageable, String typeCode, Boolean isCurrent) {
        log.info("获取协议列表: page={}, size={}, typeCode={}, isCurrent={}", 
                pageable.getPageNumber(), pageable.getPageSize(), typeCode, isCurrent);
        
        return agreementRepository.findAgreementsWithFilters(typeCode, isCurrent, pageable);
    }
    
    @Override
    public Agreement getAgreementById(Long id) {
        log.info("获取协议详情: id={}", id);
        
        return agreementRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "协议不存在"));
    }
    
    @Override
    @Transactional
    public Agreement createAgreement(CreateAgreementRequest request) {
        log.info("创建协议: title={}, typeCode={}", request.getTitle(), request.getTypeCode());
        
        // 验证协议类型是否存在
        AgreementType agreementType = agreementTypeRepository.findByTypeCode(request.getTypeCode())
                .orElseThrow(() -> new BusinessException(400, "协议类型不存在"));
        
        // 创建协议实体
        Agreement agreement = new Agreement();
        agreement.setAgreementType(agreementType);
        agreement.setVersionCode(request.getVersionCode());
        agreement.setVersionName(request.getVersionName());
        agreement.setTitle(request.getTitle());
        agreement.setContent(request.getContent());
        agreement.setSummary(request.getSummary());
        
        // 转换日期格式 - 支持多种格式
        if (request.getEffectiveDate() != null && !request.getEffectiveDate().isEmpty()) {
            agreement.setEffectiveDate(parseDateTime(request.getEffectiveDate()));
        }
        if (request.getExpiryDate() != null && !request.getExpiryDate().isEmpty()) {
            agreement.setExpiryDate(parseDateTime(request.getExpiryDate()));
        }
        
        agreement.setIsCurrent(request.getIsCurrent());
        agreement.setIsActive(true);
        agreement.setCreatedBy(request.getCreatedBy());
        
        // 如果设置为当前版本，需要将同类型的其他协议设为非当前版本
        if (agreement.getIsCurrent()) {
            agreementRepository.findByAgreementType_TypeCodeAndIsCurrent(
                    agreementType.getTypeCode(), true)
                    .forEach(existingAgreement -> {
                        existingAgreement.setIsCurrent(false);
                        agreementRepository.save(existingAgreement);
                    });
        }
        
        return agreementRepository.save(agreement);
    }
    
    @Override
    @Transactional
    public Agreement updateAgreement(Long id, Agreement agreement) {
        log.info("更新协议: id={}", id);
        
        Agreement existingAgreement = getAgreementById(id);
        
        // 更新字段
        existingAgreement.setTitle(agreement.getTitle());
        existingAgreement.setContent(agreement.getContent());
        existingAgreement.setSummary(agreement.getSummary());
        existingAgreement.setEffectiveDate(agreement.getEffectiveDate());
        existingAgreement.setExpiryDate(agreement.getExpiryDate());
        existingAgreement.setVersionName(agreement.getVersionName());
        
        return agreementRepository.save(existingAgreement);
    }
    
    @Override
    @Transactional
    public Agreement setAgreementCurrent(Long id, Boolean isCurrent) {
        log.info("设置协议当前版本: id={}, isCurrent={}", id, isCurrent);
        
        Agreement agreement = getAgreementById(id);
        
        if (isCurrent) {
            // 将同类型的其他协议设为非当前版本
            agreementRepository.findByAgreementType_TypeCodeAndIsCurrent(
                    agreement.getAgreementType().getTypeCode(), true)
                    .forEach(existingAgreement -> {
                        if (!existingAgreement.getId().equals(id)) {
                            existingAgreement.setIsCurrent(false);
                            agreementRepository.save(existingAgreement);
                        }
                    });
        }
        
        agreement.setIsCurrent(isCurrent);
        return agreementRepository.save(agreement);
    }
    
    @Override
    @Transactional
    public void deleteAgreement(Long id) {
        log.info("删除协议: id={}", id);
        
        Agreement agreement = getAgreementById(id);
        
        // 如果是当前版本，不允许删除
        if (agreement.getIsCurrent()) {
            throw new BusinessException(400, "不能删除当前版本的协议");
        }
        
        agreementRepository.delete(agreement);
    }
    
    @Override
    public Agreement getCurrentAgreementByType(String typeCode) {
        log.info("获取当前版本协议: typeCode={}", typeCode);
        
        return agreementRepository.findByAgreementType_TypeCodeAndIsCurrent(typeCode, true)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(404, "该类型的当前版本协议不存在"));
    }
    
    /**
     * 解析日期时间字符串，支持多种格式
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        
        // 支持的日期格式列表
        String[] patterns = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm"
        };
        
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDateTime.parse(dateTimeStr, formatter);
            } catch (Exception e) {
                // 继续尝试下一个格式
                continue;
            }
        }
        
        throw new BusinessException(400, "不支持的日期格式: " + dateTimeStr + 
            "，支持的格式: yyyy-MM-dd HH:mm:ss, yyyy-MM-ddTHH:mm, yyyy-MM-ddTHH:mm:ss");
    }
} 