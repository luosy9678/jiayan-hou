package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import com.jiayan.quitsmoking.service.BackgroundService;
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
import java.util.Map;

/**
 * 背景图管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/background")
@RequiredArgsConstructor
public class BackgroundController {

    private final BackgroundService backgroundService;
    private final JwtUtil jwtUtil;

    /**
     * 上传背景图文件
     */
    @PostMapping("/upload")
    public ApiResponse<String> uploadBackground(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        // 验证Token和用户ID
        if (userId == null) {
            return ApiResponse.error(401, "Token无效或已过期");
        }
        
        log.info("上传背景图: userId={}, fileName={}, size={}", userId, file.getOriginalFilename(), file.getSize());
        
        try {
            String backgroundFileName = backgroundService.uploadBackground(Long.valueOf(userId), file);
            return ApiResponse.success("背景图上传成功", backgroundFileName);
        } catch (Exception e) {
            log.error("背景图上传失败: userId={}, error={}", userId, e.getMessage(), e);
            return ApiResponse.error(400, "背景图上传失败: " + e.getMessage());
        }
    }

    /**
     * 从URL下载并保存背景图
     */
    @PostMapping("/download")
    public ApiResponse<String> downloadBackground(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        // 验证Token和用户ID
        if (userId == null) {
            return ApiResponse.error(401, "Token无效或已过期");
        }
        
        String backgroundUrl = request.get("backgroundUrl");
        log.info("下载背景图: userId={}, url={}", userId, backgroundUrl);
        
        // 验证URL参数
        if (backgroundUrl == null || backgroundUrl.trim().isEmpty()) {
            return ApiResponse.error(400, "背景图URL不能为空");
        }
        
        try {
            // 验证URL格式
            URL url = new URL(backgroundUrl.trim());
            log.info("URL解析成功: protocol={}, host={}, path={}", url.getProtocol(), url.getHost(), url.getPath());
            
            String backgroundFileName = backgroundService.downloadBackground(Long.valueOf(userId), url);
            return ApiResponse.success("背景图下载成功", backgroundFileName);
        } catch (MalformedURLException e) {
            log.error("URL格式错误: userId={}, url={}, error={}", userId, backgroundUrl, e.getMessage());
            return ApiResponse.error(400, "背景图URL格式错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("背景图下载失败: userId={}, url={}, error={}", userId, backgroundUrl, e.getMessage(), e);
            return ApiResponse.error(400, "背景图下载失败: " + e.getMessage());
        }
    }

    /**
     * 获取背景图文件
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getBackground(@PathVariable String fileName) {
        try {
            Resource resource = backgroundService.getBackgroundFile(fileName);
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                        .contentType(MediaType.IMAGE_JPEG) // 根据实际文件类型调整
                        .body(resource);
            } else {
                log.warn("背景图文件不存在或无法读取: fileName={}", fileName);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取背景图文件失败: fileName={}, error={}", fileName, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除背景图
     */
    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteBackground(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        // 验证Token和用户ID
        if (userId == null) {
            return ApiResponse.error(401, "Token无效或已过期");
        }
        
        log.info("删除背景图: userId={}", userId);
        
        try {
            backgroundService.deleteBackground(Long.valueOf(userId));
            return ApiResponse.success("背景图删除成功", null);
        } catch (Exception e) {
            log.error("背景图删除失败: userId={}, error={}", userId, e.getMessage(), e);
            return ApiResponse.error(400, "背景图删除失败: " + e.getMessage());
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