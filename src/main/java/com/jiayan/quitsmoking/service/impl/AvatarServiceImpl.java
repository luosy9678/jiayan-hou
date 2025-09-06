package com.jiayan.quitsmoking.service.impl;

import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.repository.UserRepository;
import com.jiayan.quitsmoking.service.AvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 头像服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private final UserRepository userRepository;
    
    // 头像存储目录
    private static final String AVATAR_DIR = "uploads/avatars";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    @Transactional
    public String uploadAvatar(Long userId, MultipartFile file) throws Exception {
        // 验证文件
        validateFile(file);
        
        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        
        // 删除旧头像
        deleteOldAvatar(user);
        
        // 生成新文件名
        String fileName = generateAvatarFileName(userId, file.getOriginalFilename());
        
        // 确保目录存在
        Path avatarDir = Paths.get(AVATAR_DIR);
        if (!Files.exists(avatarDir)) {
            Files.createDirectories(avatarDir);
        }
        
        // 保存文件
        Path filePath = avatarDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 更新用户头像字段
        user.setAvatar(fileName);
        userRepository.save(user);
        
        log.info("头像上传成功: userId={}, fileName={}", userId, fileName);
        return fileName;
    }

    @Override
    @Transactional
    public String downloadAvatar(Long userId, URL avatarUrl) throws Exception {
        log.info("开始下载头像: userId={}, url={}", userId, avatarUrl);
        
        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        
        // 删除旧头像
        deleteOldAvatar(user);
        
        // 从URL获取文件扩展名
        String urlPath = avatarUrl.getPath();
        log.info("URL路径: {}", urlPath);
        
        if (urlPath == null || urlPath.isEmpty()) {
            urlPath = avatarUrl.toString();
            log.info("使用完整URL作为路径: {}", urlPath);
        }
        
        String extension = getFileExtension(urlPath);
        log.info("提取的扩展名: {}", extension);
        
        if (!isValidExtension(extension)) {
            extension = ".jpg"; // 默认扩展名
            log.info("使用默认扩展名: {}", extension);
        }
        
        // 生成新文件名
        String fileName = generateAvatarFileName(userId, "avatar" + extension);
        log.info("生成的文件名: {}", fileName);
        
        // 确保目录存在
        Path avatarDir = Paths.get(AVATAR_DIR);
        if (!Files.exists(avatarDir)) {
            Files.createDirectories(avatarDir);
            log.info("创建头像目录: {}", avatarDir);
        }
        
        // 下载并保存文件
        Path filePath = avatarDir.resolve(fileName);
        log.info("文件保存路径: {}", filePath);
        
        try (var inputStream = avatarUrl.openStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("文件下载完成: {}", filePath);
        } catch (Exception e) {
            log.error("文件下载失败: url={}, error={}", avatarUrl, e.getMessage(), e);
            throw e;
        }
        
        // 更新用户头像字段
        user.setAvatar(fileName);
        userRepository.save(user);
        
        log.info("头像下载成功: userId={}, fileName={}, url={}", userId, fileName, avatarUrl);
        return fileName;
    }

    @Override
    public Resource getAvatarFile(String fileName) throws Exception {
        Path filePath = Paths.get(AVATAR_DIR).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("头像文件不存在或无法读取: " + fileName);
        }
    }

    @Override
    @Transactional
    public void deleteAvatar(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        
        deleteOldAvatar(user);
        
        // 清空用户头像字段
        user.setAvatar(null);
        userRepository.save(user);
        
        log.info("头像删除成功: userId={}", userId);
    }

    @Override
    public String generateAvatarFileName(Long userId, String originalFileName) {
        String extension = getFileExtension(originalFileName);
        if (!isValidExtension(extension)) {
            extension = ".jpg";
        }
        
        // 生成唯一文件名：用户ID_时间戳_UUID.扩展名
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("avatar_%d_%s_%s%s", userId, timestamp, uuid, extension);
    }

    /**
     * 验证上传的文件
     */
    private void validateFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }
        
        String originalFileName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        String extension = getFileExtension(originalFileName);
        if (!isValidExtension(extension)) {
            throw new IllegalArgumentException("不支持的文件格式，仅支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    /**
     * 删除用户旧头像文件
     */
    private void deleteOldAvatar(User user) throws IOException {
        if (StringUtils.hasText(user.getAvatar())) {
            Path oldAvatarPath = Paths.get(AVATAR_DIR).resolve(user.getAvatar());
            if (Files.exists(oldAvatarPath)) {
                Files.delete(oldAvatarPath);
                log.info("删除旧头像: {}", user.getAvatar());
            }
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex).toLowerCase();
        }
        return "";
    }

    /**
     * 验证文件扩展名是否有效
     */
    private boolean isValidExtension(String extension) {
        if (extension == null || extension.trim().isEmpty()) {
            return false;
        }
        
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }
} 