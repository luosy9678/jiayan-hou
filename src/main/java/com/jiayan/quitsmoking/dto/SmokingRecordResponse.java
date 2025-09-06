package com.jiayan.quitsmoking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiayan.quitsmoking.util.TimeZoneUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmokingRecordResponse {

    private String recordId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    private Integer cigaretteCount;

    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    public static SmokingRecordResponse fromEntity(com.jiayan.quitsmoking.entity.SmokingRecord record) {
        if (record == null) {
            return null;
        }
        
        return new SmokingRecordResponse(
            "rec_" + record.getId(),
            TimeZoneUtil.convertUTCToBeijing(record.getTimestamp()), // 将UTC时间转换为北京时间
            record.getCigaretteCount() != null ? record.getCigaretteCount() : 1,
            record.getNote(),
            record.getCreatedAt()
        );
    }
} 