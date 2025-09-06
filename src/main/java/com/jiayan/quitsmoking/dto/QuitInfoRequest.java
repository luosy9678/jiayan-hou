package com.jiayan.quitsmoking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 戒烟基础信息设置请求DTO
 */
@Data
public class QuitInfoRequest {
    
    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 120, message = "年龄不能超过120")
    private Integer age;
    
    @NotNull(message = "烟龄不能为空")
    @Min(value = 0, message = "烟龄不能为负数")
    @Max(value = 100, message = "烟龄不能超过100年")
    private Integer smokingYears;
    
    @NotNull(message = "原始每日吸烟量不能为空")
    @Min(value = 1, message = "原始每日吸烟量必须大于0")
    private Integer originalDailyCigarettes;
    
    @Size(max = 50, message = "香烟品牌名称过长")
    private String cigaretteBrand;
    
    @DecimalMin(value = "0.1", message = "焦油含量必须大于0")
    @DecimalMax(value = "99.9", message = "焦油含量不能超过99.9")
    private BigDecimal tarContent;
    
    @NotNull(message = "性别不能为空")
    @Pattern(regexp = "^(male|female|secret)$", message = "性别只能是male、female或secret")
    private String gender;
    
    @NotNull(message = "戒烟模式不能为空")
    @Pattern(regexp = "^(reduction|abstinence)$", message = "戒烟模式只能是reduction或abstinence")
    private String quitMode;
} 