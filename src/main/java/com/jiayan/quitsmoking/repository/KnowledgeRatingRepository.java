package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.KnowledgeRating;
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
 * 用户评分数据访问层
 */
@Repository
public interface KnowledgeRatingRepository extends JpaRepository<KnowledgeRating, Long>, JpaSpecificationExecutor<KnowledgeRating> {
    
    /**
     * 根据文章ID查找评分
     */
    Page<KnowledgeRating> findByArticleId(Long articleId, Pageable pageable);
    
    /**
     * 根据用户ID查找评分
     */
    Page<KnowledgeRating> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据文章ID和用户ID查找评分
     */
    Optional<KnowledgeRating> findByArticleIdAndUserId(Long articleId, Long userId);
    
    /**
     * 根据评分值查找评分
     */
    List<KnowledgeRating> findByRating(Integer rating);
    
    /**
     * 根据文章ID统计评分数量
     */
    long countByArticleId(Long articleId);
    
    /**
     * 根据用户ID统计评分数量
     */
    long countByUserId(Long userId);
    
    /**
     * 根据评分值统计数量
     */
    long countByRating(Integer rating);
    
    /**
     * 根据文章ID查找指定评分的数量
     */
    long countByArticleIdAndRating(Long articleId, Integer rating);
    
    /**
     * 查找指定时间范围内的评分
     */
    List<KnowledgeRating> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据文章ID查找高评分（4-5分）
     */
    @Query("SELECT r FROM KnowledgeRating r WHERE r.articleId = :articleId AND r.rating >= 4 ORDER BY r.createdAt DESC")
    List<KnowledgeRating> findHighRatingsByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 根据文章ID查找低评分（1-2分）
     */
    @Query("SELECT r FROM KnowledgeRating r WHERE r.articleId = :articleId AND r.rating <= 2 ORDER BY r.createdAt DESC")
    List<KnowledgeRating> findLowRatingsByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 计算文章的平均评分
     */
    @Query("SELECT AVG(r.rating) FROM KnowledgeRating r WHERE r.articleId = :articleId")
    Double calculateAverageRatingByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 计算文章的评分分布
     */
    @Query("SELECT r.rating, COUNT(r) FROM KnowledgeRating r WHERE r.articleId = :articleId GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getRatingDistributionByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 查找用户对指定文章的评分
     */
    @Query("SELECT r FROM KnowledgeRating r WHERE r.userId = :userId AND r.articleId = :articleId")
    Optional<KnowledgeRating> findByUserIdAndArticleId(@Param("userId") Long userId, @Param("articleId") Long articleId);
    
    /**
     * 更新评分
     */
    @Modifying
    @Query("UPDATE KnowledgeRating r SET r.rating = :rating, r.comment = :comment WHERE r.id = :ratingId")
    void updateRating(@Param("ratingId") Long ratingId, @Param("rating") Integer rating, @Param("comment") String comment);
    
    /**
     * 删除用户对指定文章的评分
     */
    @Modifying
    @Query("DELETE FROM KnowledgeRating r WHERE r.userId = :userId AND r.articleId = :articleId")
    void deleteByUserIdAndArticleId(@Param("userId") Long userId, @Param("articleId") Long articleId);
    
    /**
     * 查找最新评分
     */
    @Query("SELECT r FROM KnowledgeRating r ORDER BY r.createdAt DESC")
    Page<KnowledgeRating> findLatestRatings(Pageable pageable);
    
    /**
     * 查找指定评分范围的最新评分
     */
    @Query("SELECT r FROM KnowledgeRating r WHERE r.rating BETWEEN :minRating AND :maxRating ORDER BY r.createdAt DESC")
    Page<KnowledgeRating> findRatingsByRange(@Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating, Pageable pageable);
    
    /**
     * 查找有评论的评分
     */
    @Query("SELECT r FROM KnowledgeRating r WHERE r.comment IS NOT NULL AND r.comment <> '' ORDER BY r.createdAt DESC")
    Page<KnowledgeRating> findRatingsWithComments(Pageable pageable);
    
    /**
     * 根据评论内容搜索评分
     */
    @Query("SELECT r FROM KnowledgeRating r WHERE r.comment LIKE %:keyword% ORDER BY r.createdAt DESC")
    Page<KnowledgeRating> searchRatingsByComment(@Param("keyword") String keyword, Pageable pageable);
} 