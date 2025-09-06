package com.jiayan.quitsmoking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTrainingRecordRequest {

    @NotNull(message = "训练时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    @NotNull(message = "训练时长不能为空")
    @Min(value = 1, message = "训练时长必须大于0秒")
    private Integer duration; // 训练时长（秒）

    @Size(max = 50, message = "音频类型长度不能超过50个字符")
    private String audioType; // 音频类型，如 "female1", "male1"

    @NotNull(message = "完成状态不能为空")
    private Boolean completed; // 是否完成训练

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String note; // 备注
} 