package com.jiayan.quitsmoking.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/diary/image")
public class DiaryImageController {
    
    private static final Logger log = LoggerFactory.getLogger(DiaryImageController.class);
    
    @Value("${diary.image.upload-path:uploads/diaries}")
    private String uploadPath;
    
    @Value("${diary.image.max-size:10485760}")
    private long maxFileSize; // 10MB
    
    @Value("${diary.image.allowed-types:jpg,jpeg,png,gif,webp}")
    private String allowedTypes;
    
    /**
     * 上传日记图片
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadImages(@RequestParam("files") MultipartFile[] files,
                                           @RequestParam("userId") Long userId) {
        log.info("开始处理图片上传请求，用户ID: {}, 文件数量: {}", userId, files.length);
        
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> uploadedImages = new ArrayList<>();
        
        try {
            // 验证文件数量
            if (files.length > 6) {
                log.warn("文件数量超过限制: {}", files.length);
                response.put("success", false);
                response.put("message", "最多只能上传6张图片");
                return response;
            }
            
            // 创建用户目录
            String userDir = uploadPath + File.separator + "user_" + userId;
            Files.createDirectories(Paths.get(userDir));
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // 验证文件大小
                    if (file.getSize() > maxFileSize) {
                        log.warn("文件 {} 超过大小限制: {} bytes", file.getOriginalFilename(), file.getSize());
                        continue;
                    }
                    
                    // 验证文件类型
                    String originalFilename = file.getOriginalFilename();
                    String fileExtension = getFileExtension(originalFilename);
                    if (!isAllowedFileType(fileExtension)) {
                        log.warn("不支持的文件类型: {}", fileExtension);
                        continue;
                    }
                    
                    // 生成唯一文件名
                    String fileName = UUID.randomUUID().toString() + "." + fileExtension;
                    Path filePath = Paths.get(userDir, fileName);
                    
                    // 保存文件
                    Files.copy(file.getInputStream(), filePath);
                    
                    // 构建图片信息
                    Map<String, Object> imageInfo = new HashMap<>();
                    imageInfo.put("fileName", fileName);
                    imageInfo.put("originalName", originalFilename);
                    imageInfo.put("fileUrl", "/api/v1/diary/image/" + userId + "/" + fileName);
                    imageInfo.put("fileSize", file.getSize());
                    imageInfo.put("uploadTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    
                    uploadedImages.add(imageInfo);
                    log.info("图片上传成功: 用户{}, 文件: {}, 大小: {} bytes", userId, fileName, file.getSize());
                }
            }
            
            log.info("图片上传处理完成，准备返回响应");
            response.put("success", true);
            response.put("message", "图片上传成功，共上传 " + uploadedImages.size() + " 张图片");
            response.put("data", uploadedImages);
            
            log.info("返回响应: {}", response);
            
        } catch (Exception e) {
            log.error("图片上传处理异常", e);
            response.put("success", false);
            response.put("message", "图片上传失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 获取图片
     */
    @GetMapping("/{userId}/{fileName}")
    public byte[] getImage(@PathVariable Long userId,
                          @PathVariable String fileName) throws IOException {
        Path imagePath = Paths.get(uploadPath, "user_" + userId, fileName);
        if (!Files.exists(imagePath)) {
            throw new IOException("图片文件不存在: " + fileName);
        }
        return Files.readAllBytes(imagePath);
    }
    
    /**
     * 删除图片
     */
    @DeleteMapping("/{userId}/{fileName}")
    public Map<String, Object> deleteImage(@PathVariable Long userId,
                                         @PathVariable String fileName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Path imagePath = Paths.get(uploadPath, "user_" + userId, fileName);
            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
                log.info("图片删除成功: 用户{}, 文件: {}", userId, fileName);
                response.put("success", true);
                response.put("message", "图片删除成功");
            } else {
                response.put("success", false);
                response.put("message", "图片文件不存在");
            }
        } catch (Exception e) {
            log.error("图片删除失败", e);
            response.put("success", false);
            response.put("message", "图片删除失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
    
    /**
     * 检查文件类型是否允许
     */
    private boolean isAllowedFileType(String fileExtension) {
        String[] allowed = allowedTypes.split(",");
        for (String type : allowed) {
            if (type.trim().equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }
} 