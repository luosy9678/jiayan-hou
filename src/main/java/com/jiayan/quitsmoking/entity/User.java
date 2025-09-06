package com.jiayan.quitsmoking.entity;

import com.jiayan.quitsmoking.enums.PostPermissionLevel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = false)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "phone", length = 20, unique = true)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "background_image", length = 500)
    private String backgroundImage;

    @Column(name = "user_bio", length = 500)
    private String userBio;

    @Column(name = "enabled")
    private Boolean enabled = true;

    @Column(name = "audio_preference", length = 100)
    private String audioPreference;

    @Column(name = "login_type", length = 50)
    private String loginType;

    @Column(name = "openid", length = 100)
    private String openid;

    @Column(name = "unionid", length = 100)
    private String unionid;

    @Column(name = "age")
    private Integer age;

    @Column(name = "daily_cigarettes")
    private Integer dailyCigarettes;

    @Column(name = "current_daily_cigarettes")
    private Integer currentDailyCigarettes;

    @Column(name = "original_daily_cigarettes")
    private Integer originalDailyCigarettes;

    @Column(name = "smoking_years")
    private Integer smokingYears;

    @Column(name = "cigarette_brand", length = 100)
    private String cigaretteBrand;

    @Column(name = "price_per_pack", precision = 10, scale = 2)
    private BigDecimal pricePerPack;

    @Column(name = "tar_content", precision = 3, scale = 1)
    private BigDecimal tarContent;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "quit_mode", length = 50)
    private String quitMode;

    @Column(name = "quit_date")
    private LocalDate quitDate;

    @Column(name = "quit_days")
    private Integer quitDays;

    @Column(name = "saved_money", precision = 10, scale = 2)
    private BigDecimal savedMoney;

    @Column(name = "saved_cigarettes")
    private Integer savedCigarettes;

    @Column(name = "is_premium_member")
    private Boolean isPremiumMember = false;

    @Column(name = "reduced_cigarettes")
    private Integer reducedCigarettes;

    @Column(name = "health_improvement", length = 500)
    private String healthImprovement;

    @Column(name = "custom_training_count")
    private Integer customTrainingCount = 500;

    @Column(name = "quit_start_date")
    private LocalDate quitStartDate;

    @Column(name = "member_level")
    private String memberLevel = "free";

    @Column(name = "member_expire_date")
    private LocalDateTime memberExpireDate;

    // ========== 论坛权限相关字段 ==========
    
    @Column(name = "can_create_posts")
    private Boolean canCreatePosts = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_permission_level", length = 20)
    private PostPermissionLevel postPermissionLevel = PostPermissionLevel.LIMITED;

    @Column(name = "post_permission_granted_by")
    private Long postPermissionGrantedBy;

    @Column(name = "post_permission_granted_at")
    private LocalDateTime postPermissionGrantedAt;

    @Column(name = "post_permission_expires_at")
    private LocalDateTime postPermissionExpiresAt;

    // ========== 用户禁言相关字段 ==========
    
    @Column(name = "forum_banned")
    private Boolean forumBanned = false;

    @Column(name = "ban_reason", length = 200)
    private String banReason;

    @Column(name = "ban_start_time")
    private LocalDateTime banStartTime;

    @Column(name = "ban_end_time")
    private LocalDateTime banEndTime;

    @Column(name = "ban_count")
    private Integer banCount = 0;

    @Column(name = "last_ban_time")
    private LocalDateTime lastBanTime;

    @Column(name = "warning_count")
    private Integer warningCount = 0;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== 业务方法 ==========
    
    /**
     * 检查是否被禁言
     */
    public boolean isCurrentlyBanned() {
        if (!forumBanned) {
            return false;
        }
        
        // 检查是否在禁言期间
        if (banEndTime != null && LocalDateTime.now().isAfter(banEndTime)) {
            return false; // 禁言已过期
        }
        
        return true;
    }
    
    /**
     * 检查是否有发帖权限
     */
    public boolean hasPostPermission() {
        if (!canCreatePosts) {
            return false;
        }
        
        // 检查权限是否过期
        if (postPermissionExpiresAt != null && LocalDateTime.now().isAfter(postPermissionExpiresAt)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 检查会员等级是否满足要求
     */
    public boolean meetsMemberRequirement(String requiredLevel) {
        if ("free".equals(requiredLevel)) {
            return true;
        }
        
        if ("member".equals(requiredLevel)) {
            return "member".equals(memberLevel) || isPremiumMember;
        }
        
        if ("premium".equals(requiredLevel)) {
            return isPremiumMember;
        }
        
        return false;
    }
}