package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.Audio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 音频数据访问层
 */
@Repository
public interface AudioRepository extends JpaRepository<Audio, Long>, JpaSpecificationExecutor<Audio> {
    
    /**
     * 根据用户ID查找音频列表
     */
    List<Audio> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据用户ID查找未禁用的音频列表
     */
    List<Audio> findByUserIdAndIsDisabledFalseOrderByCreatedAtDesc(Long userId);
    
    /**
     * 查找所有公有且未禁用的音频
     */
    List<Audio> findByIsPublicTrueAndIsDisabledFalseOrderByCreatedAtDesc();
    
    /**
     * 根据音色类型查找公有音频（原有方法）
     */
    List<Audio> findByVoiceTypeAndIsPublicTrueAndIsDisabledFalseOrderByCreatedAtDesc(String voiceType);
    
    /**
     * 根据音色类型查找公有音频（新增方法，保持一致性）
     */
    List<Audio> findByIsPublicTrueAndIsDisabledFalseAndVoiceTypeOrderByCreatedAtDesc(String voiceType);
    
    /**
     * 根据文件名查找音频
     */
    Audio findByFileName(String fileName);
    
    /**
     * 检查用户是否拥有该音频
     */
    boolean existsByIdAndUserId(Long id, Long userId);
    
    /**
     * 统计用户的音频数量
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户的公有音频数量
     */
    long countByUserIdAndIsPublicTrue(Long userId);
    
    // 管理后台新增方法
    long countByIsPublicTrue();
    
    long countByIsPublicFalse();
    
    long countByIsDisabledTrue();
    
    @Modifying
    @Query("UPDATE Audio a SET a.isDisabled = :isDisabled WHERE a.id IN :audioIds")
    void updateAudioStatusByIds(@Param("audioIds") List<Long> audioIds, @Param("isDisabled") Boolean isDisabled);
} 