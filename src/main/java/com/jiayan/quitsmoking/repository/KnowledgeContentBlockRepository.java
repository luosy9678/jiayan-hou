package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.KnowledgeContentBlock;
import com.jiayan.quitsmoking.enums.BlockType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 知识内容块数据访问层
 */
@Repository
public interface KnowledgeContentBlockRepository extends JpaRepository<KnowledgeContentBlock, Long>, JpaSpecificationExecutor<KnowledgeContentBlock> {
    
    /**
     * 根据文章ID查找内容块
     */
    List<KnowledgeContentBlock> findByArticleIdOrderByContentOrderAsc(Long articleId);
    
    /**
     * 根据文章ID和内容类型查找内容块
     */
    List<KnowledgeContentBlock> findByArticleIdAndBlockTypeOrderByContentOrderAsc(Long articleId, BlockType blockType);
    
    /**
     * 根据文章ID和内容顺序查找内容块
     */
    Optional<KnowledgeContentBlock> findByArticleIdAndContentOrder(Long articleId, Integer contentOrder);
    
    /**
     * 根据文章ID统计内容块数量
     */
    long countByArticleId(Long articleId);
    
    /**
     * 查找指定文章的最大内容顺序
     */
    @Query("SELECT MAX(c.contentOrder) FROM KnowledgeContentBlock c WHERE c.articleId = :articleId")
    Integer findMaxContentOrderByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 查找指定文章的最小内容顺序
     */
    @Query("SELECT MIN(c.contentOrder) FROM KnowledgeContentBlock c WHERE c.articleId = :articleId")
    Integer findMinContentOrderByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 根据文章ID删除所有内容块
     */
    @Modifying
    @Query("DELETE FROM KnowledgeContentBlock c WHERE c.articleId = :articleId")
    void deleteByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 更新内容块顺序
     */
    @Modifying
    @Query("UPDATE KnowledgeContentBlock c SET c.contentOrder = :newOrder WHERE c.id = :blockId")
    void updateContentOrder(@Param("blockId") Long blockId, @Param("newOrder") Integer newOrder);
    
    /**
     * 批量更新内容块顺序
     */
    @Modifying
    @Query("UPDATE KnowledgeContentBlock c SET c.contentOrder = c.contentOrder + :offset WHERE c.articleId = :articleId AND c.contentOrder >= :startOrder")
    void shiftContentOrder(@Param("articleId") Long articleId, @Param("startOrder") Integer startOrder, @Param("offset") Integer offset);
    
    /**
     * 查找包含图片的内容块
     */
    @Query("SELECT c FROM KnowledgeContentBlock c WHERE c.articleId = :articleId AND c.blockType = 'image' ORDER BY c.contentOrder ASC")
    List<KnowledgeContentBlock> findImageBlocksByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 查找包含文本的内容块
     */
    @Query("SELECT c FROM KnowledgeContentBlock c WHERE c.articleId = :articleId AND c.blockType = 'text' ORDER BY c.contentOrder ASC")
    List<KnowledgeContentBlock> findTextBlocksByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 根据图片URL查找内容块
     */
    List<KnowledgeContentBlock> findByImageUrl(String imageUrl);
    
    /**
     * 检查文章是否包含图片
     */
    @Query("SELECT COUNT(c) > 0 FROM KnowledgeContentBlock c WHERE c.articleId = :articleId AND c.blockType = 'image'")
    boolean hasImageBlocks(@Param("articleId") Long articleId);
    
    /**
     * 检查文章是否包含文本
     */
    @Query("SELECT COUNT(c) > 0 FROM KnowledgeContentBlock c WHERE c.articleId = :articleId AND c.blockType = 'text'")
    boolean hasTextBlocks(@Param("articleId") Long articleId);
    
    /**
     * 查找指定顺序范围内的内容块
     */
    @Query("SELECT c FROM KnowledgeContentBlock c WHERE c.articleId = :articleId AND c.contentOrder BETWEEN :startOrder AND :endOrder ORDER BY c.contentOrder ASC")
    List<KnowledgeContentBlock> findByContentOrderBetween(@Param("articleId") Long articleId, 
                                                         @Param("startOrder") Integer startOrder, 
                                                         @Param("endOrder") Integer endOrder);
} 