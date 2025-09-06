package com.jiayan.quitsmoking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CreateSmokingRecordRequest {

    @NotNull(message = "吸烟时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    @Min(value = 1, message = "吸烟支数必须大于0")
    private Integer cigaretteCount = 1;

    @Size(max = 500, message = "备注不能超过500个字符")
    private String note;
} 