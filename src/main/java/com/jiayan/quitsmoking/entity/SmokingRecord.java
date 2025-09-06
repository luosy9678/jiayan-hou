package com.jiayan.quitsmoking.entity;

import com.jiayan.quitsmoking.enums.RecordType;
import com.jiayan.quitsmoking.util.TimeZoneUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "smoking_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmokingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "record_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecordType recordType = RecordType.SMOKING;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // 吸烟记录字段
    @Column(name = "cigarette_count")
    private Integer cigaretteCount = 1;

    // 训练记录字段
    @Column(name = "duration")
    private Integer duration; // 训练时长（秒）

    @Column(name = "audio_type", length = 50)
    private String audioType; // 音频类型

    @Column(name = "completed")
    private Boolean completed; // 是否完成

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = TimeZoneUtil.getCurrentBeijingTime(); // 使用北京时间
        this.updatedAt = TimeZoneUtil.getCurrentBeijingTime(); // 使用北京时间
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = TimeZoneUtil.getCurrentBeijingTime(); // 使用北京时间
    }
} 