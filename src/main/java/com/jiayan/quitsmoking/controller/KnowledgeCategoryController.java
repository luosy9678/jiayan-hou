package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import com.jiayan.quitsmoking.entity.KnowledgeCategory;
import com.jiayan.quitsmoking.service.KnowledgeCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge/categories")
@RequiredArgsConstructor
public class KnowledgeCategoryController {

    private final KnowledgeCategoryService knowledgeCategoryService;

    /**
     * 获取分类列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) String accessLevel,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive) {
        try {
            log.info("开始获取分类列表 - page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);
            
            // 创建分页和排序参数
            Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(sortDirection, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // 从数据库获取分页数据
            Page<KnowledgeCategory> categoryPage = knowledgeCategoryService.getCategories(pageable);
            log.info("从数据库获取到分类分页数据 - 总数: {}, 当前页: {}, 总页数: {}", 
                    categoryPage.getTotalElements(), categoryPage.getNumber(), categoryPage.getTotalPages());
            
            // 转换为简化的Map格式，避免循环引用
            List<Map<String, Object>> categories = new ArrayList<>();
            for (KnowledgeCategory category : categoryPage.getContent()) {
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("id", category.getId());
                categoryMap.put("name", category.getName());
                categoryMap.put("parentId", category.getParentId());
                categoryMap.put("description", category.getDescription());
                categoryMap.put("sortOrder", category.getSortOrder());
                categoryMap.put("isActive", category.getIsActive());
                categoryMap.put("accessLevel", category.getAccessLevel() != null ? category.getAccessLevel().name() : "FREE");
                categoryMap.put("memberOnly", category.getMemberOnly());
                categoryMap.put("iconName", category.getIconName());
                categoryMap.put("createdAt", category.getCreatedAt());
                categoryMap.put("updatedAt", category.getUpdatedAt());
                categories.add(categoryMap);
            }
            
            // 构建分页响应
            Map<String, Object> response = new HashMap<>();
            response.put("content", categories);
            response.put("totalElements", categoryPage.getTotalElements());
            response.put("totalPages", categoryPage.getTotalPages());
            response.put("size", categoryPage.getSize());
            response.put("number", categoryPage.getNumber());
            response.put("first", categoryPage.isFirst());
            response.put("last", categoryPage.isLast());
            
            log.info("返回分类分页数据，数量: {}", categories.size());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取分类列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCategoryTree() {
        try {
            log.info("开始获取分类树");
            
            // 从数据库获取真实数据
            List<KnowledgeCategory> dbCategories = knowledgeCategoryService.getCategoryTree();
            log.info("从数据库获取到分类数量: {}", dbCategories.size());
            
            // 转换为简化的Map格式，避免循环引用
            List<Map<String, Object>> categories = new ArrayList<>();
            
            for (KnowledgeCategory category : dbCategories) {
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("id", category.getId());
                categoryMap.put("name", category.getName());
                categoryMap.put("parentId", category.getParentId());
                categoryMap.put("description", category.getDescription());
                categoryMap.put("sortOrder", category.getSortOrder());
                categoryMap.put("isActive", category.getIsActive());
                categoryMap.put("accessLevel", category.getAccessLevel() != null ? category.getAccessLevel().name() : "FREE");
                categoryMap.put("memberOnly", category.getMemberOnly());
                categoryMap.put("iconName", category.getIconName());
                categoryMap.put("createdAt", category.getCreatedAt());
                categoryMap.put("updatedAt", category.getUpdatedAt());
                categories.add(categoryMap);
            }
            
            log.info("返回数据库分类数据，数量: {}", categories.size());
            return ResponseEntity.ok(ApiResponse.success(categories));
        } catch (Exception e) {
            log.error("获取分类树失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取分类树失败: " + e.getMessage()));
        }
    }
} 
