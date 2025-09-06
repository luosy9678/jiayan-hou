package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.dto.*;
import com.jiayan.quitsmoking.entity.KnowledgeArticle;
import com.jiayan.quitsmoking.entity.KnowledgeComment;
import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.enums.ArticleStatus;
import com.jiayan.quitsmoking.enums.AuditStatus;
import com.jiayan.quitsmoking.enums.BlockType;
import com.jiayan.quitsmoking.enums.CommentStatus;
import com.jiayan.quitsmoking.repository.UserRepository;
import com.jiayan.quitsmoking.service.KnowledgeArticleService;
import com.jiayan.quitsmoking.service.KnowledgeCommentService;
import com.jiayan.quitsmoking.service.KnowledgeRatingService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

/**
 * 知识文章控制器
 */
@RestController
@RequestMapping("/api/v1/knowledge/articles")
@RequiredArgsConstructor
@Slf4j
@Validated
public class KnowledgeArticleController {
    
    private final KnowledgeArticleService articleService;
    private final UserRepository userRepository;
    private final KnowledgeCommentService commentService;
    private final KnowledgeRatingService ratingService;
    
    /**
     * 创建知识文章
     */
    @PostMapping
    public ResponseEntity<com.jiayan.quitsmoking.common.ApiResponse<ArticleResponse>> createArticle(
            @Valid @RequestBody CreateArticleRequest request) {
        log.info("创建知识文章: {}", request.getTitle());
        
        try {
            KnowledgeArticle article = new KnowledgeArticle();
            article.setTitle(request.getTitle());
            article.setContent(request.getContent());
            article.setCategoryId(request.getCategoryId());
            article.setSource(request.getSource());
            Long authorId = resolveAuthorId();
            log.info("使用作者ID: {}", authorId);
            article.setAuthorId(authorId);
            
            // 校验并构造内容块
            java.util.List<com.jiayan.quitsmoking.entity.KnowledgeContentBlock> blocks = new java.util.ArrayList<>();
            if (request.getContentBlocks() != null && !request.getContentBlocks().isEmpty()) {
                // 校验顺序连续与类型合法
                int expected = 1;
                for (CreateArticleRequest.ContentBlockRequest b : request.getContentBlocks()) {
                    if (!"text".equalsIgnoreCase(b.getBlockType()) && !"image".equalsIgnoreCase(b.getBlockType())) {
                        throw new IllegalArgumentException("非法内容块类型: " + b.getBlockType());
                    }
                    if (b.getContentOrder() == null || b.getContentOrder() != expected) {
                        throw new IllegalArgumentException("内容块顺序必须从1开始递增，缺少序号: " + expected);
                    }
                    expected++;
                    com.jiayan.quitsmoking.entity.KnowledgeContentBlock cb = new com.jiayan.quitsmoking.entity.KnowledgeContentBlock();
                    cb.setBlockType(BlockType.fromCode(b.getBlockType()));
                    cb.setContentOrder(b.getContentOrder());
                    cb.setTextContent(b.getTextContent());
                    cb.setImageUrl(b.getImageUrl());
                    cb.setImageAlt(b.getImageAlt());
                    cb.setImageWidth(b.getImageWidth());
                    cb.setImageHeight(b.getImageHeight());
                    blocks.add(cb);
                }
            }
            
            KnowledgeArticle saved = articleService.createArticle(article, blocks);
            ArticleResponse resp = toArticleResponse(saved);
            return ResponseEntity.ok(com.jiayan.quitsmoking.common.ApiResponse.success("创建成功", resp));
        } catch (Exception e) {
            log.error("创建文章失败", e);
            return ResponseEntity.badRequest().body(com.jiayan.quitsmoking.common.ApiResponse.error(400, e.getMessage() == null ? "创建失败" : e.getMessage()));
        }
    }
    
