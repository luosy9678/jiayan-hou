package com.jiayan.quitsmoking.service;

import com.jiayan.quitsmoking.entity.KnowledgeComment;
import com.jiayan.quitsmoking.entity.CommentImage;
import com.jiayan.quitsmoking.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 评论服务接口
 */
public interface KnowledgeCommentService {
    
    /**
     * 创建评论
     */
    KnowledgeComment createComment(KnowledgeComment comment, List<CommentImage> images);
    
    /**
     * 更新评论
     */
    KnowledgeComment updateComment(Long commentId, KnowledgeComment comment, List<CommentImage> images);
    
    /**
     * 根据ID获取评论
     */
    KnowledgeComment getCommentById(Long commentId);
    
    /**
     * 根据文章ID获取评论列表
     */
    Page<KnowledgeComment> getCommentsByArticle(Long articleId, Pageable pageable);
    
    /**
     * 根据用户ID获取评论列表
     */
    Page<KnowledgeComment> getCommentsByUser(Long userId, Pageable pageable);
    
    /**
     * 根据父评论ID获取回复列表
     */
    List<KnowledgeComment> getRepliesByParent(Long parentId);
    
    /**
     * 获取文章的顶级评论
     */
    Page<KnowledgeComment> getTopLevelComments(Long articleId, Pageable pageable);
    
    /**
     * 获取评论树结构
     */
    List<KnowledgeComment> getCommentTree(Long articleId);
    
    /**
     * 隐藏评论
     */
    void hideComment(Long commentId, String reason, Long adminId);
    
    /**
     * 恢复评论
     */
    void restoreComment(Long commentId, Long adminId);
    
    /**
     * 软删除评论
     */
    void softDeleteComment(Long commentId, Long operatorId, String reason);
    
    /**
     * 恢复软删除的评论
     */
    void restoreSoftDeletedComment(Long commentId, Long operatorId);
    
    /**
     * 更新评论点赞数
     */
    void updateLikeCount(Long commentId, Integer increment);
    
    /**
     * 标记评论为有用
     */
    void markCommentAsHelpful(Long commentId, Long userId);
    
    /**
     * 取消标记评论为有用
     */
    void unmarkCommentAsHelpful(Long commentId, Long userId);
    
    /**
     * 检查用户是否有权限编辑评论
     */
    boolean canUserEditComment(Long commentId, Long userId);
    
    /**
     * 检查用户是否有权限删除评论
     */
    boolean canUserDeleteComment(Long commentId, Long userId);
    
    /**
     * 获取评论统计信息
     */
    long getCommentCount();
    
    /**
     * 获取指定文章的评论数量
     */
    long getCommentCountByArticle(Long articleId);
    
    /**
     * 获取指定用户的评论数量
     */
    long getCommentCountByUser(Long userId);
    
    /**
     * 获取热门评论
     */
    Page<KnowledgeComment> getPopularComments(Pageable pageable);
    
    /**
     * 获取最新评论
     */
    Page<KnowledgeComment> getLatestComments(Pageable pageable);
    
    /**
     * 根据状态获取评论
     */
    Page<KnowledgeComment> getCommentsByStatus(CommentStatus status, Pageable pageable);
    
    /**
     * 根据隐藏原因搜索评论
     */
    Page<KnowledgeComment> searchCommentsByHiddenReason(String reason, Pageable pageable);
    
    /**
     * 批量更新评论状态
     */
    void batchUpdateCommentStatus(List<Long> commentIds, CommentStatus status);
    
    /**
     * 获取评论的图片列表
     */
    List<CommentImage> getCommentImages(Long commentId);
    
    /**
     * 添加评论图片
     */
    void addCommentImage(Long commentId, CommentImage image);
    
    /**
     * 删除评论图片
     */
    void deleteCommentImage(Long imageId);
    
    /**
     * 更新评论图片排序
     */
    void updateCommentImageSortOrder(Long imageId, Integer sortOrder);
} 