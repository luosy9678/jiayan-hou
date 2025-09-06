package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.entity.Diary;
import com.jiayan.quitsmoking.repository.DiaryRepository;
import com.jiayan.quitsmoking.util.JwtUtil;
import com.jiayan.quitsmoking.dto.DiaryDetailResponse;
import com.jiayan.quitsmoking.dto.DiaryListResponse;
import com.jiayan.quitsmoking.dto.UpdateDiaryRequest;
import com.jiayan.quitsmoking.exception.DiaryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@RestController
@RequestMapping("/diary")
@SuppressWarnings("unchecked")
public class SimpleDiaryController {
    
    private static final Logger log = LoggerFactory.getLogger(SimpleDiaryController.class);
    
    @Autowired
    private DiaryRepository diaryRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public SimpleDiaryController() {
        log.info("SimpleDiaryController 已创建并加载！");
    }

    @PostMapping("/create")
    public String createDiary(@RequestBody Map<String, Object> request, 
                             @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("开始创建日记，请求数据: {}", request);
            
            // 从Authorization头获取用户ID
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return "日记创建失败: 用户未认证或token无效";
            }
            
            log.info("当前用户ID: {}", userId);
            
            // 创建日记对象
            Diary diary = new Diary();
            diary.setUserId(userId);
            
            // 处理内容
            String content = (String) request.get("content");
            diary.setContent(content);
            
            // 处理标签 - 将List转换为逗号分隔的字符串
            Object tagsObj = request.get("tags");
            if (tagsObj instanceof List<?>) {
                List<?> tagsList = (List<?>) tagsObj;
                String tagsStr = String.join(",", tagsList.stream().map(Object::toString).toList());
                diary.setTags(tagsStr);
            }
            
            // 处理图片信息 - 最多6张图片
            Object imagesObj = request.get("images");
            if (imagesObj instanceof List<?>) {
                List<?> imagesList = (List<?>) imagesObj;
                
                // 限制最多6张图片
                if (imagesList.size() > 6) {
                    log.warn("用户提交了{}张图片，超过限制6张，将只保存前6张", imagesList.size());
                    imagesList = imagesList.subList(0, 6);
                }
                
                // 将图片信息转换为JSON字符串存储
                try {
                    String imageUrlsJson = objectMapper.writeValueAsString(imagesList);
                    diary.setImageUrls(imageUrlsJson);
                    log.info("图片信息处理成功，共{}张图片", imagesList.size());
                } catch (JsonProcessingException e) {
                    log.error("图片信息JSON序列化失败", e);
                    diary.setImageUrls("[]");
                }
            } else {
                // 没有图片时设置为空数组
                diary.setImageUrls("[]");
            }
            
            // 处理状态 - 验证状态值并设置
            String status = (String) request.get("status");
            if (status != null && !status.isEmpty()) {
                if (!isValidStatus(status)) {
                    throw new DiaryException("无效的日记状态: " + status);
                }
                diary.setStatus(status);
            } else {
                diary.setStatus("草稿"); // 默认状态
            }
            
            // 计算字数
            if (content != null) {
                diary.setWordCount(content.length());
            }
            
            // 保存到数据库
            Diary savedDiary = diaryRepository.save(diary);
            
            log.info("日记保存成功，用户ID: {}, 日记ID: {}, 内容: {}, 字数: {}, 图片数量: {}", 
                    userId, savedDiary.getId(), savedDiary.getContent(), 
                    savedDiary.getWordCount(), getImageCount(savedDiary.getImageUrls()));
            
