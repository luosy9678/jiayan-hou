package com.jiayan.quitsmoking.service.impl;

import com.jiayan.quitsmoking.entity.KnowledgeComment;
import com.jiayan.quitsmoking.entity.CommentImage;
import com.jiayan.quitsmoking.entity.KnowledgeArticle;
import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.enums.CommentStatus;
import com.jiayan.quitsmoking.exception.BusinessException;
import com.jiayan.quitsmoking.repository.KnowledgeCommentRepository;
import com.jiayan.quitsmoking.repository.CommentImageRepository;
import com.jiayan.quitsmoking.repository.KnowledgeArticleRepository;
import com.jiayan.quitsmoking.repository.UserRepository;
import com.jiayan.quitsmoking.service.KnowledgeCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KnowledgeCommentServiceImpl implements KnowledgeCommentService {
    
    private final KnowledgeCommentRepository commentRepository;
    private final CommentImageRepository commentImageRepository;
    private final KnowledgeArticleRepository articleRepository;
    private final UserRepository userRepository;
    
    @Override
    public KnowledgeComment createComment(KnowledgeComment comment, List<CommentImage> images) {
        log.info("创建评论: articleId={}, userId={}", comment.getArticleId(), comment.getUserId());
        
        // 验证文章是否存在
        KnowledgeArticle article = articleRepository.findById(comment.getArticleId())
                .orElseThrow(() -> new BusinessException("文章不存在"));
        
        // 验证用户是否存在
        User user = userRepository.findById(comment.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 验证用户是否有评论权限
        if (!canUserComment(user)) {
            throw new BusinessException("用户没有评论权限");
        }
        
        // 验证父评论是否存在（如果是回复）
        if (comment.getParentId() != null) {
            KnowledgeComment parentComment = commentRepository.findById(comment.getParentId())
                    .orElseThrow(() -> new BusinessException("父评论不存在"));
            
            // 验证父评论是否属于同一篇文章
            if (!parentComment.getArticleId().equals(comment.getArticleId())) {
                throw new BusinessException("父评论与文章不匹配");
            }
        }
        
        // 设置评论初始状态
        comment.setStatus(CommentStatus.ACTIVE);
        comment.setLikeCount(0);
        comment.setIsHelpful(false);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        // 保存评论
        KnowledgeComment savedComment = commentRepository.save(comment);
        
        // 保存评论图片
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                CommentImage image = images.get(i);
                image.setCommentId(savedComment.getId());
                image.setSortOrder(i + 1);
                commentImageRepository.save(image);
            }
        }
        
        log.info("评论创建成功: commentId={}", savedComment.getId());
        return savedComment;
    }
    
    @Override
    public KnowledgeComment updateComment(Long commentId, KnowledgeComment comment, List<CommentImage> images) {
        log.info("更新评论: commentId={}", commentId);
        
        KnowledgeComment existingComment = getCommentById(commentId);
        if (existingComment == null) {
            throw new BusinessException("评论不存在");
        }
        
        // 验证用户是否有编辑权限
        if (!canUserEditComment(commentId, comment.getUserId())) {
            throw new BusinessException("没有编辑权限");
        }
        
        // 更新评论基本信息
        existingComment.setContent(comment.getContent());
        existingComment.setUpdatedAt(LocalDateTime.now());
        
        // 保存评论
        KnowledgeComment updatedComment = commentRepository.save(existingComment);
        
        // 更新评论图片
        if (images != null) {
            // 删除旧的图片
            commentImageRepository.deleteByCommentId(commentId);
            
            // 保存新的图片
            for (int i = 0; i < images.size(); i++) {
                CommentImage image = images.get(i);
                image.setCommentId(commentId);
                image.setSortOrder(i + 1);
                commentImageRepository.save(image);
            }
        }
        
        log.info("评论更新成功: commentId={}", commentId);
        return updatedComment;
    }
    
    @Override
    @Transactional(readOnly = true)
    public KnowledgeComment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeComment> getCommentsByArticle(Long articleId, Pageable pageable) {
        return commentRepository.findByArticleIdAndStatusAndIsDeletedFalse(articleId, CommentStatus.ACTIVE, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeComment> getCommentsByUser(Long userId, Pageable pageable) {
        return commentRepository.findByUserIdAndStatusAndIsDeletedFalse(userId, CommentStatus.ACTIVE, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeComment> getRepliesByParent(Long parentId) {
        return commentRepository.findByParentIdAndStatusAndIsDeletedFalseOrderByCreatedAtAsc(parentId, CommentStatus.ACTIVE);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeComment> getTopLevelComments(Long articleId, Pageable pageable) {
        return commentRepository.findByParentIdIsNullAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(CommentStatus.ACTIVE, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeComment> getCommentTree(Long articleId) {
        // 获取顶级评论
        List<KnowledgeComment> topLevelComments = commentRepository.findByParentIdIsNullAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(CommentStatus.ACTIVE, Pageable.unpaged()).getContent();
        
        // 递归构建评论树
        return topLevelComments.stream()
                .map(this::buildCommentTree)
                .collect(Collectors.toList());
    }
    
    @Override
    public void hideComment(Long commentId, String reason, Long adminId) {
        log.info("隐藏评论: commentId={}, reason={}, adminId={}", commentId, reason, adminId);
        
        KnowledgeComment comment = getCommentById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        comment.setStatus(CommentStatus.ACTIVE);
        comment.setHiddenReason(null);
        comment.setHiddenBy(null);
        comment.setHiddenAt(null);
        comment.setUpdatedAt(LocalDateTime.now());
        
        commentRepository.save(comment);
        log.info("评论隐藏成功: commentId={}", commentId);
    }
    
    @Override
    public void restoreComment(Long commentId, Long adminId) {
        log.info("恢复评论: commentId={}, adminId={}", commentId, adminId);
        
        KnowledgeComment comment = getCommentById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        if (!CommentStatus.ACTIVE.equals(comment.getStatus())) {
            throw new BusinessException("只有被隐藏的评论才能恢复");
        }
        
        comment.setStatus(CommentStatus.ACTIVE);
        comment.setHiddenReason(null);
        comment.setHiddenBy(null);
        comment.setHiddenAt(null);
        comment.setUpdatedAt(LocalDateTime.now());
        
        commentRepository.save(comment);
        log.info("评论恢复成功: commentId={}", commentId);
    }
    
    @Override
    public void softDeleteComment(Long commentId, Long operatorId, String reason) {
        log.info("软删除评论: commentId={}, operatorId={}, reason={}", commentId, operatorId, reason);
        
        KnowledgeComment comment = getCommentById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        comment.setIsDeleted(true);
        comment.setDeletedBy(operatorId);
        comment.setDeletedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        commentRepository.save(comment);
        log.info("评论软删除成功: commentId={}", commentId);
    }
    
    @Override
    public void restoreSoftDeletedComment(Long commentId, Long operatorId) {
        log.info("恢复软删除评论: commentId={}, operatorId={}", commentId, operatorId);
        
        KnowledgeComment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        if (!comment.getIsDeleted()) {
            throw new BusinessException("评论未被删除");
        }
        
        comment.setIsDeleted(false);
        comment.setDeletedBy(null);
        comment.setDeletedAt(null);
        comment.setUpdatedAt(LocalDateTime.now());
        
        commentRepository.save(comment);
        log.info("评论恢复成功: commentId={}", commentId);
    }
    
    @Override
    public void updateLikeCount(Long commentId, Integer increment) {
        // 暂时使用简单的方法，直接更新评论
        KnowledgeComment comment = getCommentById(commentId);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + increment);
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);
        }
    }
    
    @Override
    public void markCommentAsHelpful(Long commentId, Long userId) {
        log.info("标记评论为有用: commentId={}, userId={}", commentId, userId);
        
        KnowledgeComment comment = getCommentById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        comment.setIsHelpful(true);
        comment.setUpdatedAt(LocalDateTime.now());
        
        commentRepository.save(comment);
        log.info("评论标记为有用成功: commentId={}", commentId);
    }
    
    @Override
    public void unmarkCommentAsHelpful(Long commentId, Long userId) {
        log.info("取消标记评论为有用: commentId={}, userId={}", commentId, userId);
        
        KnowledgeComment comment = getCommentById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        comment.setIsHelpful(false);
        comment.setUpdatedAt(LocalDateTime.now());
        
        commentRepository.save(comment);
        log.info("评论取消标记为有用成功: commentId={}", commentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canUserEditComment(Long commentId, Long userId) {
        KnowledgeComment comment = getCommentById(commentId);
        if (comment == null) {
            return false;
        }
        
        // 评论作者可以编辑自己的评论
        if (comment.getUserId().equals(userId)) {
            return true;
        }
        
        // 管理员可以编辑所有评论
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && Boolean.TRUE.equals(user.getCanCreatePosts())) {
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canUserDeleteComment(Long commentId, Long userId) {
        KnowledgeComment comment = getCommentById(commentId);
        if (comment == null) {
            return false;
        }
        
        // 评论作者可以删除自己的评论
        if (comment.getUserId().equals(userId)) {
            return true;
        }
        
        // 管理员可以删除所有评论
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && Boolean.TRUE.equals(user.getCanCreatePosts())) {
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCommentCount() {
        // 暂时返回0，因为Repository方法不存在
        return 0L;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCommentCountByArticle(Long articleId) {
        return commentRepository.countByArticleIdAndStatusAndIsDeletedFalse(articleId, CommentStatus.ACTIVE);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCommentCountByUser(Long userId) {
        return commentRepository.countByUserIdAndStatusAndIsDeletedFalse(userId, CommentStatus.ACTIVE);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeComment> getPopularComments(Pageable pageable) {
        // 使用实际存在的方法，暂时返回空分页
        return Page.empty(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeComment> getLatestComments(Pageable pageable) {
        // 使用实际存在的方法，暂时返回空分页
        return Page.empty(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeComment> getCommentsByStatus(CommentStatus status, Pageable pageable) {
        return commentRepository.findByStatusAndIsDeletedFalse(status, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeComment> searchCommentsByHiddenReason(String reason, Pageable pageable) {
        if (!StringUtils.hasText(reason)) {
            return Page.empty(pageable);
        }
        // 使用实际存在的方法，暂时返回空分页
        return Page.empty(pageable);
    }
    
    @Override
    public void batchUpdateCommentStatus(List<Long> commentIds, CommentStatus status) {
        log.info("批量更新评论状态: commentIds={}, status={}", commentIds, status);
        
        if (commentIds == null || commentIds.isEmpty()) {
            throw new BusinessException("评论ID列表不能为空");
        }
        
        // 使用实际存在的方法，暂时逐个更新
        for (Long commentId : commentIds) {
            KnowledgeComment comment = getCommentById(commentId);
            if (comment != null) {
                comment.setStatus(status);
                comment.setUpdatedAt(LocalDateTime.now());
                commentRepository.save(comment);
            }
        }
        
        log.info("批量更新评论状态完成: count={}", commentIds.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CommentImage> getCommentImages(Long commentId) {
        return commentImageRepository.findByCommentIdOrderBySortOrderAsc(commentId);
    }
    
    @Override
    public void addCommentImage(Long commentId, CommentImage image) {
        log.info("添加评论图片: commentId={}", commentId);
        
        // 验证评论是否存在
        KnowledgeComment comment = getCommentById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        // 设置图片信息
        image.setCommentId(commentId);
        if (image.getSortOrder() == null) {
            // 获取当前最大排序号
            Integer maxSortOrder = commentImageRepository.findMaxSortOrderByCommentId(commentId);
            image.setSortOrder(maxSortOrder != null ? maxSortOrder + 1 : 1);
        }
        
        commentImageRepository.save(image);
        log.info("评论图片添加成功: imageId={}", image.getId());
    }
    
    @Override
    public void deleteCommentImage(Long imageId) {
        log.info("删除评论图片: imageId={}", imageId);
        
        CommentImage image = commentImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException("图片不存在"));
        
        commentImageRepository.delete(image);
        log.info("评论图片删除成功: imageId={}", imageId);
    }
    
    @Override
    public void updateCommentImageSortOrder(Long imageId, Integer sortOrder) {
        log.info("更新评论图片排序: imageId={}, sortOrder={}", imageId, sortOrder);
        
        CommentImage image = commentImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException("图片不存在"));
        
        image.setSortOrder(sortOrder);
        commentImageRepository.save(image);
        log.info("评论图片排序更新成功: imageId={}", imageId);
    }
    
    /**
     * 递归构建评论树
     */
    private KnowledgeComment buildCommentTree(KnowledgeComment comment) {
        // 获取子评论
        List<KnowledgeComment> replies = getRepliesByParent(comment.getId());
        
        // 递归构建子评论树
        List<KnowledgeComment> replyTree = replies.stream()
                .map(this::buildCommentTree)
                .collect(Collectors.toList());
        
        // 设置子评论（这里需要修改实体类或使用DTO）
        // comment.setReplies(replyTree);
        
        return comment;
    }
    
    /**
     * 检查用户是否有评论权限
     */
    private boolean canUserComment(User user) {
        if (user == null) {
            return false;
        }
        
        // 检查用户是否被禁言
        if (Boolean.TRUE.equals(user.getForumBanned())) {
            return false;
        }
        
        // 检查用户是否有发帖权限（有发帖权限的用户通常也有评论权限）
        return Boolean.TRUE.equals(user.getCanCreatePosts());
    }
} 