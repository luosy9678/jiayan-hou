package com.jiayan.quitsmoking.service;

import com.jiayan.quitsmoking.dto.CreateSmokingRecordRequest;
import com.jiayan.quitsmoking.dto.CreateTrainingRecordRequest;
import com.jiayan.quitsmoking.dto.SmokingRecordResponse;
import com.jiayan.quitsmoking.dto.TrainingRecordResponse;
import com.jiayan.quitsmoking.entity.SmokingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SmokingRecordService {

    /**
     * 创建吸烟记录
     */
    SmokingRecordResponse createSmokingRecord(Long userId, CreateSmokingRecordRequest request);

    /**
     * 创建训练记录
     */
    TrainingRecordResponse createTrainingRecord(Long userId, CreateTrainingRecordRequest request);

    /**
     * 获取用户的吸烟记录列表
     */
    List<SmokingRecordResponse> getUserSmokingRecords(Long userId);

    /**
     * 分页获取用户的吸烟记录
     */
    Page<SmokingRecordResponse> getUserSmokingRecords(Long userId, Pageable pageable);

    /**
     * 获取用户指定日期的吸烟记录
     */
    List<SmokingRecordResponse> getUserSmokingRecordsByDate(Long userId, LocalDate date);

    /**
     * 获取用户的训练记录列表
     */
    List<TrainingRecordResponse> getUserTrainingRecords(Long userId);

    /**
     * 分页获取用户的训练记录
     */
    Page<TrainingRecordResponse> getUserTrainingRecords(Long userId, Pageable pageable);

    /**
     * 获取用户指定日期的训练记录
     */
    List<TrainingRecordResponse> getUserTrainingRecordsByDate(Long userId, LocalDate date);

    /**
     * 统计用户今日吸烟次数
     */
    Long getTodaySmokingCount(Long userId);

    /**
     * 统计用户今日训练次数
     */
    Long getTodayTrainingCount(Long userId);

    /**
     * 获取用户最后一次吸烟记录
     */
    SmokingRecordResponse getLastSmokingRecord(Long userId);

    /**
     * 获取用户最后一次训练记录
     */
    TrainingRecordResponse getLastTrainingRecord(Long userId);

    /**
     * 删除吸烟记录
     */
    void deleteSmokingRecord(Long userId, Long recordId);

    /**
     * 删除训练记录
     */
    void deleteTrainingRecord(Long userId, Long recordId);
} 