            return "日记创建成功！用户ID: " + userId + 
                   ", 日记ID: " + savedDiary.getId() + 
                   ", 内容: " + savedDiary.getContent() + 
                   ", 字数: " + savedDiary.getWordCount() + 
                   ", 图片数量: " + getImageCount(savedDiary.getImageUrls());
            
        } catch (Exception e) {
            log.error("创建日记失败", e);
            return "日记创建失败: " + e.getMessage();
        }
    }
    
    /**
     * 获取图片数量
     */
    private int getImageCount(String imageUrlsJson) {
        if (imageUrlsJson == null || imageUrlsJson.equals("[]")) {
            return 0;
        }
        try {
            List<?> images = objectMapper.readValue(imageUrlsJson, new TypeReference<List<Object>>() {});
            return images.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * 从Authorization头中提取用户ID
     */
    private Long extractUserIdFromToken(String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // 验证token
                if (!jwtUtil.validateToken(token)) {
                    log.warn("Token验证失败");
                    return null;
                }
                
                // 从JWT token中提取用户ID (String类型)
                String userIdStr = jwtUtil.getUserIdFromToken(token);
                if (userIdStr != null) {
                    // 转换为Long类型
                    return Long.valueOf(userIdStr);
                }
            }
        } catch (Exception e) {
            log.error("解析用户ID失败: {}", e.getMessage());
        }
        return null;
    }
    
    @GetMapping("/test")
    public String test() {
        log.info("调用 /diary/test 接口");
        return "日记接口工作正常！";
    }
    
    @GetMapping("/list")
    public DiaryListResponse getDiaryList(@RequestHeader("Authorization") String authHeader) {
        try {
            // 获取当前用户ID
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                throw DiaryException.userNotAuthenticated();
            }
            
            log.info("用户 {} 获取日记列表", userId);
            
            // 获取当前用户的所有日记（排除已删除的）
            List<Diary> diaries = diaryRepository.findByUserIdAndStatusNotOrderByRecordTimeDesc(userId, "删除");
            
            // 构建日记列表响应
            List<DiaryListResponse.DiaryListItem> diaryItems = new ArrayList<>();
            
            for (Diary diary : diaries) {
                DiaryListResponse.DiaryListItem item = new DiaryListResponse.DiaryListItem();
                item.setId(diary.getId());
                item.setUserId(diary.getUserId());
                item.setRecordDate(diary.getRecordDate());
                item.setRecordTime(diary.getRecordTime());
                item.setContent(diary.getContent());
                item.setMoodScore(diary.getMoodScore());
                item.setWordCount(diary.getWordCount());
                item.setStatus(diary.getStatus());
                item.setCreatedAt(diary.getCreatedAt());
                item.setUpdatedAt(diary.getUpdatedAt());
                
                // 处理标签 - 将逗号分隔的字符串转换为List
                if (diary.getTags() != null && !diary.getTags().isEmpty()) {
                    String[] tagArray = diary.getTags().split(",");
                    List<String> tagList = new ArrayList<>();
                    for (String tag : tagArray) {
                        if (tag != null && !tag.trim().isEmpty()) {
                            tagList.add(tag.trim());
                        }
                    }
                    item.setTags(tagList);
                }
                
                // 处理图片信息 - 将JSON字符串转换为List
                if (diary.getImageUrls() != null && !diary.getImageUrls().equals("[]")) {
                    try {
                        List<Map<String, Object>> imageList = objectMapper.readValue(diary.getImageUrls(), new TypeReference<List<Map<String, Object>>>() {});
                        List<DiaryListResponse.DiaryListItem.DiaryImageInfo> imageInfoList = new ArrayList<>();
                        
                        for (Map<String, Object> imageMap : imageList) {
                            DiaryListResponse.DiaryListItem.DiaryImageInfo imageInfo = new DiaryListResponse.DiaryListItem.DiaryImageInfo();
                            imageInfo.setFileName((String) imageMap.get("fileName"));
                            imageInfo.setOriginalName((String) imageMap.get("originalName"));
                            imageInfo.setFileUrl((String) imageMap.get("fileUrl"));
                            
                            // 处理文件大小
                            Object fileSizeObj = imageMap.get("fileSize");
                            if (fileSizeObj instanceof Number) {
                                imageInfo.setFileSize(((Number) fileSizeObj).longValue());
                            }
                            
                            // 处理上传时间
                            Object uploadTimeObj = imageMap.get("uploadTime");
                            if (uploadTimeObj instanceof String) {
                                try {
                                    LocalDateTime uploadTime = LocalDateTime.parse((String) uploadTimeObj);
                                    imageInfo.setUploadTime(uploadTime);
                                } catch (Exception e) {
                                    log.warn("解析图片上传时间失败: {}", uploadTimeObj);
                                }
                            }
                            
                            imageInfoList.add(imageInfo);
                        }
                        
                        item.setImages(imageInfoList);
                    } catch (Exception e) {
                        log.error("解析图片信息失败", e);
                        item.setImages(new ArrayList<>());
                    }
                } else {
                    item.setImages(new ArrayList<>());
                }
                
                diaryItems.add(item);
            }
            
            String message = String.format("用户 %d 的日记列表: 总数=%d", userId, diaryItems.size());
            log.info("用户 {} 获取日记列表成功，总数: {}", userId, diaryItems.size());
            
            return new DiaryListResponse(200, message, diaryItems);
            
        } catch (Exception e) {
            log.error("获取日记列表失败", e);
            if (e instanceof DiaryException) {
                throw e;
            }
            throw new DiaryException("获取日记列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/stats")
    public String getDiaryStats(@RequestHeader("Authorization") String authHeader) {
        try {
            // 获取当前用户ID
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return "获取日记统计失败: 用户未认证";
            }
            
            // 只统计当前用户的日记
            long count = diaryRepository.count();
            log.info("用户 {} 获取日记统计，总数: {}", userId, count);
            return "用户 " + userId + " 的日记统计: 总数=" + count;
        } catch (Exception e) {
            log.error("获取日记统计失败", e);
            return "获取日记统计失败: " + e.getMessage();
        }
    }
    
    @GetMapping("/{id}")
    public DiaryDetailResponse getDiaryDetail(@PathVariable Long id, 
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("开始获取日记详情，日记ID: {}", id);
            
            // 从Authorization头获取用户ID
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                throw DiaryException.userNotAuthenticated();
            }
            
            log.info("当前用户ID: {}, 请求的日记ID: {}", userId, id);
            
            // 查找日记
            Diary diary = diaryRepository.findById(id).orElse(null);
            if (diary == null) {
                throw DiaryException.diaryNotFound(id);
            }
            
            // 检查权限 - 只能查看自己的日记
            if (!diary.getUserId().equals(userId)) {
                log.warn("用户 {} 尝试访问用户 {} 的日记 {}", userId, diary.getUserId(), id);
                throw DiaryException.noPermission(id);
            }
            
            log.info("日记详情获取成功，用户ID: {}, 日记ID: {}, 内容: {}, 字数: {}, 图片数量: {}", 
                    userId, diary.getId(), diary.getContent(), 
                    diary.getWordCount(), getImageCount(diary.getImageUrls()));
            
            // 构建日记详情响应DTO
            DiaryDetailResponse response = new DiaryDetailResponse();
            response.setId(diary.getId());
            response.setUserId(diary.getUserId());
            response.setRecordDate(diary.getRecordDate());
            response.setRecordTime(diary.getRecordTime());
            response.setContent(diary.getContent());
            response.setMoodScore(diary.getMoodScore());
            response.setWordCount(diary.getWordCount());
            response.setStatus(diary.getStatus());
            response.setIsForwarded(diary.getIsForwarded());
            response.setForumPostId(diary.getForumPostId());
            response.setForumSection(diary.getForumSection());
            response.setForwardTime(diary.getForwardTime());
            response.setForumViews(diary.getForumViews());
            response.setForumLikes(diary.getForumLikes());
            response.setCreatedAt(diary.getCreatedAt());
            response.setUpdatedAt(diary.getUpdatedAt());
            
            // 处理标签 - 将逗号分隔的字符串转换为List
            if (diary.getTags() != null && !diary.getTags().isEmpty()) {
                String[] tagArray = diary.getTags().split(",");
                List<String> tagList = new ArrayList<>();
                for (String tag : tagArray) {
                    if (tag != null && !tag.trim().isEmpty()) {
                        tagList.add(tag.trim());
                    }
                }
                response.setTags(tagList);
            }
            
            // 处理图片信息 - 将JSON字符串转换为List
            if (diary.getImageUrls() != null && !diary.getImageUrls().equals("[]")) {
                try {
                    List<Map<String, Object>> imageList = objectMapper.readValue(diary.getImageUrls(), new TypeReference<List<Map<String, Object>>>() {});
                    List<DiaryDetailResponse.DiaryImageInfo> imageInfoList = new ArrayList<>();
                    
                    for (Map<String, Object> imageMap : imageList) {
                        DiaryDetailResponse.DiaryImageInfo imageInfo = new DiaryDetailResponse.DiaryImageInfo();
                        imageInfo.setFileName((String) imageMap.get("fileName"));
                        imageInfo.setOriginalName((String) imageMap.get("originalName"));
                        imageInfo.setFileUrl((String) imageMap.get("fileUrl"));
                        
                        // 处理文件大小
                        Object fileSizeObj = imageMap.get("fileSize");
                        if (fileSizeObj instanceof Number) {
                            imageInfo.setFileSize(((Number) fileSizeObj).longValue());
                        }
                        
                        // 处理上传时间
                        Object uploadTimeObj = imageMap.get("uploadTime");
                        if (uploadTimeObj instanceof String) {
                            try {
                                LocalDateTime uploadTime = LocalDateTime.parse((String) uploadTimeObj);
                                imageInfo.setUploadTime(uploadTime);
                            } catch (Exception e) {
                                log.warn("解析图片上传时间失败: {}", uploadTimeObj);
                            }
                        }
                        
                        imageInfoList.add(imageInfo);
                    }
                    
                    response.setImages(imageInfoList);
                } catch (Exception e) {
                    log.error("解析图片信息失败", e);
                    response.setImages(new ArrayList<>());
                }
            } else {
                response.setImages(new ArrayList<>());
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("获取日记详情失败，日记ID: {}", id, e);
            if (e instanceof DiaryException) {
                throw e;
            }
            throw new DiaryException("获取日记详情失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public String updateDiary(@PathVariable Long id, 
                             @RequestBody UpdateDiaryRequest request,
                             @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("开始更新日记，日记ID: {}, 请求数据: {}", id, request);
            
            // 从Authorization头获取用户ID
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                throw DiaryException.userNotAuthenticated();
            }
            
            log.info("当前用户ID: {}, 请求更新的日记ID: {}", userId, id);
            
            // 查找日记
            Diary diary = diaryRepository.findById(id).orElse(null);
            if (diary == null) {
                throw DiaryException.diaryNotFound(id);
            }
            
            // 检查权限 - 只能更新自己的日记
            if (!diary.getUserId().equals(userId)) {
                log.warn("用户 {} 尝试更新用户 {} 的日记 {}", userId, diary.getUserId(), id);
                throw DiaryException.noPermission(id);
            }
            
            // 检查日记状态 - 已转发的日记不能修改
            if (diary.getIsForwarded() != null && diary.getIsForwarded()) {
                log.warn("用户 {} 尝试更新已转发的日记 {}", userId, id);
                throw new DiaryException("已转发到论坛的日记不能修改");
            }
            
            // 更新日记内容
            boolean hasChanges = false;
            
            if (request.getContent() != null && !request.getContent().equals(diary.getContent())) {
                diary.setContent(request.getContent());
                // 重新计算字数
                diary.setWordCount(request.getContent().length());
                hasChanges = true;
                log.info("日记内容已更新，新字数: {}", diary.getWordCount());
            }
            
            // 更新标签
            if (request.getTags() != null) {
                String newTags = String.join(",", request.getTags());
                if (!newTags.equals(diary.getTags())) {
                    diary.setTags(newTags);
                    hasChanges = true;
                    log.info("日记标签已更新: {}", newTags);
                }
            }
            
            // 更新图片信息
            if (request.getImages() != null) {
                // 限制最多6张图片
                if (request.getImages().size() > 6) {
                    log.warn("用户提交了{}张图片，超过限制6张，将只保存前6张", request.getImages().size());
                    request.setImages(request.getImages().subList(0, 6));
                }
                
                try {
                    String imageUrlsJson = objectMapper.writeValueAsString(request.getImages());
                    if (!imageUrlsJson.equals(diary.getImageUrls())) {
                        diary.setImageUrls(imageUrlsJson);
                        hasChanges = true;
                        log.info("日记图片信息已更新，共{}张图片", request.getImages().size());
                    }
                } catch (JsonProcessingException e) {
                    log.error("图片信息JSON序列化失败", e);
                    throw new DiaryException("图片信息处理失败");
                }
            }
            
            // 更新心情评分
            if (request.getMoodScore() != null && !request.getMoodScore().equals(diary.getMoodScore())) {
                if (request.getMoodScore() < 1 || request.getMoodScore() > 5) {
                    throw new DiaryException("心情评分必须在1-5之间");
                }
                diary.setMoodScore(request.getMoodScore());
                hasChanges = true;
                log.info("日记心情评分已更新: {}", request.getMoodScore());
            }
            
            // 更新状态
            if (request.getStatus() != null && !request.getStatus().equals(diary.getStatus())) {
                // 验证状态值
                if (!isValidStatus(request.getStatus())) {
                    throw new DiaryException("无效的日记状态: " + request.getStatus());
                }
                diary.setStatus(request.getStatus());
                hasChanges = true;
                log.info("日记状态已更新: {}", request.getStatus());
            }
            
            // 如果没有变化，直接返回
            if (!hasChanges) {
                log.info("日记内容无变化，无需更新");
                return "日记内容无变化，无需更新";
            }
            
            // 更新修改时间
            diary.setUpdatedAt(LocalDateTime.now());
            
            // 保存到数据库
            Diary updatedDiary = diaryRepository.save(diary);
            
            log.info("日记更新成功，用户ID: {}, 日记ID: {}, 内容: {}, 字数: {}, 图片数量: {}", 
                    userId, updatedDiary.getId(), updatedDiary.getContent(), 
                    updatedDiary.getWordCount(), getImageCount(updatedDiary.getImageUrls()));
            
            return "日记更新成功！用户ID: " + userId + 
                   ", 日记ID: " + updatedDiary.getId() + 
                   ", 内容: " + updatedDiary.getContent() + 
                   ", 字数: " + updatedDiary.getWordCount() + 
                   ", 图片数量: " + getImageCount(updatedDiary.getImageUrls()) + 
                   ", 更新时间: " + updatedDiary.getUpdatedAt();
            
        } catch (Exception e) {
            log.error("更新日记失败，日记ID: {}", id, e);
            if (e instanceof DiaryException) {
                throw e;
            }
            throw new DiaryException("更新日记失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证日记状态是否有效
     */
    private boolean isValidStatus(String status) {
        if (status == null) {
            return false;
        }
        
        // 定义有效的状态值
        String[] validStatuses = {"草稿", "存档", "发布", "删除"};
        
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status)) {
                return true;
            }
        }
        
        return false;
    }
    
    @DeleteMapping("/{id}")
    public String deleteDiary(@PathVariable Long id, 
                             @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("开始删除日记，日记ID: {}", id);
            
            // 从Authorization头获取用户ID
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                throw DiaryException.userNotAuthenticated();
            }
            
            log.info("当前用户ID: {}, 请求删除的日记ID: {}", userId, id);
            
            // 查找日记
            Diary diary = diaryRepository.findById(id).orElse(null);
            if (diary == null) {
                throw DiaryException.diaryNotFound(id);
            }
            
            // 检查权限 - 只能删除自己的日记
            if (!diary.getUserId().equals(userId)) {
                log.warn("用户 {} 尝试删除用户 {} 的日记 {}", userId, diary.getUserId(), id);
                throw DiaryException.noPermission(id);
            }
            
            // 检查日记状态 - 已转发的日记不能删除
            if (diary.getIsForwarded() != null && diary.getIsForwarded()) {
                log.warn("用户 {} 尝试删除已转发的日记 {}", userId, id);
                throw new DiaryException("已转发到论坛的日记不能删除");
            }
            
            // 检查日记状态 - 已删除的日记不能重复删除
            if ("删除".equals(diary.getStatus())) {
                log.warn("用户 {} 尝试删除已经删除的日记 {}", userId, id);
                return "日记已经是删除状态，无需重复删除";
            }
            
            // 软删除：将状态设置为"删除"，而不是物理删除
            diary.setStatus("删除");
            diary.setUpdatedAt(LocalDateTime.now());
            
            // 保存到数据库
            Diary deletedDiary = diaryRepository.save(diary);
            
            log.info("日记删除成功，用户ID: {}, 日记ID: {}, 内容: {}, 状态: {}", 
                    userId, deletedDiary.getId(), deletedDiary.getContent(), 
                    deletedDiary.getStatus());
            
            return "日记删除成功！用户ID: " + userId + 
                   ", 日记ID: " + deletedDiary.getId() + 
                   ", 内容: " + deletedDiary.getContent() + 
                   ", 删除时间: " + deletedDiary.getUpdatedAt() + 
                   ", 注意：这是软删除，日记数据仍然保留在数据库中";
            
        } catch (Exception e) {
            log.error("删除日记失败，日记ID: {}", id, e);
            if (e instanceof DiaryException) {
                throw e;
            }
            throw new DiaryException("删除日记失败: " + e.getMessage());
        }
    }
    
    /**
     * 物理删除日记（管理员功能，谨慎使用）
     */
    @DeleteMapping("/{id}/permanent")
    public String permanentDeleteDiary(@PathVariable Long id, 
                                     @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("开始物理删除日记，日记ID: {}", id);
            
            // 从Authorization头获取用户ID
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                throw DiaryException.userNotAuthenticated();
            }
            
            log.info("当前用户ID: {}, 请求物理删除的日记ID: {}", userId, id);
            
            // 查找日记
            Diary diary = diaryRepository.findById(id).orElse(null);
            if (diary == null) {
                throw DiaryException.diaryNotFound(id);
            }
            
            // 检查权限 - 只能删除自己的日记
            if (!diary.getUserId().equals(userId)) {
                log.warn("用户 {} 尝试物理删除用户 {} 的日记 {}", userId, diary.getUserId(), id);
                throw DiaryException.noPermission(id);
            }
            
            // 检查日记状态 - 已转发的日记不能物理删除
            if (diary.getIsForwarded() != null && diary.getIsForwarded()) {
                log.warn("用户 {} 尝试物理删除已转发的日记 {}", userId, id);
                throw new DiaryException("已转发到论坛的日记不能物理删除");
            }
            
            // 记录删除的日记信息
            String deletedContent = diary.getContent();
            String deletedTags = diary.getTags();
            int deletedWordCount = diary.getWordCount();
            
            // 物理删除日记
            diaryRepository.deleteById(id);
            
            log.info("日记物理删除成功，用户ID: {}, 日记ID: {}, 内容: {}, 字数: {}", 
                    userId, id, deletedContent, deletedWordCount);
            
            return "日记物理删除成功！用户ID: " + userId + 
                   ", 日记ID: " + id + 
                   ", 内容: " + deletedContent + 
                   ", 字数: " + deletedWordCount + 
                   ", 标签: " + deletedTags + 
                   ", 注意：这是物理删除，日记数据已从数据库中永久移除";
            
        } catch (Exception e) {
            log.error("物理删除日记失败，日记ID: {}", id, e);
            if (e instanceof DiaryException) {
                throw e;
            }
            throw new DiaryException("物理删除日记失败: " + e.getMessage());
        }
    }
}