package com.jiayan.quitsmoking.service;

import com.jiayan.quitsmoking.dto.AuthResponse;
import com.jiayan.quitsmoking.dto.LoginRequest;
import com.jiayan.quitsmoking.dto.RegisterRequest;
import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.dto.UserProfileResponse;
import com.jiayan.quitsmoking.dto.UpdateUserRequest;
import com.jiayan.quitsmoking.dto.QuitInfoRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    AuthResponse register(RegisterRequest request);

    /**
     * 跳过验证码的用户注册（测试用）
     */
    AuthResponse registerWithoutCode(RegisterRequest request);

    /**
     * 用户登录
     */
    AuthResponse login(LoginRequest request);

    /**
     * 发送验证码
     */
    void sendVerificationCode(String phone, String type);

    /**
     * 验证验证码
     */
    boolean verifyCode(String phone, String code);

    /**
     * 刷新Token
     */
    AuthResponse refreshToken(String refreshToken);

    /**
     * 根据ID获取用户
     */
    User getUserById(Long userId);

    /**
     * 根据手机号获取用户
     */
    User getUserByPhone(String phone);

    /**
     * 检查手机号是否存在
     */
    boolean isPhoneExists(String phone);

    /**
     * 检查邮箱是否存在
     */
    boolean isEmailExists(String email);

    /**
     * 获取用户档案信息
     */
    UserProfileResponse getUserProfile(Long userId);

    /**
     * 更新用户信息
     */
    UserProfileResponse updateUserProfile(Long userId, UpdateUserRequest request);

    /**
     * 设置戒烟目标
     */
    UserProfileResponse setQuitGoal(Long userId, Integer dailyCigarettes, BigDecimal pricePerPack, LocalDate quitStartDate);

    /**
     * 更新用户头像
     */
    UserProfileResponse updateAvatar(Long userId, String avatarUrl);

    /**
     * 设置戒烟基础信息
     */
    UserProfileResponse setQuitInfo(Long userId, QuitInfoRequest request);

    /**
     * 更新当前吸烟量
     */
    UserProfileResponse updateCurrentSmoking(Long userId, Integer currentDailyCigarettes);

    /**
     * 根据ID查找用户
     */
    User findById(Long userId);
} 