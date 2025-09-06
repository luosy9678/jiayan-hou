package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.dto.*;
import com.jiayan.quitsmoking.entity.KnowledgeRating;
import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.repository.UserRepository;
import com.jiayan.quitsmoking.service.KnowledgeRatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.jiayan.quitsmoking.exception.BusinessException;
import com.jiayan.quitsmoking.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户评分控制器
 */
@RestController
@RequestMapping("/api/v1/knowledge/ratings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class KnowledgeRatingController {
    
    private final KnowledgeRatingService ratingService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    /**
     * 创建评分
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createRating(
            @Valid @RequestBody CreateRatingRequest request) {
        log.info("创建评分: articleId={}", request.getArticleId());
        
        try {
            // 创建评分实体
            KnowledgeRating rating = new KnowledgeRating();
            rating.setArticleId(request.getArticleId());
            rating.setUserId(getCurrentUserId());  // 后端自动获取用户ID
            rating.setRating(request.getRating());
            rating.setComment(request.getComment());
            rating.setCreatedAt(LocalDateTime.now());
            rating.setUpdatedAt(LocalDateTime.now());
            
            // 调用评分服务创建评分
            KnowledgeRating savedRating = ratingService.createRating(rating);
            
            // 转换为响应DTO
            RatingResponse response = toRatingResponse(savedRating);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("创建评分失败", e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "error", "创建评分失败",
                        "message", e.getMessage()
                    ));
        }
    }
    
    /**
     * 更新评分
     */
    @PutMapping("/{ratingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingResponse> updateRating(
            @PathVariable Long ratingId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        log.info("更新评分: {} -> {}", ratingId, rating);
        
        try {
            // 调用评分服务更新评分
            KnowledgeRating updatedRating = ratingService.updateRating(ratingId, rating, comment);
            
            if (updatedRating == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 转换为响应DTO
            RatingResponse response = toRatingResponse(updatedRating);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("更新评分失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据ID获取评分
     */
    @GetMapping("/{ratingId}")
    public ResponseEntity<RatingResponse> getRating(@PathVariable Long ratingId) {
        log.info("获取评分: {}", ratingId);
        
        try {
            // 调用评分服务获取评分
            KnowledgeRating rating = ratingService.getRatingById(ratingId);
            
            if (rating == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 转换为响应DTO
            RatingResponse response = toRatingResponse(rating);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取评分失败", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 根据文章ID获取评分列表
     */
    @GetMapping("/by-article/{articleId}")
    public ResponseEntity<?> getRatingsByArticle(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取文章评分: articleId={}, page={}, size={}", articleId, page, size);
        
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // 调用评分服务获取文章评分列表
            Page<KnowledgeRating> ratingPage = ratingService.getRatingsByArticle(articleId, pageable);
            
            // 转换为RatingResponse
            Page<RatingResponse> ratings = ratingPage.map(this::toRatingResponse);
            
            // 返回统一的响应格式
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取文章评分成功",
                "data", PageResponse.of(ratings),
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("获取文章评分失败", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "error", "获取文章评分失败",
                "message", "系统内部错误",
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }
    
    /**
     * 根据用户ID获取评分列表
     */
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> getRatingsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取用户评分: userId={}, page={}, size={}", userId, page, size);
        
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // 调用评分服务获取用户评分列表
            Page<KnowledgeRating> ratingPage = ratingService.getRatingsByUser(userId, pageable);
            
            // 转换为RatingResponse
            Page<RatingResponse> ratings = ratingPage.map(this::toRatingResponse);
            
            // 返回统一的响应格式
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取用户评分成功",
                "data", PageResponse.of(ratings),
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("获取用户评分失败", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "error", "获取用户评分失败",
                "message", "系统内部错误",
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }
    
    /**
     * 获取文章的高评分（4-5分）
     */
    @GetMapping("/high/{articleId}")
    public ResponseEntity<?> getHighRatingsByArticle(@PathVariable Long articleId) {
        log.info("获取文章高评分: articleId={}", articleId);
        
        try {
            // 调用评分服务获取高评分
            List<KnowledgeRating> highRatings = ratingService.getHighRatingsByArticle(articleId);
            
            // 转换为RatingResponse
            List<RatingResponse> responses = highRatings.stream()
                    .map(this::toRatingResponse)
                    .collect(Collectors.toList());
            
            // 返回统一的响应格式
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取文章高评分成功",
                "data", responses,
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("获取文章高评分失败", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "error", "获取文章高评分失败",
                "message", "系统内部错误",
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }
    
    /**
     * 获取文章的低评分（1-2分）
     */
    @GetMapping("/low/{articleId}")
    public ResponseEntity<?> getLowRatingsByArticle(@PathVariable Long articleId) {
        log.info("获取文章低评分: articleId={}", articleId);
        
        try {
            // 调用评分服务获取低评分 - 修复方法名
            List<KnowledgeRating> lowRatings = ratingService.getLowRatingsByArticle(articleId);
            
            // 转换为RatingResponse
            List<RatingResponse> responses = lowRatings.stream()
                    .map(this::toRatingResponse)
                    .collect(Collectors.toList());
            
            // 返回统一的响应格式
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取文章低评分成功",
                "data", responses,
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("获取文章低评分失败", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "error", "获取文章低评分失败",
                "message", "系统内部错误",
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }
    
    /**
     * 计算文章的平均评分
     */
    @GetMapping("/average/{articleId}")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long articleId) {
        log.info("计算文章平均评分: articleId={}", articleId);
        
        try {
            // 调用评分服务计算平均评分
            Double averageRating = ratingService.calculateAverageRating(articleId);
            return ResponseEntity.ok(averageRating != null ? averageRating : 0.0);
        } catch (Exception e) {
            log.error("计算文章平均评分失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取文章的评分分布
     */
    @GetMapping("/distribution/{articleId}")
    public ResponseEntity<Map<Integer, Long>> getRatingDistribution(@PathVariable Long articleId) {
        log.info("获取文章评分分布: articleId={}", articleId);
        
        try {
            // 调用评分服务获取评分分布
            Map<Integer, Long> distribution = ratingService.getRatingDistribution(articleId);
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            log.error("获取文章评分分布失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取最新评分
     */
    @GetMapping("/latest")
    public ResponseEntity<PageResponse<RatingResponse>> getLatestRatings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取最新评分: page={}, size={}", page, size);
        
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // 调用评分服务获取最新评分
            Page<KnowledgeRating> ratingPage = ratingService.getLatestRatings(pageable);
            
            // 转换为RatingResponse
            Page<RatingResponse> ratings = ratingPage.map(this::toRatingResponse);
            return ResponseEntity.ok(PageResponse.of(ratings));
        } catch (Exception e) {
            log.error("获取最新评分失败", e);
            return ResponseEntity.internalServerError().body(PageResponse.empty());
        }
    }
    
    /**
     * 获取指定评分范围的最新评分
     */
    @GetMapping("/by-range")
    public ResponseEntity<PageResponse<RatingResponse>> getRatingsByRange(
            @RequestParam Integer minRating,
            @RequestParam Integer maxRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取评分范围: {}-{}, page={}, size={}", minRating, maxRating, page, size);
        
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // 调用评分服务获取指定范围的评分
            Page<KnowledgeRating> ratingPage = ratingService.getRatingsByRange(minRating, maxRating, pageable);
            
            // 转换为RatingResponse
            Page<RatingResponse> ratings = ratingPage.map(this::toRatingResponse);
            return ResponseEntity.ok(PageResponse.of(ratings));
        } catch (Exception e) {
            log.error("获取评分范围失败", e);
            return ResponseEntity.internalServerError().body(PageResponse.empty());
        }
    }
    
    /**
     * 获取有评论的评分
     */
    @GetMapping("/with-comments")
    public ResponseEntity<PageResponse<RatingResponse>> getRatingsWithComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取有评论的评分: page={}, size={}", page, size);
        
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // 调用评分服务获取有评论的评分
            Page<KnowledgeRating> ratingPage = ratingService.getRatingsWithComments(pageable);
            
            // 转换为RatingResponse
            Page<RatingResponse> ratings = ratingPage.map(this::toRatingResponse);
            return ResponseEntity.ok(PageResponse.of(ratings));
        } catch (Exception e) {
            log.error("获取有评论的评分失败", e);
            return ResponseEntity.internalServerError().body(PageResponse.empty());
        }
    }
    
    /**
     * 根据评论内容搜索评分
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<RatingResponse>> searchRatingsByComment(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("搜索评分: keyword={}, page={}, size={}", keyword, page, size);
        
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // 调用评分服务搜索评分
            Page<KnowledgeRating> ratingPage = ratingService.searchRatingsByComment(keyword, pageable);
            
            // 转换为RatingResponse
            Page<RatingResponse> ratings = ratingPage.map(this::toRatingResponse);
            return ResponseEntity.ok(PageResponse.of(ratings));
        } catch (Exception e) {
            log.error("搜索评分失败", e);
            return ResponseEntity.internalServerError().body(PageResponse.empty());
        }
    }
    
    /**
     * 删除用户对指定文章的评分
     */
    @DeleteMapping("/by-article/{articleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteRating(@PathVariable Long articleId) {
        log.info("删除用户评分: articleId={}", articleId);
        
        try {
            // 调用评分服务删除评分
            ratingService.deleteRating(articleId, getCurrentUserId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("删除用户评分失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 检查用户是否已对文章评分
     */
    @GetMapping("/check/{articleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> hasUserRated(@PathVariable Long articleId) {
        log.info("检查用户是否已评分: articleId={}", articleId);
        
        try {
            // 调用评分服务检查用户是否已评分
            boolean hasRated = ratingService.hasUserRated(articleId, getCurrentUserId());
            return ResponseEntity.ok(hasRated);
        } catch (Exception e) {
            log.error("检查用户是否已评分失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取热门评分文章
     */
    @GetMapping("/top-articles")
    public ResponseEntity<List<Long>> getTopRatedArticles(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("获取热门评分文章: limit={}", limit);
        
        try {
            // 由于评分服务没有热门文章方法，暂时返回空结果
            // TODO: 实现热门文章查询逻辑（按平均评分排序）
            log.warn("热门文章查询功能需要扩展评分服务");
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            log.error("获取热门评分文章失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取用户评分趋势
     */
    @GetMapping("/trend/{userId}")
    public ResponseEntity<Map<String, Double>> getUserRatingTrend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") int days) {
        log.info("获取用户评分趋势: userId={}, days={}", userId, days);
        
        try {
            // 由于评分服务没有趋势查询方法，暂时返回空结果
            // TODO: 实现评分趋势查询逻辑（按时间统计平均评分）
            log.warn("评分趋势查询功能需要扩展评分服务");
            return ResponseEntity.ok(Map.of());
        } catch (Exception e) {
            log.error("获取用户评分趋势失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取当前登录用户ID
     * 从JWT Token中获取用户ID，这是前端用户评论的标准方式
     * 注意：此方法不创建系统用户，只获取真实用户ID
     */
    private Long getCurrentUserId() {
        try {
            // 获取当前请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                throw new BusinessException("无法获取请求上下文");
            }
            
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new BusinessException("Authorization头缺失或格式错误");
            }
            
            // 从JWT token中提取用户ID
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                throw new BusinessException("JWT token无效");
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            if (userIdStr == null) {
                throw new BusinessException("无法从JWT token中提取用户ID");
            }
            
            Long userId = Long.valueOf(userIdStr);
            log.info("从JWT token获取到用户ID: {}", userId);
            return userId;
            
        } catch (BusinessException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            log.error("获取用户ID时发生异常: {}", e.getMessage(), e);
            throw new BusinessException("用户认证失败");
        }
    }
    
    /**
     * 创建系统用户
     */
    private Long createSystemUser() {
        User user = new User();
        user.setNickname("系统用户");
        user.setEnabled(true);
        user.setCanCreatePosts(true);
        user.setMemberLevel("free");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        log.info("创建系统用户: id={}", savedUser.getId());
        return savedUser.getId();
    }
    
    /**
     * 将KnowledgeRating转换为RatingResponse
     */
    private RatingResponse toRatingResponse(KnowledgeRating rating) {
        if (rating == null) {
            return null;
        }
        
        RatingResponse response = new RatingResponse();
        response.setId(rating.getId());
        response.setArticleId(rating.getArticleId());
        response.setArticleTitle("文章" + rating.getArticleId()); // 简化处理
        response.setUserId(rating.getUserId());
        response.setUserName("用户" + rating.getUserId()); // 简化处理
        response.setUserAvatar(null); // 暂时不设置头像
        response.setRating(rating.getRating());
        response.setComment(rating.getComment());
        response.setCreatedAt(rating.getCreatedAt());
        
        return response;
    }
} 