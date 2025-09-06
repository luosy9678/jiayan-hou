package com.jiayan.quitsmoking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiayan.quitsmoking.entity.Audio;
import com.jiayan.quitsmoking.enums.QuitMode;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 音频响应DTO
 */
@Data
public class AudioResponse {
    
    private Long id;
    private Boolean isPublic;
    private String description;
    private String fileName;
    private String voiceType;
    private Long userId;
    private Boolean isDisabled;
    private QuitMode quitMode;
    private Boolean isPremiumOnly;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;
    
    // 用户信息
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatar;
    }
    
    public static AudioResponse fromEntity(Audio audio) {
        AudioResponse response = new AudioResponse();
        response.setId(audio.getId());
        response.setIsPublic(audio.getIsPublic());
        response.setDescription(audio.getDescription());
        response.setFileName(audio.getFileName());
        response.setVoiceType(audio.getVoiceType());
        response.setUserId(audio.getUserId());
        response.setIsDisabled(audio.getIsDisabled());
        response.setQuitMode(audio.getQuitMode());
        response.setIsPremiumOnly(audio.getIsPremiumOnly());
        response.setCreatedAt(audio.getCreatedAt());
        response.setUpdatedAt(audio.getUpdatedAt());
        
        if (audio.getUser() != null) {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(audio.getUser().getId());
            userInfo.setNickname(audio.getUser().getNickname());
            userInfo.setAvatar(audio.getUser().getAvatar());
            response.setUser(userInfo);
        }
        
        return response;
    }
} 