package com.jiayan.quitsmoking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiayan.quitsmoking.entity.SmokingRecord;
import com.jiayan.quitsmoking.util.TimeZoneUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRecordResponse {

    private String recordId; // 记录ID，格式为 "rec_" + id

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp; // 训练时间

    private Integer duration; // 训练时长（秒）

    private String audioType; // 音频类型

    private Boolean completed; // 是否完成

    private String note; // 备注

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt; // 创建时间

    /**
     * 从 SmokingRecord 实体转换为 TrainingRecordResponse
     */
    public static TrainingRecordResponse fromEntity(SmokingRecord record) {
        if (record == null) {
            return null;
        }

        TrainingRecordResponse response = new TrainingRecordResponse();
        response.setRecordId("rec_" + record.getId());
        response.setTimestamp(TimeZoneUtil.convertUTCToBeijing(record.getTimestamp())); // 将UTC时间转换为北京时间
        response.setDuration(record.getDuration());
        response.setAudioType(record.getAudioType());
        response.setCompleted(record.getCompleted());
        response.setNote(record.getNote());
        response.setCreatedAt(record.getCreatedAt());

        return response;
    }
} 