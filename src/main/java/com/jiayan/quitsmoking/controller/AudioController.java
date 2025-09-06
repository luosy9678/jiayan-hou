package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import com.jiayan.quitsmoking.dto.AudioRequest;
import com.jiayan.quitsmoking.dto.AudioResponse;
import com.jiayan.quitsmoking.service.AudioService;
import com.jiayan.quitsmoking.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 音频管理控制器
 */
@RestController
@RequestMapping("/audios")
@RequiredArgsConstructor
@Slf4j
public class AudioController {
    
    private final AudioService audioService;
    private final JwtUtil jwtUtil;
    
    /**
     * 创建音频
     */
    @PostMapping
    public ApiResponse<AudioResponse> createAudio(
            @Valid @RequestBody AudioRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ApiResponse.error(401, "未提供有效的认证Token");
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Long userId = Long.valueOf(userIdStr);
            AudioResponse response = audioService.createAudio(userId, request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("创建音频时发生异常", e);
            return ApiResponse.error(401, "Token无效或已过期");
        }
    }
    
    /**
     * 更新音频
     */
    @PutMapping("/{audioId}")
    public ApiResponse<AudioResponse> updateAudio(
            @PathVariable Long audioId,
            @Valid @RequestBody AudioRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ApiResponse.error(401, "未提供有效的认证Token");
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Long userId = Long.valueOf(userIdStr);
            AudioResponse response = audioService.updateAudio(userId, audioId, request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("更新音频时发生异常", e);
            return ApiResponse.error(401, "Token无效或已过期");
        }
    }
    
    /**
     * 获取音频详情
     */
    @GetMapping("/{audioId}")
    public ApiResponse<AudioResponse> getAudio(@PathVariable Long audioId) {
        AudioResponse response = audioService.getAudio(audioId);
        return ApiResponse.success(response);
    }
    
    /**
     * 获取用户的音频列表
     */
    @GetMapping("/my")
    public ApiResponse<List<AudioResponse>> getMyAudios(HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ApiResponse.error(401, "未提供有效的认证Token");
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Long userId = Long.valueOf(userIdStr);
            List<AudioResponse> responses = audioService.getUserAudios(userId);
            return ApiResponse.success(responses);
        } catch (Exception e) {
            log.error("获取用户音频列表时发生异常", e);
            return ApiResponse.error(401, "Token无效或已过期");
        }
    }
    
    /**
     * 获取公有音频列表
     */
    @GetMapping("/public")
    public ApiResponse<List<AudioResponse>> getPublicAudios() {
        List<AudioResponse> responses = audioService.getPublicAudios();
        return ApiResponse.success(responses);
    }
    
    /**
     * 根据音色获取公有音频
     */
    @GetMapping("/public/voice-type/{voiceType}")
    public ApiResponse<List<AudioResponse>> getPublicAudiosByVoiceType(@PathVariable String voiceType) {
        List<AudioResponse> responses = audioService.getPublicAudiosByVoiceType(voiceType);
        return ApiResponse.success(responses);
    }
    
    /**
     * 删除音频
     */
    @DeleteMapping("/{audioId}")
    public ApiResponse<Void> deleteAudio(
            @PathVariable Long audioId,
            HttpServletRequest httpRequest) {
        
        try {
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ApiResponse.error(401, "未提供有效的认证Token");
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Long userId = Long.valueOf(userIdStr);
            audioService.deleteAudio(userId, audioId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("删除音频时发生异常", e);
            return ApiResponse.error(401, "Token无效或已过期");
        }
    }
    
    /**
     * 启用/禁用音频
     */
    @PutMapping("/{audioId}/toggle")
    public ApiResponse<AudioResponse> toggleAudioStatus(
            @PathVariable Long audioId,
            HttpServletRequest httpRequest) {
        
        try {
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ApiResponse.error(401, "未提供有效的认证Token");
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Long userId = Long.valueOf(userIdStr);
            AudioResponse response = audioService.toggleAudioStatus(userId, audioId);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("切换音频状态时发生异常", e);
            return ApiResponse.error(401, "Token无效或已过期");
        }
    }
    
    /**
     * 下载音频文件
     */
    @GetMapping("/download/{audioId}")
    public ResponseEntity<Resource> downloadAudio(
            @PathVariable Long audioId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = null;
            
            // 尝试获取用户ID（可选）
            String token = extractTokenFromHeader(httpRequest);
            if (token != null) {
                try {
                    String userIdStr = jwtUtil.getUserIdFromToken(token);
                    userId = Long.valueOf(userIdStr);
                } catch (Exception e) {
                    log.warn("Token无效，将以匿名用户身份下载");
                }
            }
            
            // 下载音频文件
            Resource resource = audioService.downloadAudio(audioId, userId);
            
            // 获取音频信息用于设置文件名
            AudioResponse audio = audioService.getAudio(audioId);
            String downloadFileName = audio.getFileName();
            
            // 如果有描述，使用描述作为文件名
            if (audio.getDescription() != null && !audio.getDescription().isEmpty()) {
                String extension = "";
                int lastDot = downloadFileName.lastIndexOf('.');
                if (lastDot > 0) {
                    extension = downloadFileName.substring(lastDot);
                }
                downloadFileName = audio.getDescription() + extension;
            }
            
            // 设置响应头
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + downloadFileName + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("下载音频时发生异常，音频ID: {}", audioId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 从请求头中提取Token
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
} 