    /**
     * 更新知识文章（支持部分字段）
     */
    @PutMapping("/{articleId}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long articleId,
            @RequestBody java.util.Map<String, Object> request) {
        log.info("更新知识文章: {} -> {}", articleId, request);
        try {
            KnowledgeArticle existing = articleService.getArticleById(articleId);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }

            // 基础信息
            if (request.containsKey("title")) {
                existing.setTitle((String) request.get("title"));
            }
            if (request.containsKey("content")) {
                existing.setContent((String) request.get("content"));
            }
            if (request.containsKey("source")) {
                existing.setSource((String) request.get("source"));
            }
            if (request.containsKey("categoryId")) {
                Object cid = request.get("categoryId");
                if (cid != null) {
                    existing.setCategoryId(Long.valueOf(String.valueOf(cid)));
                }
            }

            // 状态更新
            if (request.containsKey("status")) {
                String s = String.valueOf(request.get("status"));
                try {
                    existing.setStatus(com.jiayan.quitsmoking.enums.ArticleStatus.valueOf(s.toUpperCase()));
                } catch (IllegalArgumentException ignore) {
                    log.warn("非法的文章状态: {}", s);
                }
            }

            // 审核更新
            if (request.containsKey("auditStatus")) {
                String s = String.valueOf(request.get("auditStatus"));
                try {
                    existing.setAuditStatus(com.jiayan.quitsmoking.enums.AuditStatus.valueOf(s.toUpperCase()));
                } catch (IllegalArgumentException ignore) {
                    log.warn("非法的审核状态: {}", s);
                }
            }
            if (request.containsKey("auditComment")) {
                existing.setAuditComment((String) request.get("auditComment"));
            }

            // 更新时间
            existing.setUpdatedAt(java.time.LocalDateTime.now());

            // 解析内容块（可选）
            java.util.List<com.jiayan.quitsmoking.entity.KnowledgeContentBlock> blocks = null;
            if (request.containsKey("contentBlocks") && request.get("contentBlocks") instanceof java.util.List<?> rawList) {
                blocks = new java.util.ArrayList<>();
                int expected = 1;
                for (Object obj : rawList) {
                    if (!(obj instanceof java.util.Map)) continue;
                    java.util.Map<?,?> m = (java.util.Map<?,?>) obj;
                    String type = m.get("blockType") == null ? null : String.valueOf(m.get("blockType"));
                    Integer order = null;
                    try { order = m.get("contentOrder") == null ? null : Integer.valueOf(String.valueOf(m.get("contentOrder"))); } catch (Exception ignore) {}
                    if (type == null || order == null || order != expected) {
                        throw new IllegalArgumentException("内容块顺序必须从1开始递增，或类型缺失");
                    }
                    expected++;
                    com.jiayan.quitsmoking.entity.KnowledgeContentBlock cb = new com.jiayan.quitsmoking.entity.KnowledgeContentBlock();
                    cb.setBlockType(BlockType.fromCode(type));
                    cb.setContentOrder(order);
                    cb.setTextContent(m.get("textContent") == null ? null : String.valueOf(m.get("textContent")));
                    cb.setImageUrl(m.get("imageUrl") == null ? null : String.valueOf(m.get("imageUrl")));
                    cb.setImageAlt(m.get("imageAlt") == null ? null : String.valueOf(m.get("imageAlt")));
                    try { cb.setImageWidth(m.get("imageWidth") == null ? null : Integer.valueOf(String.valueOf(m.get("imageWidth")))); } catch (Exception ignore) {}
                    try { cb.setImageHeight(m.get("imageHeight") == null ? null : Integer.valueOf(String.valueOf(m.get("imageHeight")))); } catch (Exception ignore) {}
                    blocks.add(cb);
                }
            }

            // 保存
            KnowledgeArticle saved = articleService.updateArticle(articleId, existing, blocks);
            return ResponseEntity.ok(toArticleResponse(saved));
        } catch (Exception e) {
            log.error("更新文章失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据ID获取文章（含内容块）
     */
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long articleId) {
        log.info("获取文章: {}", articleId);
        try {
            KnowledgeArticle a = articleService.getArticleById(articleId);
            if (a == null) return ResponseEntity.notFound().build();
            ArticleResponse r = toArticleResponse(a);
            // 若需要可在此处加载并附加内容块到响应（当前 ArticleResponse 已包含 contentBlocks 字段）
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            log.error("获取文章失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 分页获取文章列表
     */
    @GetMapping
    public ResponseEntity<PageResponse<ArticleResponse>> getArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String auditStatus) {
        log.info("分页获取文章列表: page={}, size={}, keyword={}, categoryId={}, status={}, auditStatus={}",
                page, size, keyword, categoryId, status, auditStatus);
        
        Sort sort = Sort.by("DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<KnowledgeArticle> articlePage;
        try {
            ArticleStatus articleStatus = null;
            AuditStatus as = null;
            if (status != null && !status.isBlank()) {
                articleStatus = ArticleStatus.valueOf(status.trim().toUpperCase());
            }
            if (auditStatus != null && !auditStatus.isBlank()) {
                as = AuditStatus.valueOf(auditStatus.trim().toUpperCase());
            }
            articlePage = articleService.getArticlesCombined(keyword, categoryId, articleStatus, as, pageable);
        } catch (IllegalArgumentException e) {
            // 枚举转换失败，返回空结果，避免500
            log.warn("筛选参数无效: status={}, auditStatus={}", status, auditStatus);
            articlePage = Page.empty(pageable);
        } catch (Exception e) {
            log.error("获取文章列表失败", e);
            return ResponseEntity.internalServerError().body(PageResponse.empty());
        }
        
        Page<ArticleResponse> mapped = articlePage.map(this::toArticleResponse);
        PageResponse<ArticleResponse> body = PageResponse.of(mapped);
        return ResponseEntity.ok(body);
    }
    
    private ArticleResponse toArticleResponse(KnowledgeArticle a) {
        ArticleResponse r = new ArticleResponse();
        r.setId(a.getId());
        r.setTitle(a.getTitle());
        r.setContent(a.getContent());
        r.setCategoryId(a.getCategoryId());
        r.setCategoryName(a.getCategory() != null ? a.getCategory().getName() : null);
        r.setAuthorId(a.getAuthorId());
        r.setAuthorName(a.getAuthor() != null ? safeDisplayName(a) : null);
        r.setSource(a.getSource());
        r.setViewCount(a.getViewCount());
        r.setLikeCount(a.getLikeCount());
        r.setDislikeCount(a.getDislikeCount());
        r.setRatingScore(a.getRatingScore());
        r.setRatingCount(a.getRatingCount());
        r.setStatus(a.getStatus());
        r.setAuditStatus(a.getAuditStatus());
        r.setAuditComment(a.getAuditComment());
        r.setPublishTime(a.getPublishTime());
        r.setLastEditTime(a.getLastEditTime());
        r.setCreatedAt(a.getCreatedAt());
        r.setUpdatedAt(a.getUpdatedAt());
        // 内容块
        if (a.getContentBlocks() != null && !a.getContentBlocks().isEmpty()) {
            java.util.List<ArticleResponse.ContentBlockResponse> list = a.getContentBlocks().stream()
                .sorted(java.util.Comparator.comparingInt(com.jiayan.quitsmoking.entity.KnowledgeContentBlock::getContentOrder))
                .map(cb -> {
                    ArticleResponse.ContentBlockResponse cr = new ArticleResponse.ContentBlockResponse();
                    cr.setId(cb.getId());
                    cr.setBlockType(cb.getBlockType() != null ? cb.getBlockType().getCode() : null);
                    cr.setContentOrder(cb.getContentOrder());
                    cr.setTextContent(cb.getTextContent());
                    cr.setImageUrl(cb.getImageUrl());
                    cr.setImageAlt(cb.getImageAlt());
                    cr.setImageWidth(cb.getImageWidth());
                    cr.setImageHeight(cb.getImageHeight());
                    return cr;
                })
                .toList();
            r.setContentBlocks(list);
        }
        return r;
    }
    
    private String safeDisplayName(KnowledgeArticle a) {
        try {
            return a.getAuthor() != null && a.getAuthor().getNickname() != null
                    ? a.getAuthor().getNickname()
                    : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 根据分类ID获取文章
     */
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<PageResponse<ArticleResponse>> getArticlesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("根据分类获取文章: categoryId={}, page={}, size={}", categoryId, page, size);
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ArticleResponse> mapped = articleService.getArticlesByCategory(categoryId, pageable)
                .map(this::toArticleResponse);
        return ResponseEntity.ok(PageResponse.of(mapped));
    }
    
    /**
     * 搜索文章
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ArticleResponse>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("搜索文章: keyword={}, page={}, size={}", keyword, page, size);
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ArticleResponse> mapped = articleService.searchArticles(keyword, pageable)
                .map(this::toArticleResponse);
        return ResponseEntity.ok(PageResponse.of(mapped));
    }
    
    /**
     * 获取热门文章
     */
    @GetMapping("/popular")
    public ResponseEntity<PageResponse<ArticleResponse>> getPopularArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取热门文章: page={}, size={}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleResponse> mapped = articleService.getPopularArticles(pageable)
                .map(this::toArticleResponse);
        return ResponseEntity.ok(PageResponse.of(mapped));
    }
    
    /**
     * 获取最新文章
     */
    @GetMapping("/latest")
    public ResponseEntity<PageResponse<ArticleResponse>> getLatestArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取最新文章: page={}, size={}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleResponse> mapped = articleService.getLatestArticles(pageable)
                .map(this::toArticleResponse);
        return ResponseEntity.ok(PageResponse.of(mapped));
    }
    
    /**
     * 增加文章浏览次数
     */
    @PostMapping("/{articleId}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long articleId) {
        log.info("增加文章浏览次数: {}", articleId);
        
        // TODO: 实现浏览次数增加逻辑
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * 点赞文章
     */
    @PostMapping("/{articleId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> likeArticle(@PathVariable Long articleId) {
        log.info("点赞文章: {}", articleId);
        
        // TODO: 实现文章点赞逻辑
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * 取消点赞文章
     */
    @DeleteMapping("/{articleId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlikeArticle(@PathVariable Long articleId) {
        log.info("取消点赞文章: {}", articleId);
        
        // TODO: 实现取消点赞逻辑
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * 提交文章审核
     */
    @PostMapping("/{articleId}/submit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<Void> submitForAudit(@PathVariable Long articleId) {
        log.info("提交文章审核: {}", articleId);
        
        // TODO: 实现文章提交审核逻辑
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * 审核文章
     */
    @PostMapping("/{articleId}/audit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public ResponseEntity<Void> auditArticle(
            @PathVariable Long articleId,
            @RequestParam String auditStatus,
            @RequestParam(required = false) String comment) {
        log.info("审核文章: {} -> {}", articleId, auditStatus);
        
        // TODO: 实现文章审核逻辑
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * 发布文章
     */
    @PostMapping("/{articleId}/publish")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
    public ResponseEntity<Void> publishArticle(@PathVariable Long articleId) {
        log.info("发布文章: {}", articleId);
        
        // TODO: 实现文章发布逻辑
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * 禁用文章
     */
    @PostMapping("/{articleId}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> banArticle(
            @PathVariable Long articleId,
            @RequestParam String reason) {
        log.info("禁用文章: {} -> {}", articleId, reason);
        
        // TODO: 实现文章禁用逻辑
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * 恢复文章
     */
    @PostMapping("/{articleId}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> restoreArticle(@PathVariable Long articleId) {
        log.info("恢复文章: {}", articleId);
        
        // TODO: 实现文章恢复逻辑
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * 删除文章
     */
    @DeleteMapping("/{articleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long articleId) {
        log.info("删除文章: {}", articleId);
        
        // TODO: 实现文章删除逻辑
        
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取文章评论列表
     */
    @GetMapping("/{articleId}/comments")
    public ResponseEntity<PageResponse<CommentResponse>> getArticleComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取文章评论: articleId={}, page={}, size={}", articleId, page, size);
        
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // 调用评论服务获取文章评论
            Page<KnowledgeComment> commentPage = commentService.getCommentsByArticle(articleId, pageable);
            
            // 转换为CommentResponse
            Page<CommentResponse> comments = commentPage.map(this::toCommentResponse);
            return ResponseEntity.ok(PageResponse.of(comments));
        } catch (Exception e) {
            log.error("获取文章评论失败: articleId={}", articleId, e);
            return ResponseEntity.internalServerError().body(PageResponse.empty());
        }
    }

    /**
     * 获取文章评分信息
     */
    @GetMapping("/{articleId}/rating")
    public ResponseEntity<Map<String, Object>> getArticleRating(@PathVariable Long articleId) {
        log.info("获取文章评分: articleId={}", articleId);
        
        try {
            // 调用评分服务获取文章评分统计
            Double averageRating = ratingService.calculateAverageRating(articleId);
            Map<Integer, Long> distribution = ratingService.getRatingDistribution(articleId);
            
            // 添加空值检查
            if (averageRating == null) {
                averageRating = 0.0;
            }
            
            if (distribution == null) {
                distribution = Map.of(1, 0L, 2, 0L, 3, 0L, 4, 0L, 5, 0L);
            }
            
            if (averageRating == 0.0 && distribution.values().stream().allMatch(count -> count == 0L)) {
                // 如果没有评分数据，返回默认值
                Map<String, Object> data = new HashMap<>();
                data.put("averageRating", 0.0);
                data.put("totalRatings", 0L);
                data.put("ratingDistribution", Map.of(1, 0L, 2, 0L, 3, 0L, 4, 0L, 5, 0L));
                data.put("userRating", null);
                
                Map<String, Object> response = Map.of(
                    "code", 200,
                    "message", "获取文章评分成功",
                    "data", data,
                    "timestamp", LocalDateTime.now().toString()
                );
                return ResponseEntity.ok(response);
            }
            
            // 获取评分总数
            Long totalRatings = distribution.values().stream().mapToLong(Long::longValue).sum();
            
            Map<String, Object> data = new HashMap<>();
            data.put("averageRating", averageRating);
            data.put("totalRatings", totalRatings);
            data.put("ratingDistribution", distribution);
            data.put("userRating", null);
            
            Map<String, Object> response = Map.of(
                "code", 200,
                "message", "获取文章评分成功",
                "data", data,
                "timestamp", LocalDateTime.now().toString()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取文章评分失败: articleId={}", articleId, e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "error", "获取文章评分失败",
                "message", "系统内部错误",
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }

    /**
     * 获取文章评论统计
     */
    @GetMapping("/{articleId}/comments/stats")
    public ResponseEntity<Map<String, Object>> getArticleCommentStats(@PathVariable Long articleId) {
        log.info("获取文章评论统计: articleId={}", articleId);
        
        try {
            // 调用评论服务获取评论统计
            Pageable pageable = PageRequest.of(0, 1000); // 获取足够多的评论来计算统计
            Page<KnowledgeComment> comments = commentService.getCommentsByArticle(articleId, pageable);
            
            long totalComments = comments.getTotalElements();
            long activeComments = comments.getContent().stream()
                    .filter(c -> c.getStatus() == CommentStatus.ACTIVE)
                    .count();
            long hiddenComments = comments.getContent().stream()
                    .filter(c -> c.getStatus() != CommentStatus.ACTIVE)
                    .count();
            
            // 计算总点赞数
            long totalLikes = comments.getContent().stream()
                    .mapToLong(c -> c.getLikeCount() != null ? c.getLikeCount() : 0)
                    .sum();
            
            // 计算有用评论数
            long helpfulComments = comments.getContent().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsHelpful()))
                    .count();
            
            // 计算最近评论数（最近7天）
            LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
            long recentComments = comments.getContent().stream()
                    .filter(c -> c.getCreatedAt() != null && c.getCreatedAt().isAfter(weekAgo))
                    .count();
            
            // 判断评论趋势
            String commentTrend = recentComments > totalComments / 4 ? "increasing" : 
                                 recentComments > 0 ? "stable" : "none";
            
            Map<String, Object> stats = Map.of(
                "totalComments", totalComments,
                "activeComments", activeComments,
                "hiddenComments", hiddenComments,
                "totalLikes", totalLikes,
                "helpfulComments", helpfulComments,
                "recentComments", recentComments,
                "commentTrend", commentTrend
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("获取文章评论统计失败: articleId={}", articleId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取文章评分统计
     */
    @GetMapping("/{articleId}/rating/stats")
    public ResponseEntity<Map<String, Object>> getArticleRatingStats(@PathVariable Long articleId) {
        log.info("获取文章评分统计: articleId={}", articleId);
        
        try {
            // 调用评分服务获取评分统计
            Double averageRating = ratingService.calculateAverageRating(articleId);
            Map<Integer, Long> distribution = ratingService.getRatingDistribution(articleId);
            
            if (averageRating == null) {
                // 如果没有评分数据，返回默认值
                Map<String, Object> stats = Map.of(
                    "averageRating", 0.0,
                    "totalRatings", 0L,
                    "ratingDistribution", Map.of(1, 0L, 2, 0L, 3, 0L, 4, 0L, 5, 0L),
                    "ratingTrend", "none",
                    "recentRatings", 0L,
                    "topRated", false,
                    "ratingPercentile", 0
                );
                return ResponseEntity.ok(stats);
            }
            
            // 获取评分总数
            Long totalRatings = distribution.values().stream().mapToLong(Long::longValue).sum();
            
            // 判断是否为高分文章（平均分>=4.0）
            boolean topRated = averageRating >= 4.0;
            
            // 计算评分百分位（简化计算）
            int ratingPercentile = (int) (averageRating / 5.0 * 100);
            
            // 获取最近评分数（简化处理）
            Long recentRatings = totalRatings > 0 ? Math.min(totalRatings, 10L) : 0L;
            
            // 判断评分趋势（简化处理）
            String ratingTrend = totalRatings > 10 ? "stable" : totalRatings > 0 ? "growing" : "none";
            
            Map<String, Object> stats = Map.of(
                "averageRating", averageRating,
                "totalRatings", totalRatings,
                "ratingDistribution", distribution,
                "ratingTrend", ratingTrend,
                "recentRatings", recentRatings,
                "topRated", topRated,
                "ratingPercentile", ratingPercentile
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("获取文章评分统计失败: articleId={}", articleId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private Long resolveAuthorId() {
        // 优先使用ID=1
        return userRepository.findById(1L).map(User::getId)
                // 取任意一个现有用户
                .orElseGet(() -> userRepository.findAll(PageRequest.of(0, 1))
                        .stream().findFirst().map(User::getId)
                        // 若无用户，则创建系统用户
                        .orElseGet(this::createSystemUser));
    }

    /**
     * 将KnowledgeComment转换为CommentResponse
     */
    private CommentResponse toCommentResponse(KnowledgeComment comment) {
        if (comment == null) {
            return null;
        }
        
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setArticleId(comment.getArticleId());
        response.setUserId(comment.getUserId());
        response.setUserName("用户" + comment.getUserId()); // 简化处理，使用用户ID作为名称
        response.setUserAvatar(null); // 暂时不设置头像
        response.setParentId(comment.getParentId());
        response.setContent(comment.getContent());
        response.setLikeCount(comment.getLikeCount());
        response.setIsHelpful(comment.getIsHelpful());
        response.setStatus(comment.getStatus());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        
        // 暂时不设置图片和回复，避免复杂关联查询
        response.setImages(null);
        response.setReplies(null);
        
        return response;
    }

    private Long createSystemUser() {
        User u = new User();
        u.setNickname("系统用户");
        u.setEnabled(true);
        u.setCanCreatePosts(true);
        u.setMemberLevel("free");
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        User saved = userRepository.save(u);
        return saved.getId();
    }
} 