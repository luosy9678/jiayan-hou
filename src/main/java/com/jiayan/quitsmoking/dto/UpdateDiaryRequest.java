package com.jiayan.quitsmoking.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import java.util.List;

/**
 * 更新日记请求DTO
 */
@Data
public class UpdateDiaryRequest {
    
    @Size(max = 1000, message = "日记内容不能超过1000字符")
    private String content;
    
    @Size(max = 5, message = "标签数量不能超过5个")
    private List<String> tags;
    
    @Size(max = 6, message = "图片数量不能超过6张")
    private List<CreateDiaryRequest.DiaryImage> images;
    
    @Max(value = 5, message = "心情评分不能超过5")
    private Integer moodScore;
    
    private String status;
} 