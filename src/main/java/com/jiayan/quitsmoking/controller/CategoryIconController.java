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
@RequestMapping("/api/v1/knowledge/categories/icons")
@Slf4j
public class CategoryIconController {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadIcon(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error(400, "文件为空");
            }
            
            String contentType = file.getContentType();
            long size = file.getSize();
            String original = file.getOriginalFilename();
            log.info("分类图标上传: name={}, size={} bytes, type={}", original, size, contentType);

            if (contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.error(400, "仅支持图片文件");
            }
            
            if (size > 5L * 1024 * 1024) {
                return ApiResponse.error(400, "图标大小不能超过5MB");
            }

            String ext = ".png";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "category_" + date + "_" + UUID.randomUUID().toString().substring(0,8) + ext;

            Path dir = Path.of("uploads", "category-icons");
            Files.createDirectories(dir);
            
            if (!Files.isWritable(dir)) {
                log.error("目录无写权限: {}", dir.toAbsolutePath());
                return ApiResponse.error(500, "服务器目录无写权限");
            }
            
            Path dest = dir.resolve(fileName);
            log.info("保存分类图标到: {}", dest.toAbsolutePath());
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            return ApiResponse.success("图标上传成功", fileName);
            
        } catch (IOException e) {
            log.error("图标上传失败(IO)", e);
            return ApiResponse.error(500, "IO失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("图标上传失败", e);
            return ApiResponse.error(500, "上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/{fileName}")
    public void getIcon(@PathVariable String fileName, 
                       jakarta.servlet.http.HttpServletResponse response) throws IOException {
        Path iconPath = Path.of("uploads", "category-icons", fileName);
        
        if (!Files.exists(iconPath)) {
            response.setStatus(404);
            return;
        }
        
        String contentType = Files.probeContentType(iconPath);
        if (contentType == null) {
            contentType = "image/png";
        }
        response.setContentType(contentType);
        
        Files.copy(iconPath, response.getOutputStream());
    }
}
