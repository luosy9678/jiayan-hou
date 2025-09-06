package com.jiayan.quitsmoking.config;

import com.jiayan.quitsmoking.entity.AgreementType;
import com.jiayan.quitsmoking.entity.Agreement;
import com.jiayan.quitsmoking.repository.AgreementTypeRepository;
import com.jiayan.quitsmoking.repository.AgreementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 简单数据初始化组件
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SimpleDataInitializer implements CommandLineRunner {
    
    private final AgreementTypeRepository agreementTypeRepository;
    private final AgreementRepository agreementRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化协议数据...");
        
        // 初始化协议类型
        initializeAgreementTypes();
        
        // 初始化示例协议数据
        initializeSampleAgreements();
        
        log.info("协议数据初始化完成");
    }
    
    /**
     * 初始化协议类型
     */
    private void initializeAgreementTypes() {
        // 用户协议
        if (!agreementTypeRepository.existsByTypeCode("USER_AGREEMENT")) {
            AgreementType userAgreement = new AgreementType();
            userAgreement.setTypeCode("USER_AGREEMENT");
            userAgreement.setTypeName("用户协议");
            userAgreement.setDescription("用户使用本应用需要遵守的协议条款");
            userAgreement.setIsActive(true);
            userAgreement.setSortOrder(1);
            agreementTypeRepository.save(userAgreement);
            log.info("创建协议类型: 用户协议");
        }
        
        // 隐私政策
        if (!agreementTypeRepository.existsByTypeCode("PRIVACY_POLICY")) {
            AgreementType privacyPolicy = new AgreementType();
            privacyPolicy.setTypeCode("PRIVACY_POLICY");
            privacyPolicy.setTypeName("隐私政策");
            privacyPolicy.setDescription("关于用户隐私保护的政策说明");
            privacyPolicy.setIsActive(true);
            privacyPolicy.setSortOrder(2);
            agreementTypeRepository.save(privacyPolicy);
            log.info("创建协议类型: 隐私政策");
        }
        
        // 会员协议
        if (!agreementTypeRepository.existsByTypeCode("MEMBER_AGREEMENT")) {
            AgreementType memberAgreement = new AgreementType();
            memberAgreement.setTypeCode("MEMBER_AGREEMENT");
            memberAgreement.setTypeName("会员协议");
            memberAgreement.setDescription("会员服务相关的协议条款");
            memberAgreement.setIsActive(true);
            memberAgreement.setSortOrder(3);
            agreementTypeRepository.save(memberAgreement);
            log.info("创建协议类型: 会员协议");
        }
        
        // 服务条款
        if (!agreementTypeRepository.existsByTypeCode("TERMS_OF_SERVICE")) {
            AgreementType termsOfService = new AgreementType();
            termsOfService.setTypeCode("TERMS_OF_SERVICE");
            termsOfService.setTypeName("服务条款");
            termsOfService.setDescription("应用服务的使用条款和规则");
            termsOfService.setIsActive(true);
            termsOfService.setSortOrder(4);
            agreementTypeRepository.save(termsOfService);
            log.info("创建协议类型: 服务条款");
        }
    }
    
    /**
     * 初始化示例协议数据
     */
    private void initializeSampleAgreements() {
        try {
            // 获取协议类型
            AgreementType userAgreementType = agreementTypeRepository.findByTypeCode("USER_AGREEMENT").orElse(null);
            AgreementType privacyPolicyType = agreementTypeRepository.findByTypeCode("PRIVACY_POLICY").orElse(null);
            AgreementType memberAgreementType = agreementTypeRepository.findByTypeCode("MEMBER_AGREEMENT").orElse(null);
            AgreementType termsOfServiceType = agreementTypeRepository.findByTypeCode("TERMS_OF_SERVICE").orElse(null);
            
            // 检查并创建用户协议
            if (userAgreementType != null && !hasAgreementForType(userAgreementType.getId())) {
                Agreement userAgreement = new Agreement();
                userAgreement.setAgreementType(userAgreementType);
                userAgreement.setVersionCode("1.0.0");
                userAgreement.setVersionName("初始版本");
                userAgreement.setTitle("用户协议");
                userAgreement.setContent("欢迎使用知行戒烟APP！\n\n本协议是您与知行戒烟APP之间关于使用本应用服务所订立的协议。\n\n1. 服务内容\n知行戒烟APP为用户提供戒烟相关的健康管理服务。\n\n2. 用户责任\n用户应遵守相关法律法规，不得利用本应用进行违法活动。\n\n3. 隐私保护\n我们承诺保护用户隐私，不会泄露用户个人信息。");
                userAgreement.setSummary("用户使用本应用需要遵守的协议条款");
                userAgreement.setEffectiveDate(java.time.LocalDateTime.now().minusDays(30));
                userAgreement.setIsCurrent(true);
                userAgreement.setIsActive(true);
                userAgreement.setCreatedBy("系统");
                agreementRepository.save(userAgreement);
                log.info("创建示例协议: 用户协议");
            }
            
            // 检查并创建隐私政策
            if (privacyPolicyType != null && !hasAgreementForType(privacyPolicyType.getId())) {
                Agreement privacyPolicy = new Agreement();
                privacyPolicy.setAgreementType(privacyPolicyType);
                privacyPolicy.setVersionCode("1.0.0");
                privacyPolicy.setVersionName("初始版本");
                privacyPolicy.setTitle("隐私政策");
                privacyPolicy.setContent("隐私政策\n\n我们非常重视您的隐私保护。本隐私政策说明了我们如何收集、使用和保护您的个人信息。\n\n1. 信息收集\n我们可能收集您的设备信息、使用数据等。\n\n2. 信息使用\n收集的信息仅用于提供服务、改善用户体验。\n\n3. 信息保护\n我们采用行业标准的安全措施保护您的信息。");
                privacyPolicy.setSummary("关于用户隐私保护的政策说明");
                privacyPolicy.setEffectiveDate(java.time.LocalDateTime.now().minusDays(20));
                privacyPolicy.setIsCurrent(true);
                privacyPolicy.setIsActive(true);
                privacyPolicy.setCreatedBy("系统");
                agreementRepository.save(privacyPolicy);
                log.info("创建示例协议: 隐私政策");
            }
            
            // 检查并创建会员协议
            if (memberAgreementType != null && !hasAgreementForType(memberAgreementType.getId())) {
                Agreement memberAgreement = new Agreement();
                memberAgreement.setAgreementType(memberAgreementType);
                memberAgreement.setVersionCode("1.0.0");
                memberAgreement.setVersionName("初始版本");
                memberAgreement.setTitle("会员协议");
                memberAgreement.setContent("会员服务协议\n\n感谢您选择我们的会员服务！\n\n1. 会员权益\n会员用户可享受更多高级功能和服务。\n\n2. 会员费用\n会员服务需要支付相应的费用。\n\n3. 服务期限\n会员服务有明确的服务期限。\n\n4. 退费政策\n我们提供合理的退费政策。");
                memberAgreement.setSummary("会员服务相关的协议条款");
                memberAgreement.setEffectiveDate(java.time.LocalDateTime.now().minusDays(10));
                memberAgreement.setIsCurrent(true);
                memberAgreement.setIsActive(true);
                memberAgreement.setCreatedBy("系统");
                agreementRepository.save(memberAgreement);
                log.info("创建示例协议: 会员协议");
            }
            
            // 检查并创建服务条款
            if (termsOfServiceType != null && !hasAgreementForType(termsOfServiceType.getId())) {
                Agreement termsOfService = new Agreement();
                termsOfService.setAgreementType(termsOfServiceType);
                termsOfService.setVersionCode("1.0.0");
                termsOfService.setVersionName("初始版本");
                termsOfService.setTitle("服务条款");
                termsOfService.setContent("服务条款\n\n本条款规定了知行戒烟APP的服务规则和使用条件。\n\n1. 服务说明\n本应用是一个健康管理平台，主要功能包括戒烟计划管理、健康数据记录等。\n\n2. 使用规则\n用户在使用过程中应遵守相关法律法规，不得发布违法信息。\n\n3. 服务限制\n我们保留限制违规用户使用的权利。\n\n4. 免责声明\n服务按\"现状\"提供，不保证服务无中断或错误。");
                termsOfService.setSummary("应用服务的使用条款和规则");
                termsOfService.setEffectiveDate(java.time.LocalDateTime.now().minusDays(5));
                termsOfService.setIsCurrent(true);
                termsOfService.setIsActive(true);
                termsOfService.setCreatedBy("系统");
                agreementRepository.save(termsOfService);
                log.info("创建示例协议: 服务条款");
            }
            
        } catch (Exception e) {
            log.error("初始化示例协议数据失败", e);
        }
    }
    
    /**
     * 检查指定类型是否已有协议
     */
    private boolean hasAgreementForType(Long typeId) {
        // 直接通过类型ID查找协议
        try {
            return agreementRepository.findByAgreementType_Id(typeId).size() > 0;
        } catch (Exception e) {
            log.warn("检查协议类型协议时出错: typeId={}", typeId, e);
            return false;
        }
    }
} 