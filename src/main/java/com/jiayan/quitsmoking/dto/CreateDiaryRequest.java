package com.jiayan.quitsmoking.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import java.util.List;

/**
 * 创建日记请求DTO
 */
@Data
public class CreateDiaryRequest {
    
    @NotBlank(message = "日记内容不能为空")
    @Size(max = 1000, message = "日记内容不能超过1000字符")
    private String content;
    
    @Size(max = 5, message = "标签数量不能超过5个")
    private List<String> tags;
    
    @Size(max = 6, message = "图片数量不能超过6张")
    private List<DiaryImage> images;
    
    @Max(value = 5, message = "心情评分不能超过5")
    private Integer moodScore;
    
    private String status = "草稿"; // 默认草稿状态
    
    /**
     * 日记图片DTO
     */
    @Data
    public static class DiaryImage {
        private String fileName;
        private String fileUrl;
        private Long fileSize;
    }
} 