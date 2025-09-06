package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.AgreementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 协议类型Repository接口
 */
@Repository
public interface AgreementTypeRepository extends JpaRepository<AgreementType, Long> {
    
    /**
     * 根据类型代码查找协议类型
     */
    Optional<AgreementType> findByTypeCode(String typeCode);
    
    /**
     * 查找所有活跃的协议类型
     */
    List<AgreementType> findByIsActiveTrueOrderBySortOrder();
    
    /**
     * 检查类型代码是否存在
     */
    boolean existsByTypeCode(String typeCode);
} 