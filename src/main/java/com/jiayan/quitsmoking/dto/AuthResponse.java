package com.jiayan.quitsmoking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiayan.quitsmoking.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 认证响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String userId;
    private String token;
    private String refreshToken;
    private Integer expiresIn;
    private UserInfo user;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String id;
        private String nickname;
        private String phone;
        private String email;
        private String avatar;
        private String backgroundImage;
        private String userBio;
        private Integer dailyCigarettes;
        private Double pricePerPack;
        private LocalDate quitStartDate;
        private String memberLevel;
        private LocalDateTime memberExpireDate;
        private LocalDateTime createdAt;
        
        public static UserInfo fromEntity(User user) {
            return UserInfo.builder()
                    .id(user.getId().toString())
                    .nickname(user.getNickname())
                    .phone(user.getPhone())
                    .email(user.getEmail())
                    .avatar(user.getAvatar())
                    .backgroundImage(user.getBackgroundImage())
                    .userBio(user.getUserBio())
                    .dailyCigarettes(user.getDailyCigarettes())
                    .pricePerPack(user.getPricePerPack() != null ? user.getPricePerPack().doubleValue() : null)
                    .quitStartDate(user.getQuitStartDate())
                    .memberLevel(user.getMemberLevel() != null ? user.getMemberLevel().toString() : null)
                    .memberExpireDate(user.getMemberExpireDate())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }
}