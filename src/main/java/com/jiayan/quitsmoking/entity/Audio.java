package com.jiayan.quitsmoking.entity;

import com.jiayan.quitsmoking.enums.QuitMode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 音频实体类
 */
@Entity
@Table(name = "audios")
@Data
@EqualsAndHashCode(callSuper = false)
public class Audio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "voice_type", length = 50)
    private String voiceType;

    @Column(name = "user_id", nullable = true)
    private Long userId;

    @Column(name = "is_disabled", nullable = false)
    private Boolean isDisabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "quit_mode", length = 20)
    private QuitMode quitMode = QuitMode.BOTH;

    @Column(name = "is_premium_only", nullable = false)
    private Boolean isPremiumOnly = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
} 