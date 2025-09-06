package com.jiayan.quitsmoking.service.impl;

import com.jiayan.quitsmoking.dto.CreateSmokingRecordRequest;
import com.jiayan.quitsmoking.dto.CreateTrainingRecordRequest;
import com.jiayan.quitsmoking.dto.SmokingRecordResponse;
import com.jiayan.quitsmoking.dto.TrainingRecordResponse;
import com.jiayan.quitsmoking.entity.SmokingRecord;
import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.enums.ErrorCode;
import com.jiayan.quitsmoking.enums.RecordType;
import com.jiayan.quitsmoking.exception.BusinessException;
import com.jiayan.quitsmoking.repository.SmokingRecordRepository;
import com.jiayan.quitsmoking.repository.UserRepository;
import com.jiayan.quitsmoking.service.SmokingRecordService;
import com.jiayan.quitsmoking.util.TimeZoneUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 吸烟记录服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmokingRecordServiceImpl implements SmokingRecordService {
    
    private final SmokingRecordRepository smokingRecordRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public SmokingRecordResponse createSmokingRecord(Long userId, CreateSmokingRecordRequest request) {
        log.info("创建吸烟记录，用户ID: {}, 请求: {}", userId, request);
        
        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "用户不存在"));
        
        SmokingRecord record = new SmokingRecord();
        record.setUserId(userId);
        record.setRecordType(RecordType.SMOKING);
        record.setTimestamp(TimeZoneUtil.convertBeijingToUTC(request.getTimestamp())); // 将前端传递的北京时间转换为UTC时间存储
        record.setCigaretteCount(request.getCigaretteCount());
        record.setNote(request.getNote());
        
        SmokingRecord savedRecord = smokingRecordRepository.save(record);
        log.info("吸烟记录创建成功，ID: {}", savedRecord.getId());
        
        return SmokingRecordResponse.fromEntity(savedRecord);
    }
    
    @Override
    @Transactional
    public TrainingRecordResponse createTrainingRecord(Long userId, CreateTrainingRecordRequest request) {
        log.info("创建训练记录，用户ID: {}, 请求: {}", userId, request);
        
        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "用户不存在"));
        
        SmokingRecord record = new SmokingRecord();
        record.setUserId(userId);
        record.setRecordType(RecordType.TRAINING);
        record.setTimestamp(TimeZoneUtil.convertBeijingToUTC(request.getTimestamp())); // 将前端传递的北京时间转换为UTC时间存储
        record.setDuration(request.getDuration());
        record.setAudioType(request.getAudioType());
        record.setCompleted(request.getCompleted());
        record.setNote(request.getNote());
        
        SmokingRecord savedRecord = smokingRecordRepository.save(record);
        log.info("训练记录创建成功，ID: {}", savedRecord.getId());
        
        return TrainingRecordResponse.fromEntity(savedRecord);
    }
    
    @Override
    public List<SmokingRecordResponse> getUserSmokingRecords(Long userId) {
        log.info("获取用户吸烟记录列表，用户ID: {}", userId);
        
        List<SmokingRecord> records = smokingRecordRepository
                .findByUserIdAndRecordTypeOrderByTimestampDesc(userId, RecordType.SMOKING);
        
        return records.stream()
                .map(SmokingRecordResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<SmokingRecordResponse> getUserSmokingRecords(Long userId, Pageable pageable) {
        log.info("分页获取用户吸烟记录，用户ID: {}, 页码: {}, 大小: {}", 
                userId, pageable.getPageNumber(), pageable.getPageSize());

        Page<SmokingRecord> recordPage = smokingRecordRepository
                .findByUserIdAndRecordType(userId, RecordType.SMOKING, pageable);

        return recordPage.map(SmokingRecordResponse::fromEntity);
    }
    
    @Override
    public List<SmokingRecordResponse> getUserSmokingRecordsByDate(Long userId, LocalDate date) {
        log.info("获取用户指定日期吸烟记录，用户ID: {}, 日期: {}", userId, date);

        List<SmokingRecord> records = smokingRecordRepository
                .findByUserIdAndRecordTypeAndCreatedAtBetween(
                        userId, RecordType.SMOKING, date, date.plusDays(1));

        return records.stream()
                .map(SmokingRecordResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TrainingRecordResponse> getUserTrainingRecords(Long userId) {
        log.info("获取用户训练记录列表，用户ID: {}", userId);

        List<SmokingRecord> records = smokingRecordRepository
                .findByUserIdAndRecordTypeOrderByTimestampDesc(userId, RecordType.TRAINING);

        return records.stream()
                .map(TrainingRecordResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<TrainingRecordResponse> getUserTrainingRecords(Long userId, Pageable pageable) {
        log.info("分页获取用户训练记录，用户ID: {}, 页码: {}, 大小: {}", 
                userId, pageable.getPageNumber(), pageable.getPageSize());

        Page<SmokingRecord> recordPage = smokingRecordRepository
                .findByUserIdAndRecordType(userId, RecordType.TRAINING, pageable);

        return recordPage.map(TrainingRecordResponse::fromEntity);
    }
    
    @Override
    public List<TrainingRecordResponse> getUserTrainingRecordsByDate(Long userId, LocalDate date) {
        log.info("获取用户指定日期训练记录，用户ID: {}, 日期: {}", userId, date);

        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(LocalTime.MAX);

        List<SmokingRecord> records = smokingRecordRepository
                .findByUserIdAndRecordTypeAndCreatedAtBetween(
                        userId, RecordType.TRAINING, date, date.plusDays(1));

        return records.stream()
                .map(TrainingRecordResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public Long getTodaySmokingCount(Long userId) {
        log.info("统计用户今日吸烟次数，用户ID: {}", userId);
        
        try {
            Long count = smokingRecordRepository.countTodaySmokingRecordsByUserId(userId);
            log.info("用户 {} 今日吸烟次数: {}", userId, count);
            return count;
        } catch (Exception e) {
            log.error("统计用户 {} 今日吸烟次数异常", userId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "统计今日吸烟次数失败");
        }
    }
    
    @Override
    public Long getTodayTrainingCount(Long userId) {
        log.info("统计用户今日训练次数，用户ID: {}", userId);
        
        try {
            Long count = smokingRecordRepository.countTodayTrainingRecordsByUserId(userId);
            log.info("用户 {} 今日训练次数: {}", userId, count);
            return count;
        } catch (Exception e) {
            log.error("统计用户 {} 今日训练次数异常", userId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "统计今日训练次数失败");
        }
    }
    
    @Override
    public SmokingRecordResponse getLastSmokingRecord(Long userId) {
        log.info("获取用户最后一次吸烟记录，用户ID: {}", userId);

        try {
            SmokingRecord lastRecord = smokingRecordRepository
                    .findFirstByUserIdAndRecordTypeOrderByTimestampDesc(userId, RecordType.SMOKING);

            if (lastRecord == null) {
                log.info("用户 {} 暂无吸烟记录", userId);
                return null;
            }

            log.info("找到用户 {} 最后一次吸烟记录，时间: {}, 支数: {}", 
                    userId, lastRecord.getTimestamp(), lastRecord.getCigaretteCount());
            
            return SmokingRecordResponse.fromEntity(lastRecord);
            
        } catch (Exception e) {
            log.error("获取用户 {} 最后一次吸烟记录异常", userId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "获取吸烟记录失败");
        }
    }
    
    @Override
    public TrainingRecordResponse getLastTrainingRecord(Long userId) {
        log.info("获取用户最后一次训练记录，用户ID: {}", userId);

        try {
            SmokingRecord lastRecord = smokingRecordRepository
                    .findFirstByUserIdAndRecordTypeOrderByTimestampDesc(userId, RecordType.TRAINING);

            if (lastRecord == null) {
                log.info("用户 {} 暂无训练记录", userId);
                return null;
            }

            log.info("找到用户 {} 最后一次训练记录，时间: {}, 时长: {}秒", 
                    userId, lastRecord.getTimestamp(), lastRecord.getDuration());
            
            return TrainingRecordResponse.fromEntity(lastRecord);
            
        } catch (Exception e) {
            log.error("获取用户 {} 最后一次训练记录异常", userId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "获取训练记录失败");
        }
    }
    
    @Override
    @Transactional
    public void deleteSmokingRecord(Long userId, Long recordId) {
        log.info("删除吸烟记录，用户ID: {}, 记录ID: {}", userId, recordId);
        
        SmokingRecord record = smokingRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "记录不存在"));
        
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "无权限删除此记录");
        }
        
        if (record.getRecordType() != RecordType.SMOKING) {
            throw new BusinessException(ErrorCode.BAD_REQUEST.getCode(), "记录类型不匹配");
        }
        
        smokingRecordRepository.delete(record);
        log.info("吸烟记录删除成功，ID: {}", recordId);
    }
    
    @Override
    @Transactional
    public void deleteTrainingRecord(Long userId, Long recordId) {
        log.info("删除训练记录，用户ID: {}, 记录ID: {}", userId, recordId);
        
        SmokingRecord record = smokingRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "记录不存在"));
        
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "无权限删除此记录");
        }
        
        if (record.getRecordType() != RecordType.TRAINING) {
            throw new BusinessException(ErrorCode.BAD_REQUEST.getCode(), "记录类型不匹配");
        }
        
        smokingRecordRepository.delete(record);
        log.info("训练记录删除成功，ID: {}", recordId);
    }
} 