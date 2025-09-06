package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.Agreement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 协议Repository接口
 */
@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {
    
    /**
     * 根据协议类型代码查找协议
     */
    List<Agreement> findByAgreementType_TypeCode(String typeCode);
    
    /**
     * 根据协议类型代码和当前状态查找协议
     */
    List<Agreement> findByAgreementType_TypeCodeAndIsCurrent(String typeCode, Boolean isCurrent);
    
    /**
     * 查找当前版本的协议
     */
    List<Agreement> findByIsCurrentTrue();
    
    /**
     * 查找活跃的协议
     */
    List<Agreement> findByIsActiveTrue();
    
    /**
     * 分页查询协议（支持类型和状态过滤）
     */
    @Query("SELECT a FROM Agreement a WHERE " +
           "(:typeCode IS NULL OR a.agreementType.typeCode = :typeCode) AND " +
           "(:isCurrent IS NULL OR a.isCurrent = :isCurrent) AND " +
           "a.isActive = true " +
           "ORDER BY a.agreementType.sortOrder, a.effectiveDate DESC")
    Page<Agreement> findAgreementsWithFilters(
            @Param("typeCode") String typeCode,
            @Param("isCurrent") Boolean isCurrent,
            Pageable pageable);
    
    /**
     * 根据协议类型查找当前版本
     */
    Optional<Agreement> findByAgreementType_TypeCodeAndIsCurrentTrue(String typeCode);
    
    /**
     * 根据协议类型ID查找协议
     */
    List<Agreement> findByAgreementType_Id(Long typeId);
} 