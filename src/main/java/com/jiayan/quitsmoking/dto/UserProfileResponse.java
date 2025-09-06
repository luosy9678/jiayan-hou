package com.jiayan.quitsmoking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiayan.quitsmoking.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 用户资料响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    
    private String id;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private String backgroundImage;
    private String userBio;
    private Integer dailyCigarettes;
    private BigDecimal pricePerPack;
    private Integer smokingYears;
    private BigDecimal tarContent;
    private LocalDate quitStartDate;
    private String memberLevel;
    private LocalDateTime memberExpireDate;
    private Integer customTrainingCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 新增字段
    private Integer age;
    
    // 计算字段
    private Long quitDays;
    private BigDecimal savedMoney;
    private Integer savedCigarettes;
    private Boolean isPremiumMember;
    
    // 文档中声明但缺失的字段
    private String audioPreference;
    private String loginType;
    private Integer currentDailyCigarettes;
    private Integer originalDailyCigarettes;
    private String cigaretteBrand;
    private String gender;
    private String quitMode;
    private Integer reducedCigarettes;
    private String healthImprovement;
    
    // 文本显示字段
    private String quitModeText;
    private String genderText;
    
    public static UserProfileResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId().toString());
        response.setNickname(user.getNickname());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setBackgroundImage(user.getBackgroundImage());
        response.setUserBio(user.getUserBio());
        response.setDailyCigarettes(user.getDailyCigarettes());
        response.setSmokingYears(user.getSmokingYears());
        response.setPricePerPack(user.getPricePerPack());
        response.setTarContent(user.getTarContent());
        response.setCustomTrainingCount(user.getCustomTrainingCount());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        // 新增字段
        response.setAge(user.getAge());
        
        // 文档中声明但缺失的字段
        response.setAudioPreference(user.getAudioPreference());
        response.setLoginType(user.getLoginType());
        response.setCurrentDailyCigarettes(user.getCurrentDailyCigarettes());
        response.setOriginalDailyCigarettes(user.getOriginalDailyCigarettes());
        response.setCigaretteBrand(user.getCigaretteBrand());
        response.setGender(user.getGender());
        response.setQuitMode(user.getQuitMode());
        response.setReducedCigarettes(user.getReducedCigarettes());
        response.setHealthImprovement(user.getHealthImprovement());
        
        // 计算字段
        response.setQuitStartDate(user.getQuitStartDate());
        response.setMemberLevel(user.getMemberLevel());
        response.setMemberExpireDate(user.getMemberExpireDate());
        response.setIsPremiumMember(user.getIsPremiumMember());
        
        // 计算戒烟天数
        if (user.getQuitStartDate() != null) {
            long quitDays = ChronoUnit.DAYS.between(user.getQuitStartDate(), LocalDate.now());
            response.setQuitDays(quitDays);
        }
        
        // 计算节省金额和香烟数
        if (user.getDailyCigarettes() != null && user.getPricePerPack() != null && user.getQuitStartDate() != null) {
            long quitDays = ChronoUnit.DAYS.between(user.getQuitStartDate(), LocalDate.now());
            if (quitDays > 0) {
                // 计算节省的香烟数
                int savedCigarettes = user.getDailyCigarettes() * (int) quitDays;
                response.setSavedCigarettes(savedCigarettes);
                
                // 计算节省金额（假设每包20支）
                BigDecimal cigarettesPerPack = new BigDecimal("20");
                BigDecimal savedPacks = new BigDecimal(savedCigarettes).divide(cigarettesPerPack, 2, RoundingMode.HALF_UP);
                BigDecimal savedMoney = savedPacks.multiply(user.getPricePerPack());
                response.setSavedMoney(savedMoney);
            }
        }
        
        // 计算减少的香烟数
        if (user.getOriginalDailyCigarettes() != null && user.getCurrentDailyCigarettes() != null) {
            int reduced = user.getOriginalDailyCigarettes() - user.getCurrentDailyCigarettes();
            if (reduced > 0) {
                response.setReducedCigarettes(reduced);
            }
        }
        
        // 设置文本显示字段
        response.setQuitModeText(getQuitModeText(user.getQuitMode()));
        response.setGenderText(getGenderText(user.getGender()));
        
        return response;
    }
    
    /**
     * 获取戒烟模式文本
     */
    private static String getQuitModeText(String quitMode) {
        if (quitMode == null) return null;
        switch (quitMode.toLowerCase()) {
            case "reduction":
                return "减量模式";
            case "abstinence":
                return "戒断模式";
            case "both":
                return "混合模式";
            default:
                return quitMode;
        }
    }
    
    /**
     * 获取性别文本
     */
    private static String getGenderText(String gender) {
        if (gender == null) return null;
        switch (gender.toLowerCase()) {
            case "male":
                return "男";
            case "female":
                return "女";
            case "secret":
                return "保密";
            default:
                return gender;
        }
    }

    /**
     * 构建头像完整URL
     */
    private static String buildAvatarUrl(String avatarFileName) {
        if (avatarFileName == null || avatarFileName.trim().isEmpty()) {
            return null;
        }
        
        // 如果已经是完整URL，直接返回
        if (avatarFileName.startsWith("http://") || avatarFileName.startsWith("https://")) {
            return avatarFileName;
        }
        
        // 构建本地头像URL
        return "/api/v1/avatar/" + avatarFileName;
    }

    /**
     * 构建背景图完整URL
     */
    private static String buildBackgroundImageUrl(String backgroundImageFileName) {
        if (backgroundImageFileName == null || backgroundImageFileName.trim().isEmpty()) {
            return null;
        }
        
        // 如果已经是完整URL，直接返回
        if (backgroundImageFileName.startsWith("http://") || backgroundImageFileName.startsWith("https://")) {
            return backgroundImageFileName;
        }
        
        // 构建本地背景图URL
        return "/api/v1/background/" + backgroundImageFileName;
    }
} 