package com.jiayan.quitsmoking.service.impl;

import com.jiayan.quitsmoking.dto.UpdateUserRequest;
import com.jiayan.quitsmoking.service.BackgroundService;
import com.jiayan.quitsmoking.service.UserService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 背景图服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackgroundServiceImpl implements BackgroundService {

    private final UserService userService;
    
    // 背景图存储目录
    private static final String BACKGROUND_UPLOAD_DIR = "uploads/backgrounds";
    private static final String DEFAULT_BACKGROUND = "/uploads/backgrounds/default-bg.jpg";

    @Override
    @Transactional
    public String uploadBackground(Long userId, MultipartFile file) throws Exception {
        // 验证文件
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }

        // 验证文件大小（最大10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("背景图文件大小不能超过10MB");
        }

        // 创建上传目录
        Path uploadDir = Paths.get(BACKGROUND_UPLOAD_DIR);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        if (fileExtension == null) {
            fileExtension = "jpg";
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomId = UUID.randomUUID().toString().substring(0, 8);
        String fileName = String.format("background_%d_%s_%s.%s", userId, timestamp, randomId, fileExtension);

        // 保存文件
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("背景图上传成功: userId={}, fileName={}, size={}", userId, fileName, file.getSize());

        // 更新用户背景图
        userService.updateUserProfile(userId, createUpdateRequest(fileName));

        return fileName;
    }

    @Override
    @Transactional
    public String downloadBackground(Long userId, URL url) throws Exception {
        // 创建上传目录
        Path uploadDir = Paths.get(BACKGROUND_UPLOAD_DIR);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 生成文件名
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomId = UUID.randomUUID().toString().substring(0, 8);
        String fileName = String.format("background_%d_%s_%s.jpg", userId, timestamp, randomId);

        // 下载并保存文件
        Path filePath = uploadDir.resolve(fileName);
        try (var inputStream = url.openStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("背景图下载成功: userId={}, fileName={}, url={}", userId, fileName, url);

        // 更新用户背景图
        userService.updateUserProfile(userId, createUpdateRequest(fileName));

        return fileName;
    }

    @Override
    public Resource getBackgroundFile(String fileName) throws Exception {
        // 如果是默认背景图，返回默认文件
        if ("default-bg.jpg".equals(fileName)) {
            Path defaultPath = Paths.get(BACKGROUND_UPLOAD_DIR, "default-bg.jpg");
            if (!Files.exists(defaultPath)) {
                // 如果默认文件不存在，创建一个简单的默认图片或返回错误
                throw new IOException("默认背景图文件不存在: " + fileName);
            }
            Resource resource = new UrlResource(defaultPath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
        }
        
        Path filePath = Paths.get(BACKGROUND_UPLOAD_DIR).resolve(fileName);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("背景图文件不存在或无法读取: " + fileName);
        }
    }

    @Override
    @Transactional
    public void deleteBackground(Long userId) throws Exception {
        // 获取用户当前背景图
        var userProfile = userService.getUserProfile(userId);
        String currentBackground = userProfile.getBackgroundImage();

        // 如果是默认背景图，不删除
        if (DEFAULT_BACKGROUND.equals(currentBackground)) {
            log.info("用户使用默认背景图，无需删除: userId={}", userId);
            return;
        }

        // 提取文件名
        String fileName = extractFileNameFromUrl(currentBackground);
        if (fileName != null) {
            // 删除文件
            Path filePath = Paths.get(BACKGROUND_UPLOAD_DIR).resolve(fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("背景图文件删除成功: userId={}, fileName={}", userId, fileName);
            }
        }

        // 重置为默认背景图
        userService.updateUserProfile(userId, createUpdateRequest(DEFAULT_BACKGROUND));
        log.info("背景图重置为默认: userId={}", userId);
    }

    /**
     * 创建更新用户请求
     */
    private UpdateUserRequest createUpdateRequest(String backgroundImage) {
        var request = new UpdateUserRequest();
        request.setBackgroundImage(backgroundImage);
        return request;
    }

    /**
     * 从URL中提取文件名
     */
    private String extractFileNameFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        // 如果是完整URL，提取文件名
        if (url.startsWith("http://") || url.startsWith("https://")) {
            try {
                URL urlObj = new URL(url);
                String path = urlObj.getPath();
                return path.substring(path.lastIndexOf('/') + 1);
            } catch (Exception e) {
                log.warn("无法从URL提取文件名: {}", url);
                return null;
            }
        }

        // 如果是本地路径，提取文件名
        if (url.startsWith("/api/v1/background/")) {
            return url.substring("/api/v1/background/".length());
        }

        // 如果是相对路径，直接返回
        if (url.startsWith("/uploads/backgrounds/")) {
            return url.substring("/uploads/backgrounds/".length());
        }

        return url;
    }
} 