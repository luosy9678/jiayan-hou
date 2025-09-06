package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import com.jiayan.quitsmoking.service.AvatarService;
import com.jiayan.quitsmoking.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 头像管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;
    private final JwtUtil jwtUtil;

    /**
     * 上传头像文件
     */
    @PostMapping("/upload")
    public ApiResponse<String> uploadAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        // 验证Token和用户ID
        if (userId == null) {
            return ApiResponse.error(401, "Token无效或已过期");
        }
        
        log.info("上传头像: userId={}, fileName={}, size={}", userId, file.getOriginalFilename(), file.getSize());
        
        try {
            String avatarFileName = avatarService.uploadAvatar(Long.valueOf(userId), file);
            return ApiResponse.success("头像上传成功", avatarFileName);
        } catch (Exception e) {
            log.error("头像上传失败: userId={}, error={}", userId, e.getMessage(), e);
            return ApiResponse.error(400, "头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 从URL下载并保存头像
     */
    @PostMapping("/download")
    public ApiResponse<String> downloadAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String avatarUrl) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        // 验证Token和用户ID
        if (userId == null) {
            return ApiResponse.error(401, "Token无效或已过期");
        }
        
        log.info("下载头像: userId={}, url={}", userId, avatarUrl);
        
        // 验证URL参数
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return ApiResponse.error(400, "头像URL不能为空");
        }
        
        try {
            // 验证URL格式
            URL url = new URL(avatarUrl.trim());
            log.info("URL解析成功: protocol={}, host={}, path={}", url.getProtocol(), url.getHost(), url.getPath());
            
            String avatarFileName = avatarService.downloadAvatar(Long.valueOf(userId), url);
            return ApiResponse.success("头像下载成功", avatarFileName);
        } catch (MalformedURLException e) {
            log.error("URL格式错误: userId={}, url={}, error={}", userId, avatarUrl, e.getMessage());
            return ApiResponse.error(400, "头像URL格式错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("头像下载失败: userId={}, url={}, error={}", userId, avatarUrl, e.getMessage(), e);
            return ApiResponse.error(400, "头像下载失败: " + e.getMessage());
        }
    }

    /**
     * 获取头像文件
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String fileName) {
        try {
            Resource resource = avatarService.getAvatarFile(fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("获取头像失败: fileName={}, error={}", fileName, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除头像
     */
    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteAvatar(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("删除头像: userId={}", userId);
        
        try {
            avatarService.deleteAvatar(Long.valueOf(userId));
            return ApiResponse.success("头像删除成功", null);
        } catch (Exception e) {
            log.error("头像删除失败: userId={}, error={}", userId, e.getMessage(), e);
            return ApiResponse.error(400, "头像删除失败: " + e.getMessage());
        }
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