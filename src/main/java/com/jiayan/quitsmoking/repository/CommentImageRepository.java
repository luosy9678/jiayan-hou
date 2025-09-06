package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.CommentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 评论图片数据访问层
 */
@Repository
public interface CommentImageRepository extends JpaRepository<CommentImage, Long>, JpaSpecificationExecutor<CommentImage> {
    
    /**
     * 根据评论ID查找图片
     */
    List<CommentImage> findByCommentIdOrderBySortOrderAsc(Long commentId);
    
    /**
     * 根据评论ID和排序查找图片
     */
    Optional<CommentImage> findByCommentIdAndSortOrder(Long commentId, Integer sortOrder);
    
    /**
     * 根据评论ID统计图片数量
     */
    long countByCommentId(Long commentId);
    
    /**
     * 根据图片URL查找评论图片
     */
    List<CommentImage> findByImageUrl(String imageUrl);
    
    /**
     * 根据评论ID删除所有图片
     */
    @Modifying
    @Query("DELETE FROM CommentImage ci WHERE ci.commentId = :commentId")
    void deleteByCommentId(@Param("commentId") Long commentId);
    
    /**
     * 更新图片排序
     */
    @Modifying
    @Query("UPDATE CommentImage ci SET ci.sortOrder = :newOrder WHERE ci.id = :imageId")
    void updateSortOrder(@Param("imageId") Long imageId, @Param("newOrder") Integer newOrder);
    
    /**
     * 批量更新图片排序
     */
    @Modifying
    @Query("UPDATE CommentImage ci SET ci.sortOrder = ci.sortOrder + :offset WHERE ci.commentId = :commentId AND ci.sortOrder >= :startOrder")
    void shiftSortOrder(@Param("commentId") Long commentId, @Param("startOrder") Integer startOrder, @Param("offset") Integer offset);
    
    /**
     * 查找指定评论的最大排序值
     */
    @Query("SELECT MAX(ci.sortOrder) FROM CommentImage ci WHERE ci.commentId = :commentId")
    Integer findMaxSortOrderByCommentId(@Param("commentId") Long commentId);
    
    /**
     * 查找指定评论的最小排序值
     */
    @Query("SELECT MIN(ci.sortOrder) FROM CommentImage ci WHERE ci.commentId = :commentId")
    Integer findMinSortOrderByCommentId(@Param("commentId") Long commentId);
    
    /**
     * 查找指定排序范围内的图片
     */
    @Query("SELECT ci FROM CommentImage ci WHERE ci.commentId = :commentId AND ci.sortOrder BETWEEN :startOrder AND :endOrder ORDER BY ci.sortOrder ASC")
    List<CommentImage> findBySortOrderBetween(@Param("commentId") Long commentId, 
                                            @Param("startOrder") Integer startOrder, 
                                            @Param("endOrder") Integer endOrder);
    
    /**
     * 检查评论是否包含图片
     */
    @Query("SELECT COUNT(ci) > 0 FROM CommentImage ci WHERE ci.commentId = :commentId")
    boolean hasImages(@Param("commentId") Long commentId);
    
    /**
     * 查找包含图片的评论ID列表
     */
    @Query("SELECT DISTINCT ci.commentId FROM CommentImage ci")
    List<Long> findCommentIdsWithImages();
    
    /**
     * 根据图片尺寸查找图片
     */
    List<CommentImage> findByImageWidthAndImageHeight(Integer imageWidth, Integer imageHeight);
    
    /**
     * 查找指定尺寸范围内的图片
     */
    @Query("SELECT ci FROM CommentImage ci WHERE ci.imageWidth BETWEEN :minWidth AND :maxWidth " +
           "AND ci.imageHeight BETWEEN :minHeight AND :maxHeight")
    List<CommentImage> findByImageSizeBetween(@Param("minWidth") Integer minWidth, 
                                            @Param("maxWidth") Integer maxWidth,
                                            @Param("minHeight") Integer minHeight, 
                                            @Param("maxHeight") Integer maxHeight);
} 