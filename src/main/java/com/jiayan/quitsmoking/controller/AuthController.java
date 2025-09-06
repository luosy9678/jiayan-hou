package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import com.jiayan.quitsmoking.dto.AuthResponse;
import com.jiayan.quitsmoking.dto.LoginRequest;
import com.jiayan.quitsmoking.dto.RegisterRequest;
import com.jiayan.quitsmoking.dto.TestLoginRequest;
import com.jiayan.quitsmoking.dto.UserProfileResponse;
import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.enums.ErrorCode;  // 修正导入路径
import com.jiayan.quitsmoking.exception.BusinessException;
import com.jiayan.quitsmoking.service.UserService;
import com.jiayan.quitsmoking.service.ThirdPartyAuthService;
import com.jiayan.quitsmoking.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ThirdPartyAuthService thirdPartyAuthService;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册请求: {}", request.getPhone());
        AuthResponse response = userService.register(request);
        return ApiResponse.success("注册成功", response);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        
        try {
            log.info("用户登录，手机号: {}", request.getPhone()); // 改为 getPhone()
            
            // 验证登录
            AuthResponse response = userService.login(request);
            
            log.info("用户登录成功，用户ID: {}", response.getUserId());
            return ApiResponse.success(response);
            
        } catch (BusinessException e) {
            log.warn("用户登录失败: {}", e.getMessage());
            return ApiResponse.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户登录异常", e);
            return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "登录失败，请稍后重试");
        }
    }

    /**
     * 发送验证码 - 支持GET和POST请求
     */
    @RequestMapping(value = "/send-code", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<Map<String, Object>> sendCode(
            @RequestParam @NotBlank(message = "手机号不能为空") 
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone,
            @RequestParam @NotBlank(message = "类型不能为空") String type) {
        
        log.info("发送验证码请求: 手机号={}, 类型={}", phone, type);
        userService.sendVerificationCode(phone, type);
        
        Map<String, Object> data = new HashMap<>();
        data.put("expiresIn", 300); // 5分钟过期
        
        return ApiResponse.success("验证码发送成功", data);
    }

    /**
     * 简单的GET方式发送验证码（测试用）
     */
    @GetMapping("/send-code-simple")
    public ApiResponse<Map<String, Object>> sendCodeSimple(
            @RequestParam String phone,
            @RequestParam String type) {
        
        log.info("发送验证码请求(简单版): 手机号={}, 类型={}", phone, type);
        userService.sendVerificationCode(phone, type);
        
        Map<String, Object> data = new HashMap<>();
        data.put("phone", phone);
        data.put("type", type);
        data.put("expiresIn", 300);
        data.put("message", "请查看控制台获取验证码");
        
        return ApiResponse.success("验证码发送成功", data);
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@RequestHeader("Authorization") String authHeader) {
        // 提取Token（去掉"Bearer "前缀）
        String refreshToken = authHeader;
        if (authHeader.startsWith("Bearer ")) {
            refreshToken = authHeader.substring(7);
        }
        
        log.info("刷新Token请求");
        AuthResponse response = userService.refreshToken(refreshToken);
        return ApiResponse.success("Token刷新成功", response);
    }

    /**
     * 验证码验证接口（用于测试）
     */
    @RequestMapping(value = "/verify-code", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<Boolean> verifyCode(
            @RequestParam @NotBlank(message = "手机号不能为空") String phone,
            @RequestParam @NotBlank(message = "验证码不能为空") String code) {
        
        boolean isValid = userService.verifyCode(phone, code);
        return ApiResponse.success("验证码验证" + (isValid ? "成功" : "失败"), isValid);
    }

    /**
     * 检查手机号是否存在
     */
    @GetMapping("/check-phone")
    public ApiResponse<Map<String, Object>> checkPhone(@RequestParam String phone) {
        boolean exists = userService.isPhoneExists(phone);
        Map<String, Object> data = new HashMap<>();
        data.put("exists", exists);
        return ApiResponse.success("检查完成", data);
    }

    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check-email")
    public ApiResponse<Map<String, Object>> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        Map<String, Object> data = new HashMap<>();
        data.put("exists", exists);
        return ApiResponse.success("检查完成", data);
    }

    /**
     * 简单的GET方式注册测试
     */
    @GetMapping("/register-simple")
    public ApiResponse<AuthResponse> registerSimple(
            @RequestParam String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false, defaultValue = "123456") String password,
            @RequestParam(required = false, defaultValue = "测试用户") String nickname,
            @RequestParam String code) {
        
        log.info("简单注册请求: 手机号={}, 验证码={}", phone, code);
        
        RegisterRequest request = new RegisterRequest();
        request.setPhone(phone);
        request.setEmail(email != null ? email : phone + "@example.com");
        request.setPassword(password);
        request.setNickname(nickname);
        request.setVerificationCode(code);
        
        AuthResponse response = userService.register(request);
        return ApiResponse.success("注册成功", response);
    }

    /**
     * 简单的GET方式登录测试
     */
    @GetMapping("/login-simple")
    public ApiResponse<AuthResponse> loginSimple(
            @RequestParam String account,
            @RequestParam(required = false, defaultValue = "123456") String password) {
        
        log.info("简单登录请求: 账号={}", account);
        
        LoginRequest request = new LoginRequest();
        request.setPhone(account);
        request.setPassword(password);
        
        AuthResponse response = userService.login(request);
        return ApiResponse.success("登录成功", response);
    }

    /**
     * 测试账号登录接口（仅用于开发测试）
     */
    @PostMapping("/test-login")
    public ApiResponse<AuthResponse> testLogin(@RequestBody TestLoginRequest request) {
        
        try {
            log.info("测试账号登录，用户ID: {}", request.getUserId());
            
            // 查找用户
            User user = userService.findById(request.getUserId());
            if (user == null) {
                return ApiResponse.error(ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在");
            }
            
            // 生成JWT Token
            String token = jwtUtil.generateToken(user.getId().toString());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString());
            
            // 构建用户信息
            AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.fromEntity(user);
            
            // 构建响应
            AuthResponse response = AuthResponse.builder()
                    .userId(user.getId().toString())
                    .token(token)
                    .refreshToken(refreshToken)
                    .expiresIn(7200)
                    .user(userInfo)
                    .build();
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("测试登录失败", e);
            return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "登录失败");
        }
    }

    /**
     * 通过用户ID获取用户信息（测试用）
     */
    @GetMapping("/test-user/{userId}")
    public ApiResponse<UserProfileResponse> getTestUser(@PathVariable Long userId) {
        
        try {
            log.info("获取测试用户信息，用户ID: {}", userId);
            
            User user = userService.findById(userId);
            if (user == null) {
                return ApiResponse.error(ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在");
            }
            
            UserProfileResponse response = UserProfileResponse.fromEntity(user);
            return ApiResponse.success("获取用户信息成功", response);
            
        } catch (Exception e) {
            log.error("获取测试用户信息异常", e);
            return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "获取用户信息失败");
        }
    }
}