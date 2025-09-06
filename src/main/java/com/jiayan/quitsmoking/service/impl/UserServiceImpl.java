package com.jiayan.quitsmoking.service.impl;

import com.jiayan.quitsmoking.dto.AuthResponse;
import com.jiayan.quitsmoking.dto.LoginRequest;
import com.jiayan.quitsmoking.dto.RegisterRequest;
import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.enums.ErrorCode;
import com.jiayan.quitsmoking.exception.BusinessException;
import com.jiayan.quitsmoking.repository.UserRepository;
import com.jiayan.quitsmoking.service.UserService;
import com.jiayan.quitsmoking.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import com.jiayan.quitsmoking.dto.UserProfileResponse;
import com.jiayan.quitsmoking.dto.UpdateUserRequest;
import com.jiayan.quitsmoking.dto.QuitInfoRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 临时存储验证码（生产环境应使用Redis）
    private static final ConcurrentHashMap<String, CodeInfo> CODE_CACHE = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("用户注册，手机号: {}", request.getPhone());
        
        // 检查手机号是否已存在
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS.getCode(), "手机号已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setMemberLevel("free");
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 生成Token
        String token = jwtUtil.generateToken(savedUser.getId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getId().toString());
        
        // 构建响应
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.fromEntity(savedUser);
        
        return AuthResponse.builder()
                .userId(savedUser.getId().toString())
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(7200)
                .user(userInfo)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerWithoutCode(RegisterRequest request) {
        // 跳过验证码验证，直接注册
        
        // 检查手机号是否已存在
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 创建用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setMemberLevel("basic");
        user.setEnabled(true);
        
        // 设置默认背景图和简介
        user.setBackgroundImage("/uploads/backgrounds/default-bg.jpg");
        user.setUserBio("这家伙很懒，什么都没有留下");

        User savedUser = userRepository.save(user);

        // 生成Token
        String token = jwtUtil.generateToken(savedUser.getId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getId().toString());

        return AuthResponse.builder()
                .userId(savedUser.getId().toString())
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(86400) // 24小时
                .user(AuthResponse.UserInfo.fromEntity(savedUser))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("用户登录，手机号: {}", request.getPhone());
        
        // 查找用户
        User userEntity = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在"));

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD.getCode(), "密码错误");
        }

        // 检查用户状态
        if (!userEntity.getEnabled()) {
            throw new BusinessException(ErrorCode.USER_DISABLED.getCode(), "用户已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(userEntity.getId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(userEntity.getId().toString());
        
        // 构建响应
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.fromEntity(userEntity);

        return AuthResponse.builder()
                .userId(userEntity.getId().toString())
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(7200)
                .user(userInfo)
                .build();
    }

    @Override
    public void sendVerificationCode(String phone, String type) {
        // 生成6位数字验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // 存储验证码（5分钟有效期）
        CODE_CACHE.put(phone, new CodeInfo(code, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)));
        
        // TODO: 这里应该调用短信服务发送验证码
        // 临时方案：在日志中输出验证码（仅用于测试）
        log.info("=== 验证码信息 ===");
        log.info("手机号: {}", phone);
        log.info("验证码: {}", code);
        log.info("类型: {}", type);
        log.info("有效期: 5分钟");
        log.info("==================");
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        CodeInfo codeInfo = CODE_CACHE.get(phone);
        if (codeInfo == null) {
            return false;
        }

        // 检查是否过期
        if (System.currentTimeMillis() > codeInfo.expireTime) {
            CODE_CACHE.remove(phone);
            return false;
        }

        // 验证码正确则删除
        if (codeInfo.code.equals(code)) {
            CODE_CACHE.remove(phone);
            return true;
        }

        return false;
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        // 验证刷新Token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        // 获取用户ID
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = getUserById(Long.valueOf(userId));

        // 生成新的Token
        String newToken = jwtUtil.generateToken(userId);

        return AuthResponse.builder()
                .userId(userId)
                .token(newToken)
                .refreshToken(refreshToken)
                .expiresIn(86400)
                .user(AuthResponse.UserInfo.fromEntity(user))
                .build();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .filter(u -> u.getEnabled())
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "用户不存在或已禁用"));
    }

    @Override
    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public boolean isPhoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        User user = getUserById(userId);
        return UserProfileResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UpdateUserRequest request) {
        User user = getUserById(userId);
        
        log.info("更新用户信息开始: userId={}, request={}", userId, request);
        
        // 更新昵称
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.setNickname(request.getNickname().trim());
            log.info("更新昵称: {}", request.getNickname());
        }
        
        // 更新邮箱（需要检查唯一性）
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
            log.info("更新邮箱: {}", request.getEmail());
        }
        
        // 更新戒烟相关信息 - 允许0值更新
        if (request.getDailyCigarettes() != null) {
            user.setDailyCigarettes(request.getDailyCigarettes());
            log.info("更新每日吸烟量: {} -> {}", user.getDailyCigarettes(), request.getDailyCigarettes());
        }
        
        if (request.getPricePerPack() != null) {
            user.setPricePerPack(request.getPricePerPack());
            log.info("更新每包价格: {} -> {}", user.getPricePerPack(), request.getPricePerPack());
        }
        
        if (request.getQuitStartDate() != null) {
            user.setQuitStartDate(request.getQuitStartDate());
            log.info("更新戒烟开始日期: {}", request.getQuitStartDate());
        }
        
        // 更新语音偏好
        if (request.getAudioPreference() != null && 
            (request.getAudioPreference().equals("male") || request.getAudioPreference().equals("female"))) {
            user.setAudioPreference(request.getAudioPreference());
            log.info("更新语音偏好: {}", request.getAudioPreference());
        }
        
        // 更新头像
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
            log.info("更新头像: {}", request.getAvatar());
        }

        // 更新新增字段 - 允许0值更新
        if (request.getAge() != null) {
            user.setAge(request.getAge());
            log.info("更新年龄: {} -> {}", user.getAge(), request.getAge());
        }
        
        if (request.getSmokingYears() != null) {
            user.setSmokingYears(request.getSmokingYears());
            log.info("更新烟龄: {} -> {}", user.getSmokingYears(), request.getSmokingYears());
        }
        
        if (request.getCurrentDailyCigarettes() != null) {
            user.setCurrentDailyCigarettes(request.getCurrentDailyCigarettes());
            log.info("更新当前每日吸烟量: {} -> {}", user.getCurrentDailyCigarettes(), request.getCurrentDailyCigarettes());
        }
        
        if (request.getCigaretteBrand() != null) {
            user.setCigaretteBrand(request.getCigaretteBrand());
            log.info("更新香烟品牌: {} -> {}", user.getCigaretteBrand(), request.getCigaretteBrand());
        }
        
        if (request.getTarContent() != null) {
            user.setTarContent(request.getTarContent());
            log.info("更新焦油含量: {} -> {}", user.getTarContent(), request.getTarContent());
        }
        
        if (request.getGender() != null) {
            user.setGender(request.getGender());
            log.info("更新性别: {} -> {}", user.getGender(), request.getGender());
        }
        
        if (request.getQuitMode() != null) {
            user.setQuitMode(request.getQuitMode());
            log.info("更新戒烟模式: {} -> {}", user.getQuitMode(), request.getQuitMode());
        }
        
        if (request.getCustomTrainingCount() != null) {
            user.setCustomTrainingCount(request.getCustomTrainingCount());
            log.info("更新自定义训练次数: {} -> {}", user.getCustomTrainingCount(), request.getCustomTrainingCount());
        }
        
        // 新增背景图和简介字段更新
        if (request.getBackgroundImage() != null) {
            user.setBackgroundImage(request.getBackgroundImage());
            log.info("更新背景图: {} -> {}", user.getBackgroundImage(), request.getBackgroundImage());
        }
        
        if (request.getUserBio() != null) {
            user.setUserBio(request.getUserBio());
            log.info("更新用户简介: {} -> {}", user.getUserBio(), request.getUserBio());
        }
        
        User savedUser = userRepository.save(user);
        log.info("用户信息更新完成: userId={}", userId);
        return UserProfileResponse.fromEntity(savedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse setQuitGoal(Long userId, Integer dailyCigarettes, BigDecimal pricePerPack, LocalDate quitStartDate) {
        User user = getUserById(userId);
        
        if (dailyCigarettes != null && dailyCigarettes > 0) {
            user.setDailyCigarettes(dailyCigarettes);
        }
        
        if (pricePerPack != null && pricePerPack.compareTo(BigDecimal.ZERO) > 0) {
            user.setPricePerPack(pricePerPack);
        }
        
        if (quitStartDate != null) {
            user.setQuitStartDate(quitStartDate);
        }
        
        User savedUser = userRepository.save(user);
        return UserProfileResponse.fromEntity(savedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse updateAvatar(Long userId, String avatarUrl) {
        User user = getUserById(userId);
        user.setAvatar(avatarUrl);
        User savedUser = userRepository.save(user);
        return UserProfileResponse.fromEntity(savedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse setQuitInfo(Long userId, QuitInfoRequest request) {
        User user = getUserById(userId);
        
        // 设置戒烟基础信息
        user.setAge(request.getAge());
        user.setSmokingYears(request.getSmokingYears());
        user.setOriginalDailyCigarettes(request.getOriginalDailyCigarettes());
        user.setCigaretteBrand(request.getCigaretteBrand());
        user.setTarContent(request.getTarContent());
        user.setGender(request.getGender());
        user.setQuitMode(request.getQuitMode());
        
        // 如果是减量模式，初始当前吸烟量等于原始吸烟量
        if ("reduction".equals(request.getQuitMode())) {
            user.setCurrentDailyCigarettes(request.getOriginalDailyCigarettes());
        }
        
        User savedUser = userRepository.save(user);
        return UserProfileResponse.fromEntity(savedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse updateCurrentSmoking(Long userId, Integer currentDailyCigarettes) {
        User user = getUserById(userId);
        
        if (currentDailyCigarettes == null || currentDailyCigarettes < 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST.getCode(), "当前每日吸烟量不能为空或负数");
        }
        
        // 检查是否为减量模式
        if (!"reduction".equals(user.getQuitMode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST.getCode(), "只有减量模式才能更新当前吸烟量");
        }
        
        // 检查是否超过原始吸烟量
        if (user.getOriginalDailyCigarettes() != null && 
            currentDailyCigarettes > user.getOriginalDailyCigarettes()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST.getCode(), "当前吸烟量不能超过原始吸烟量");
        }
        
        user.setCurrentDailyCigarettes(currentDailyCigarettes);
        User savedUser = userRepository.save(user);
        return UserProfileResponse.fromEntity(savedUser);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在"));
    }

    /**
     * 验证码信息类
     */
    private static class CodeInfo {
        String code;
        long expireTime;

        CodeInfo(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }
} 