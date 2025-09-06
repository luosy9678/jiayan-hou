package com.jiayan.quitsmoking.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jiayan.quitsmoking.enums.QuitMode;
import com.jiayan.quitsmoking.util.QuitModeDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 音频请求DTO
 */
@Data
public class AudioRequest {
    
    @NotNull(message = "公有标识不能为空")
    private Boolean isPublic;
    
    @Size(max = 500, message = "描述长度不能超过500字符")
    private String description;
    
    @NotBlank(message = "音频文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255字符")
    private String fileName;
    
    @Size(max = 50, message = "音色类型长度不能超过50字符")
    private String voiceType;
    
    @JsonDeserialize(using = QuitModeDeserializer.class)
    private QuitMode quitMode = QuitMode.BOTH;
    
    private Boolean isPremiumOnly = false;
} 