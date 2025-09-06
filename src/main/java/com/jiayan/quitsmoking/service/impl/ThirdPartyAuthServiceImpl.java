package com.jiayan.quitsmoking.service.impl;

import com.jiayan.quitsmoking.dto.*;
import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.enums.ErrorCode;
import com.jiayan.quitsmoking.exception.BusinessException;
import com.jiayan.quitsmoking.repository.UserRepository;
import com.jiayan.quitsmoking.service.AvatarService;
import com.jiayan.quitsmoking.service.ThirdPartyAuthService;
import com.jiayan.quitsmoking.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThirdPartyAuthServiceImpl implements ThirdPartyAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AvatarService avatarService;

    @Override
    public WechatAccessTokenResponse getWechatAccessToken(String code) {
        log.info("获取微信Access Token，授权码: {}", code);
        
        // 模拟调用微信API获取Access Token
        // 在真实环境中，这里需要调用微信API：
        // https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        
        // 模拟返回数据（测试用）
        String mockOpenid = "mock_openid_" + code;
        String mockAccessToken = "mock_access_token_" + System.currentTimeMillis();
        
        return new WechatAccessTokenResponse(
            mockAccessToken,
            7200,
            "mock_refresh_token",
            mockOpenid,
            "snsapi_login"
        );
    }

    @Override
    public WechatUserInfoResponse getWechatUserInfo(String accessToken, String openid) {
        log.info("获取微信用户信息，accessToken: {}, openid: {}", accessToken, openid);
        
        // 模拟调用微信API获取用户信息
        // 在真实环境中，这里需要调用微信API：
        // https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
        
        // 模拟返回数据（测试用）
        return new WechatUserInfoResponse(
            openid,
            "微信用户_" + openid.substring(openid.length() - 6),
            1, // 1-男性，2-女性
            "广东",
            "深圳",
            "中国",
            "https://thirdwx.qlogo.cn/mmopen/mock_avatar.jpg",
            "mock_unionid_" + openid
        );
    }

    @Override
    @Transactional
    public AuthResponse thirdPartyBind(ThirdPartyBindRequest request) {
        log.info("第三方登录绑定，平台: {}, openid: {}", request.getPlatform(), request.getOpenid());

        // 根据openid查找用户
        Optional<User> existingUser = userRepository.findByOpenid(request.getOpenid());
        
        User user;
        boolean isNewUser = false;
        
        if (existingUser.isPresent()) {
            // 用户已存在，直接登录
            user = existingUser.get();
            log.info("用户已存在，直接登录: {}", user.getId());
        } else {
            // 新用户，创建账号
            user = createUserFromThirdParty(request);
            isNewUser = true;
            log.info("创建新用户: {}", user.getId());
            
            // 如果有头像URL，自动下载并保存头像
            if (request.getAvatar() != null && !request.getAvatar().trim().isEmpty()) {
                try {
                    String avatarFileName = avatarService.downloadAvatar(user.getId(), new java.net.URL(request.getAvatar()));
                    log.info("第三方登录自动下载头像成功: userId={}, fileName={}", user.getId(), avatarFileName);
                } catch (Exception e) {
                    log.warn("第三方登录下载头像失败: userId={}, avatarUrl={}, error={}", 
                            user.getId(), request.getAvatar(), e.getMessage());
                }
            }
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId().toString());
        
        // 构建响应
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.fromEntity(user);

        return AuthResponse.builder()
            .userId(user.getId().toString())
            .token(token)
            .expiresIn(7200)
            .user(userInfo)
            .build();
    }

    private User createUserFromThirdParty(ThirdPartyBindRequest request) {
        User user = new User();
        
        // 基础信息
        user.setNickname(request.getNickname() != null ? request.getNickname() : "用户" + System.currentTimeMillis());
        user.setAvatar(request.getAvatar());
        user.setLoginType(request.getPlatform());
        user.setOpenid(request.getOpenid());
        user.setUnionid(request.getUnionid());
        
        // 为第三方登录用户生成虚拟手机号（基于openid生成唯一手机号）
        user.setPhone("third_" + Math.abs(request.getOpenid().hashCode()) % 100000000L);
        
        // 设置默认密码（第三方登录用户可能没有密码）
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        
        // 设置地区信息
        if (request.getProvince() != null) {
            // 这里可以设置省市信息，目前User实体类中可能没有这些字段
        }
        
        // 设置默认值
        user.setEnabled(true);
        user.setDailyCigarettes(0);
        user.setPricePerPack(java.math.BigDecimal.ZERO);
        user.setCustomTrainingCount(500);
        user.setAudioPreference("female");
        user.setMemberLevel("basic");

        return userRepository.save(user);
    }
} 