package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.SmokingRecord;
import com.jiayan.quitsmoking.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 吸烟记录数据访问层
 */
@Repository
public interface SmokingRecordRepository extends JpaRepository<SmokingRecord, Long> {
    
    /**
     * 根据用户ID和记录类型查找记录
     */
    List<SmokingRecord> findByUserIdAndRecordTypeOrderByTimestampDesc(Long userId, RecordType recordType);
    
    /**
     * 根据用户ID和记录类型分页查找记录
     */
    Page<SmokingRecord> findByUserIdAndRecordType(Long userId, RecordType recordType, Pageable pageable);
    
    /**
     * 根据用户ID、记录类型和日期范围查找记录
     */
    List<SmokingRecord> findByUserIdAndRecordTypeAndCreatedAtBetween(
            Long userId, RecordType recordType, LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据记录类型统计数量
     */
    long countByRecordType(RecordType recordType);
    
    /**
     * 统计今日吸烟记录数量
     */
    @Query("SELECT COUNT(s) FROM SmokingRecord s WHERE s.recordType = :recordType AND DATE(s.createdAt) = CURRENT_DATE")
    long countTodayRecords(@Param("recordType") RecordType recordType);
    
    /**
     * 统计今日吸烟记录数量（简化版）
     */
    @Query("SELECT COUNT(s) FROM SmokingRecord s WHERE s.recordType = 'SMOKING' AND DATE(s.createdAt) = CURRENT_DATE")
    long countTodaySmokingRecords();
    
    /**
     * 统计今日训练记录数量
     */
    @Query("SELECT COUNT(s) FROM SmokingRecord s WHERE s.recordType = 'TRAINING' AND DATE(s.createdAt) = CURRENT_DATE")
    long countTodayTrainingRecords();
    
    /**
     * 查找用户最后一次吸烟记录
     */
    SmokingRecord findFirstByUserIdAndRecordTypeOrderByTimestampDesc(Long userId, RecordType recordType);
    
    /**
     * 根据用户ID删除记录
     */
    void deleteByUserId(Long userId);

    // 添加按用户ID统计今日吸烟记录的方法
    @Query("SELECT COUNT(s) FROM SmokingRecord s WHERE s.userId = :userId AND s.recordType = 'SMOKING' AND DATE(s.createdAt) = CURRENT_DATE")
    long countTodaySmokingRecordsByUserId(@Param("userId") Long userId);

    // 添加按用户ID统计今日训练记录的方法  
    @Query("SELECT COUNT(s) FROM SmokingRecord s WHERE s.userId = :userId AND s.recordType = 'TRAINING' AND DATE(s.createdAt) = CURRENT_DATE")
    long countTodayTrainingRecordsByUserId(@Param("userId") Long userId);
} 