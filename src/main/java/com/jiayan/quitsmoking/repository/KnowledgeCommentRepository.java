package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.KnowledgeComment;
import com.jiayan.quitsmoking.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 评论数据访问层
 */
@Repository
public interface KnowledgeCommentRepository extends JpaRepository<KnowledgeComment, Long>, JpaSpecificationExecutor<KnowledgeComment> {
    
    /**
     * 根据文章ID查找评论
     */
    Page<KnowledgeComment> findByArticleIdAndStatusAndIsDeletedFalse(Long articleId, CommentStatus status, Pageable pageable);
    
    /**
     * 根据用户ID查找评论
     */
    Page<KnowledgeComment> findByUserIdAndStatusAndIsDeletedFalse(Long userId, CommentStatus status, Pageable pageable);
    
    /**
     * 根据父评论ID查找回复
     */
    List<KnowledgeComment> findByParentIdAndStatusAndIsDeletedFalseOrderByCreatedAtAsc(Long parentId, CommentStatus status);
    
    /**
     * 查找顶级评论（parentId为null）
     */
    Page<KnowledgeComment> findByParentIdIsNullAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(CommentStatus status, Pageable pageable);
    
    /**
     * 根据文章ID查找所有可见评论
     */
    @Query("SELECT c FROM KnowledgeComment c WHERE c.articleId = :articleId AND c.status = 'active' AND c.isDeleted = false " +
           "ORDER BY c.parentId ASC NULLS FIRST, c.createdAt ASC")
    List<KnowledgeComment> findVisibleCommentsByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 统计文章的评论数量
     */
    long countByArticleIdAndStatusAndIsDeletedFalse(Long articleId, CommentStatus status);
    
    /**
     * 统计用户的评论数量
     */
    long countByUserIdAndStatusAndIsDeletedFalse(Long userId, CommentStatus status);
    
    /**
     * 统计评论的回复数量
     */
    long countByParentIdAndStatusAndIsDeletedFalse(Long parentId, CommentStatus status);
    
    /**
     * 查找指定时间范围内的评论
     */
    @Query("SELECT c FROM KnowledgeComment c WHERE c.createdAt BETWEEN :startTime AND :endTime " +
           "AND c.status = :status AND c.isDeleted = false " +
           "ORDER BY c.createdAt DESC")
    Page<KnowledgeComment> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("status") CommentStatus status,
                                                 Pageable pageable);
    
    /**
     * 查找被隐藏的评论
     */
    Page<KnowledgeComment> findByStatusAndIsDeletedFalse(CommentStatus status, Pageable pageable);
    
    /**
     * 根据隐藏原因查找评论
     */
    @Query("SELECT c FROM KnowledgeComment c WHERE c.hiddenReason LIKE %:reason% AND c.status = 'hidden' AND c.isDeleted = false")
    Page<KnowledgeComment> findByHiddenReasonContaining(@Param("reason") String reason, Pageable pageable);
    
    /**
     * 更新评论点赞数
     */
    @Modifying
    @Query("UPDATE KnowledgeComment c SET c.likeCount = c.likeCount + :increment WHERE c.id = :commentId")
    void updateLikeCount(@Param("commentId") Long commentId, @Param("increment") Integer increment);
    
    /**
     * 隐藏评论
     */
    @Modifying
    @Query("UPDATE KnowledgeComment c SET c.status = 'hidden', c.hiddenReason = :reason, c.hiddenBy = :adminId, c.hiddenAt = :hiddenTime WHERE c.id = :commentId")
    void hideComment(@Param("commentId") Long commentId,
                    @Param("reason") String reason,
                    @Param("adminId") Long adminId,
                    @Param("hiddenTime") LocalDateTime hiddenTime);
    
    /**
     * 恢复评论
     */
    @Modifying
    @Query("UPDATE KnowledgeComment c SET c.status = 'active', c.hiddenReason = null, c.hiddenBy = null, c.hiddenAt = null WHERE c.id = :commentId")
    void restoreComment(@Param("commentId") Long commentId);
    
    /**
     * 软删除评论
     */
    @Modifying
    @Query("UPDATE KnowledgeComment c SET c.isDeleted = true, c.deletedBy = :deletedBy, c.deletedAt = :deletedAt WHERE c.id = :commentId")
    void softDelete(@Param("commentId") Long commentId, @Param("deletedBy") Long deletedBy, @Param("deletedAt") LocalDateTime deletedAt);
    
    /**
     * 恢复软删除的评论
     */
    @Modifying
    @Query("UPDATE KnowledgeComment c SET c.isDeleted = false, c.deletedBy = null, c.deletedAt = null WHERE c.id = :commentId")
    void restore(@Param("commentId") Long commentId);
    
    /**
     * 批量更新评论状态
     */
    @Modifying
    @Query("UPDATE KnowledgeComment c SET c.status = :status WHERE c.id IN :commentIds")
    void updateStatusByIds(@Param("commentIds") List<Long> commentIds, @Param("status") CommentStatus status);
    
    /**
     * 查找热门评论（按点赞数排序）
     */
    @Query("SELECT c FROM KnowledgeComment c WHERE c.status = 'active' AND c.isDeleted = false " +
           "AND c.parentId IS NULL " +
           "ORDER BY c.likeCount DESC, c.createdAt DESC")
    Page<KnowledgeComment> findPopularComments(Pageable pageable);
    
    /**
     * 查找最新评论
     */
    @Query("SELECT c FROM KnowledgeComment c WHERE c.status = 'active' AND c.isDeleted = false " +
           "ORDER BY c.createdAt DESC")
    Page<KnowledgeComment> findLatestComments(Pageable pageable);
} 