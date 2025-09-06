package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/knowledge/articles/images")
@Slf4j
public class ArticleImageController {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return ApiResponse.error(400, "文件为空");
            String contentType = file.getContentType();
            long size = file.getSize();
            String original = file.getOriginalFilename();
            log.info("文章图片上传: name={}, size={} bytes, type={}", original, size, contentType);

            if (contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.error(400, "仅支持图片文件");
            }
            if (size > 20L * 1024 * 1024) { // 与 application.properties 对齐
                return ApiResponse.error(400, "图片大小不能超过20MB");
            }

            String ext = ".png";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }

            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String name = date + "_" + UUID.randomUUID().toString().substring(0,8) + ext;

            Path dir = Path.of("uploads", "articles");
            Files.createDirectories(dir);
            if (!Files.isWritable(dir)) {
                log.error("目录无写权限: {}", dir.toAbsolutePath());
                return ApiResponse.error(500, "服务器目录无写权限: " + dir.toAbsolutePath());
            }
            Path dest = dir.resolve(name);
            log.info("保存文章图片到: {}", dest.toAbsolutePath());
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            String url = "/uploads/articles/" + name;
            return ApiResponse.success("上传成功", url);
        } catch (IOException e) {
            log.error("图片上传失败(IO)", e);
            return ApiResponse.error(500, "IO失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return ApiResponse.error(500, "服务器内部错误: " + e.getMessage());
        }
    }
} 