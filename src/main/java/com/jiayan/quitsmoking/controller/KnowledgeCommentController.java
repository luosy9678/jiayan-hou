package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.dto.*;
import com.jiayan.quitsmoking.service.KnowledgeCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import com.jiayan.quitsmoking.entity.KnowledgeComment;
import com.jiayan.quitsmoking.enums.CommentStatus;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import java.util.Map;
import com.jiayan.quitsmoking.repository.UserRepository;
import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.util.JwtUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import com.jiayan.quitsmoking.exception.BusinessException;

/**
 * 评论控制器- 专门用于前端用户评论
 * 注意：此控制器不包含管理员功能，管理员功能请使用AdminController
 */
@RestController
@RequestMapping("/api/v1/knowledge/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class KnowledgeCommentController {
    
    private final KnowledgeCommentService commentService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    /**
     * 创建评论
     * 前端用户必须提供有效的JWT Token
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createComment(
            @Valid @RequestBody CreateCommentRequest request) {
        log.info("创建评论: articleId={}", request.getArticleId());
        
        try {
            // 获取当前登录用户ID（必须从JWT Token获取）
            Long userId = getCurrentUserId();
            log.info("当前用户ID: {}", userId);
            
            // 创建评论实体
            KnowledgeComment comment = new KnowledgeComment();
            comment.setArticleId(request.getArticleId());
            comment.setUserId(userId);
            comment.setParentId(request.getParentId());
            comment.setContent(request.getContent());
            comment.setLikeCount(0);
            comment.setIsHelpful(false);
            comment.setStatus(CommentStatus.ACTIVE);
            comment.setIsDeleted(false);
            
            // 调用评论服务创建评论
            KnowledgeComment savedComment = commentService.createComment(comment, null);
            
            // 转换为响应DTO
            CommentResponse response = toCommentResponse(savedComment);
            
            // 返回统一的响应格式
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "评论创建成功",
                "data", response,
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (BusinessException e) {
            log.error("创建评论失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "code", 400,
                        "error", "创建评论失败",
                        "message", e.getMessage(),
                        "timestamp", LocalDateTime.now().toString()
                    ));
        } catch (Exception e) {
            log.error("创建评论失败", e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "code", 400,
                        "error", "创建评论失败",
                        "message", "系统内部错误",
                        "timestamp", LocalDateTime.now().toString()
                    ));
        }
    }
    
    /**
     * 更新评论
     */
    @PutMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CreateCommentRequest request) {
        log.info("更新评论: {}", commentId);
        
        try {
            // 调用评论服务更新评论
            KnowledgeComment updatedComment = commentService.updateComment(commentId, null, null);
            
            if (updatedComment == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 转换为响应DTO
            CommentResponse response = toCommentResponse(updatedComment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("更新评论失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据ID获取评论
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long commentId) {
        log.info("获取评论: {}", commentId);
        
        try {
            // TODO: 实现评论获取逻辑
            // 由于评论服务尚未完全实现，暂时返回模拟数据
            log.warn("评论获取功能尚未完全实现，返回模拟数据");
            
            CommentResponse response = new CommentResponse();
            response.setId(commentId);
            response.setArticleId(1L);
            response.setUserId(1L);
            response.setUserName("测试用户");
            response.setContent("这是一个测试评论内容");
            response.setParentId(null);
            response.setLikeCount(5);
            response.setIsHelpful(true);
            response.setStatus(CommentStatus.ACTIVE);
            response.setCreatedAt(LocalDateTime.now());
            response.setUpdatedAt(LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取评论失败", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 根据文章ID获取评论列表
     */
    @GetMapping("/by-article/{articleId}")
    public ResponseEntity<?> getCommentsByArticle(
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
            
            // 返回统一的响应格式
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取评论成功",
                "data", PageResponse.of(comments),
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("获取文章评论失败", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "error", "获取评论失败",
                "message", "系统内部错误",
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }
    
    /**
     * 根据用户ID获取评论列表
     */
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> getCommentsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取用户评论: userId={}, page={}, size={}", userId, page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<KnowledgeComment> commentPage = commentService.getCommentsByUser(userId, pageable);
            
            // 转换为CommentResponse
            Page<CommentResponse> comments = commentPage.map(this::toCommentResponse);
            
            // 返回统一的响应格式
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取用户评论成功",
                "data", PageResponse.of(comments),
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("获取用户评论失败", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "error", "获取用户评论失败",
                "message", "系统内部错误",
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }
    
    /**
     * 获取评论树结构
     */
    @GetMapping("/tree/{articleId}")
    public ResponseEntity<?> getCommentTree(@PathVariable Long articleId) {
        log.info("获取评论树 articleId={}", articleId);
        
        try {
            // 调用评论服务获取评论树
            List<KnowledgeComment> commentTree = commentService.getCommentTree(articleId);
            
            // 转换为响应DTO
            List<CommentResponse> responses = commentTree.stream()
                    .map(this::toCommentResponse)
                    .collect(Collectors.toList());
            
            // 返回统一的响应格式
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取评论树成功",
                "data", responses,
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("获取评论树失败", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "error", "获取评论树失败",
                "message", "系统内部错误",
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }
    
    /**
     * 点赞评论
     */
    @PostMapping("/{commentId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> likeComment(@PathVariable Long commentId) {
        log.info("点赞评论: {}", commentId);
        
        try {
            // 调用评论服务增加点赞数
            commentService.updateLikeCount(commentId, 1);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("点赞评论失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 取消点赞评论
     */
    @DeleteMapping("/{commentId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId) {
        log.info("取消点赞评论: {}", commentId);
        
        try {
            // 调用评论服务减少点赞数
            commentService.updateLikeCount(commentId, -1);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("取消点赞评论失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 标记评论为有用
     */
    @PostMapping("/{commentId}/helpful")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markCommentAsHelpful(@PathVariable Long commentId) {
        log.info("标记评论为有用 {}", commentId);
        
        try {
            // 调用评论服务标记为有用
            commentService.markCommentAsHelpful(commentId, getCurrentUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("标记评论为有用失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 取消标记评论为有用
     */
    @DeleteMapping("/{commentId}/helpful")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unmarkCommentAsHelpful(@PathVariable Long commentId) {
        log.info("取消标记评论为有用 {}", commentId);
        
        try {
            // TODO: 实现取消标记逻辑
            log.warn("取消标记有用功能尚未完全实现");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("取消标记评论为有用失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 隐藏评论
     */
    @PostMapping("/{commentId}/hide")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> hideComment(
            @PathVariable Long commentId,
            @RequestParam String reason) {
        log.info("隐藏评论: {} -> {}", commentId, reason);
        
        try {
            // 调用评论服务隐藏评论
            commentService.hideComment(commentId, reason, getCurrentUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("隐藏评论失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 恢复评论
     */
    @PostMapping("/{commentId}/restore")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> restoreComment(@PathVariable Long commentId) {
        log.info("恢复评论: {}", commentId);
        
        try {
            // 调用评论服务恢复评论
            commentService.restoreComment(commentId, getCurrentUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("恢复评论失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        log.info("删除评论: {}", commentId);
        
        try {
            // 调用评论服务软删除评论
            commentService.softDeleteComment(commentId, getCurrentUserId(), "管理员删除");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取热门评论
     */
    @GetMapping("/popular")
    public ResponseEntity<PageResponse<CommentResponse>> getPopularComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取热门评论: page={}, size={}", page, size);
        
        try {
            // 由于评论服务没有热门评论方法，暂时返回空结果
            // TODO: 实现热门评论查询逻辑（按点赞数排序）
            log.warn("热门评论查询功能需要扩展评论服务");
            return ResponseEntity.ok(PageResponse.empty());
        } catch (Exception e) {
            log.error("获取热门评论失败", e);
            return ResponseEntity.internalServerError().body(PageResponse.empty());
        }
    }
    
    /**
     * 获取最新评论
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取最新评论 page={}, size={}", page, size);
        
        try {
            // 由于评论服务没有最新评论方法，暂时返回空结构
            // TODO: 实现最新评论查询逻辑（按创建时间排序）
            log.warn("最新评论查询功能需要扩展评论服务");
            
            // 返回统一的响应格式
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取最新评论成功",
                "data", PageResponse.empty(),
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("获取最新评论失败", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "error", "获取最新评论失败",
                "message", "系统内部错误",
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }
    
    /**
     * 批量更新评论状态
     */
    @PutMapping("/batch/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> batchUpdateCommentStatus(
            @RequestParam List<Long> commentIds,
            @RequestParam String status) {
        log.info("批量更新评论状态 {} -> {}", commentIds, status);
        
        try {
            // 由于评论服务没有批量更新方法，暂时返回成功
            // TODO: 实现批量状态更新逻辑
            log.warn("批量状态更新功能需要扩展评论服务");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("批量更新评论状态失败", e);
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
            log.error("获取用户ID时发生异常? {}", e.getMessage(), e);
            throw new BusinessException("用户认证失败");
        }
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
} 
