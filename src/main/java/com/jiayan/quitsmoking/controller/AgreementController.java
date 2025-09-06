package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import com.jiayan.quitsmoking.entity.Agreement;
import com.jiayan.quitsmoking.entity.AgreementType;
import com.jiayan.quitsmoking.service.AgreementService;
import com.jiayan.quitsmoking.repository.AgreementTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 协议相关接口（前端使用）
 */
@RestController
@RequestMapping("/agreements")
@Slf4j
@RequiredArgsConstructor
public class AgreementController {
    
    private final AgreementService agreementService;
    private final AgreementTypeRepository agreementTypeRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取协议类型列表
     */
    @GetMapping("/types")
    public ApiResponse<List<Map<String, Object>>> getAgreementTypes() {
        try {
            log.info("获取协议类型列表");
            
            List<AgreementType> types = agreementTypeRepository.findByIsActiveTrueOrderBySortOrder();
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (AgreementType type : types) {
                Map<String, Object> typeMap = new HashMap<>();
                typeMap.put("typeCode", type.getTypeCode());
                typeMap.put("typeName", type.getTypeName());
                typeMap.put("description", type.getDescription());
                typeMap.put("url", "/agreements/" + type.getTypeCode());
                result.add(typeMap);
            }
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取协议类型列表失败", e);
            return ApiResponse.error(500, "获取协议类型列表失败");
        }
    }

    /**
     * 获取用户服务协议
     */
    @GetMapping("/user-agreement")
    public ApiResponse<Map<String, Object>> getUserAgreement() {
        try {
            log.info("获取用户服务协议");
            return getAgreementByTypeCode("USER_AGREEMENT");
        } catch (Exception e) {
            log.error("获取用户服务协议失败", e);
            return ApiResponse.error(500, "获取用户服务协议失败");
        }
    }

    /**
     * 获取隐私政策
     */
    @GetMapping("/privacy-policy")
    public ApiResponse<Map<String, Object>> getPrivacyPolicy() {
        try {
            log.info("获取隐私政策");
            return getAgreementByTypeCode("PRIVACY_POLICY");
        } catch (Exception e) {
            log.error("获取隐私政策失败", e);
            return ApiResponse.error(500, "获取隐私政策失败");
        }
    }

    /**
     * 获取会员协议
     */
    @GetMapping("/membership-agreement")
    public ApiResponse<Map<String, Object>> getMembershipAgreement() {
        try {
            log.info("获取会员协议");
            return getAgreementByTypeCode("MEMBER_AGREEMENT");
        } catch (Exception e) {
            log.error("获取会员协议失败", e);
            return ApiResponse.error(500, "获取会员协议失败");
        }
    }

    /**
     * 获取服务条款
     */
    @GetMapping("/terms-of-service")
    public ApiResponse<Map<String, Object>> getTermsOfService() {
        try {
            log.info("获取服务条款");
            return getAgreementByTypeCode("TERMS_OF_SERVICE");
        } catch (Exception e) {
            log.error("获取服务条款失败", e);
            return ApiResponse.error(500, "获取服务条款失败");
        }
    }

    /**
     * 根据类型获取协议
     */
    @GetMapping("/{typeCode}")
    public ApiResponse<Map<String, Object>> getAgreementByType(@PathVariable String typeCode) {
        try {
            log.info("根据类型获取协议: typeCode={}", typeCode);
            return getAgreementByTypeCode(typeCode);
        } catch (Exception e) {
            log.error("根据类型获取协议失败: typeCode={}", typeCode, e);
            return ApiResponse.error(500, "获取协议失败");
        }
    }

    /**
     * 获取协议摘要
     */
    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> getAgreementsSummary() {
        try {
            log.info("获取协议摘要信息");
            
            List<AgreementType> types = agreementTypeRepository.findByIsActiveTrueOrderBySortOrder();
            List<Map<String, Object>> agreements = new ArrayList<>();
            
            for (AgreementType type : types) {
                try {
                    Agreement agreement = agreementService.getCurrentAgreementByType(type.getTypeCode());
                    Map<String, Object> agreementMap = new HashMap<>();
                    agreementMap.put("type", type.getTypeName());
                    agreementMap.put("version", agreement.getVersionName());
                    agreementMap.put("status", agreement.getIsCurrent() ? "生效中" : "非当前版本");
                    agreements.add(agreementMap);
                } catch (Exception e) {
                    log.warn("获取协议类型 {} 的当前版本失败", type.getTypeCode(), e);
                    // 如果某个类型没有当前版本，跳过
                    continue;
                }
            }
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalCount", agreements.size());
            summary.put("lastUpdated", java.time.LocalDateTime.now().format(DATE_FORMATTER));
            summary.put("agreements", agreements);
            
            return ApiResponse.success(summary);
        } catch (Exception e) {
            log.error("获取协议摘要失败", e);
            return ApiResponse.error(500, "获取协议摘要失败");
        }
    }

    /**
     * 根据类型代码获取协议的通用方法
     */
    private ApiResponse<Map<String, Object>> getAgreementByTypeCode(String typeCode) {
        try {
            Agreement agreement = agreementService.getCurrentAgreementByType(typeCode);
            
            Map<String, Object> result = new HashMap<>();
            result.put("title", agreement.getTitle());
            result.put("version", agreement.getVersionName());
            result.put("effectiveDate", agreement.getEffectiveDate().format(DATE_FORMATTER));
            result.put("content", agreement.getContent());
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取协议失败: typeCode={}", typeCode, e);
            return ApiResponse.error(404, "协议不存在或未找到当前版本");
        }
    }
} 