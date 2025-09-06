package com.jiayan.quitsmoking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用户信息更新请求DTO
 */
@Data
public class UpdateUserRequest {

    @Size(min = 1, max = 50, message = "昵称长度必须在1-50位之间")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Min(value = 0, message = "每日吸烟数量不能为负数")
    private Integer dailyCigarettes;

    @DecimalMin(value = "0.0", message = "香烟价格不能为负数")
    private BigDecimal pricePerPack;

    private LocalDate quitStartDate;

    @Size(max = 20, message = "语音偏好设置过长")
    private String audioPreference; // "male" 或 "female"

    @Size(max = 500, message = "头像路径过长")
    private String avatar;

    // 新增字段
    @Min(value = 0, message = "年龄不能为负数")
    @Max(value = 120, message = "年龄不能超过120")
    private Integer age;

    @Min(value = 0, message = "烟龄不能为负数")
    @Max(value = 100, message = "烟龄不能超过100年")
    private Integer smokingYears;

    @Min(value = 0, message = "当前每日吸烟数量不能为负数")
    private Integer currentDailyCigarettes;

    @Size(max = 50, message = "香烟品牌名称过长")
    private String cigaretteBrand;

    @DecimalMin(value = "0.0", message = "焦油含量不能为负数")
    @DecimalMax(value = "99.9", message = "焦油含量不能超过99.9")
    private BigDecimal tarContent;

    @Pattern(regexp = "^(male|female|secret)$", message = "性别只能是male、female或secret")
    private String gender;

    @Pattern(regexp = "^(reduction|abstinence)$", message = "戒烟模式只能是reduction或abstinence")
    private String quitMode;

    @Min(value = 0, message = "自定义训练次数不能为负数")
    private Integer customTrainingCount;

    // 新增字段
    @Size(max = 255, message = "背景图路径过长")
    private String backgroundImage;

    @Size(max = 500, message = "用户简介过长")
    private String userBio;
} 