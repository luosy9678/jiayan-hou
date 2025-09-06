package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import com.jiayan.quitsmoking.dto.UpdateUserRequest;
import com.jiayan.quitsmoking.dto.UserProfileResponse;
import com.jiayan.quitsmoking.dto.QuitInfoRequest;
import com.jiayan.quitsmoking.service.UserService;
import com.jiayan.quitsmoking.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用户信息管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 获取用户档案信息
     */
    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("获取用户档案: userId={}", userId);
        UserProfileResponse profile = userService.getUserProfile(Long.valueOf(userId));
        return ApiResponse.success("获取用户档案成功", profile);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateUserProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateUserRequest request) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("更新用户信息: userId={}", userId);
        UserProfileResponse updatedProfile = userService.updateUserProfile(Long.valueOf(userId), request);
        return ApiResponse.success("用户信息更新成功", updatedProfile);
    }

    /**
     * 设置戒烟目标
     */
    @PostMapping("/quit-goal")
    public ApiResponse<UserProfileResponse> setQuitGoal(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @Min(value = 1, message = "每日吸烟数量必须大于0") Integer dailyCigarettes,
            @RequestParam @DecimalMin(value = "0.01", message = "香烟价格必须大于0") BigDecimal pricePerPack,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate quitStartDate) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("设置戒烟目标: userId={}, dailyCigarettes={}, pricePerPack={}, quitStartDate={}", 
                userId, dailyCigarettes, pricePerPack, quitStartDate);
        
        UserProfileResponse profile = userService.setQuitGoal(Long.valueOf(userId), dailyCigarettes, pricePerPack, quitStartDate);
        return ApiResponse.success("戒烟目标设置成功", profile);
    }

    /**
     * 设置戒烟基础信息
     */
    @PostMapping("/quit-info")
    public ApiResponse<UserProfileResponse> setQuitInfo(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody QuitInfoRequest request) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("设置戒烟基础信息: userId={}, age={}, smokingYears={}, originalDailyCigarettes={}, gender={}, quitMode={}", 
                userId, request.getAge(), request.getSmokingYears(), request.getOriginalDailyCigarettes(), 
                request.getGender(), request.getQuitMode());
        
        UserProfileResponse profile = userService.setQuitInfo(Long.valueOf(userId), request);
        return ApiResponse.success("戒烟基础信息设置成功", profile);
    }

    /**
     * 更新当前吸烟量
     */
    @PostMapping("/current-smoking")
    public ApiResponse<UserProfileResponse> updateCurrentSmoking(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @Min(value = 1, message = "当前每日吸烟数量必须大于0") Integer currentDailyCigarettes) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("更新当前吸烟量: userId={}, currentDailyCigarettes={}", userId, currentDailyCigarettes);
        
        UserProfileResponse profile = userService.updateCurrentSmoking(Long.valueOf(userId), currentDailyCigarettes);
        return ApiResponse.success("当前吸烟量更新成功", profile);
    }

    /**
     * 更新用户头像（兼容旧接口，现在推荐使用 /avatar/upload 或 /avatar/download）
     */
    @PostMapping("/avatar")
    public ApiResponse<UserProfileResponse> updateAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String avatarUrl) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("更新用户头像: userId={}, avatarUrl={}", userId, avatarUrl);
        UserProfileResponse profile = userService.updateAvatar(Long.valueOf(userId), avatarUrl);
        return ApiResponse.success("头像更新成功", profile);
    }

    /**
     * 批量更新用户自定义信息（背景图、简介等）
     */
    @PostMapping("/profile-custom")
    public ApiResponse<UserProfileResponse> updateProfileCustom(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateUserRequest request) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("批量更新用户自定义信息: userId={}, request={}", userId, request);
        UserProfileResponse updatedProfile = userService.updateUserProfile(Long.valueOf(userId), request);
        return ApiResponse.success("用户自定义信息更新成功", updatedProfile);
    }

    /**
     * 更新用户简介
     */
    @PostMapping("/bio")
    public ApiResponse<UserProfileResponse> updateUserBio(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateUserRequest request) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("更新用户简介: userId={}, userBio={}", userId, request.getUserBio());
        
        UserProfileResponse updatedProfile = userService.updateUserProfile(Long.valueOf(userId), request);
        return ApiResponse.success("用户简介更新成功", updatedProfile);
    }

    /**
     * 更新用户背景图
     */
    @PostMapping("/background")
    public ApiResponse<UserProfileResponse> updateUserBackground(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateUserRequest request) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("更新用户背景图: userId={}, backgroundImage={}", userId, request.getBackgroundImage());
        
        UserProfileResponse updatedProfile = userService.updateUserProfile(Long.valueOf(userId), request);
        return ApiResponse.success("背景图更新成功", updatedProfile);
    }

    // ===== 简化的测试接口（使用GET方法，无需Token） =====

    /**
     * 获取用户档案信息（测试用）
     */
    @GetMapping("/profile-simple")
    public ApiResponse<UserProfileResponse> getUserProfileSimple(@RequestParam Long userId) {
        log.info("获取用户档案(测试): userId={}", userId);
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ApiResponse.success("获取用户档案成功", profile);
    }

    /**
     * 设置戒烟目标（测试用）
     */
    @GetMapping("/quit-goal-simple")
    public ApiResponse<UserProfileResponse> setQuitGoalSimple(
            @RequestParam Long userId,
            @RequestParam Integer dailyCigarettes,
            @RequestParam BigDecimal pricePerPack,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate quitStartDate) {
        
        log.info("设置戒烟目标(测试): userId={}, dailyCigarettes={}, pricePerPack={}, quitStartDate={}", 
                userId, dailyCigarettes, pricePerPack, quitStartDate);
        
        UserProfileResponse profile = userService.setQuitGoal(userId, dailyCigarettes, pricePerPack, quitStartDate);
        return ApiResponse.success("戒烟目标设置成功", profile);
    }

    /**
     * 更新用户昵称（测试用）
     */
    @GetMapping("/update-nickname")
    public ApiResponse<UserProfileResponse> updateNickname(
            @RequestParam Long userId,
            @RequestParam String nickname) {
        
        log.info("更新用户昵称(测试): userId={}, nickname={}", userId, nickname);
        
        UpdateUserRequest request = new UpdateUserRequest();
        request.setNickname(nickname);
        
        UserProfileResponse profile = userService.updateUserProfile(userId, request);
        return ApiResponse.success("昵称更新成功", profile);
    }

    /**
     * 测试更新背景图（测试用）
     */
    @GetMapping("/update-background")
    public ApiResponse<UserProfileResponse> updateBackground(
            @RequestParam Long userId,
            @RequestParam String backgroundImage) {
        
        log.info("更新背景图(测试): userId={}, backgroundImage={}", userId, backgroundImage);
        
        UpdateUserRequest request = new UpdateUserRequest();
        request.setBackgroundImage(backgroundImage);
        
        UserProfileResponse profile = userService.updateUserProfile(userId, request);
        return ApiResponse.success("背景图更新成功", profile);
    }

    /**
     * 从Authorization header中提取Token
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid authorization header");
    }
} 