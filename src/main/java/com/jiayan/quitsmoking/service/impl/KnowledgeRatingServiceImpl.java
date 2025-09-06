package com.jiayan.quitsmoking.service.impl;

import com.jiayan.quitsmoking.entity.KnowledgeRating;
import com.jiayan.quitsmoking.entity.KnowledgeArticle;
import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.exception.BusinessException;
import com.jiayan.quitsmoking.repository.KnowledgeRatingRepository;
import com.jiayan.quitsmoking.repository.KnowledgeArticleRepository;
import com.jiayan.quitsmoking.repository.UserRepository;
import com.jiayan.quitsmoking.service.KnowledgeRatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户评分服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KnowledgeRatingServiceImpl implements KnowledgeRatingService {
    
    private final KnowledgeRatingRepository ratingRepository;
    private final KnowledgeArticleRepository articleRepository;
    private final UserRepository userRepository;
    
    @Override
    public KnowledgeRating createRating(KnowledgeRating rating) {
        log.info("创建评分: articleId={}, userId={}, rating={}", rating.getArticleId(), rating.getUserId(), rating.getRating());
        
        // 验证文章是否存在
        KnowledgeArticle article = articleRepository.findById(rating.getArticleId())
                .orElseThrow(() -> new BusinessException("文章不存在"));
        
        // 验证用户是否存在
        User user = userRepository.findById(rating.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 验证评分值是否有效
        if (!isValidRating(rating.getRating())) {
            throw new BusinessException("评分值无效，必须在1-5之间");
        }
        
        // 检查用户是否已经对文章评分
        if (hasUserRated(rating.getArticleId(), rating.getUserId())) {
            throw new BusinessException("用户已经对该文章评分，不能重复评分");
        }
        
        // 设置评分初始信息
        rating.setCreatedAt(LocalDateTime.now());
        // KnowledgeRating没有updatedAt字段，暂时注释掉
        // rating.setUpdatedAt(LocalDateTime.now());
        
        // 保存评分
        KnowledgeRating savedRating = ratingRepository.save(rating);
        
        // 更新文章的评分统计
        updateArticleRatingStats(rating.getArticleId());
        
        log.info("评分创建成功: ratingId={}", savedRating.getId());
        return savedRating;
    }
    
    @Override
    public KnowledgeRating updateRating(Long ratingId, Integer rating, String comment) {
        log.info("更新评分: ratingId={}, rating={}", ratingId, rating);
        
        KnowledgeRating existingRating = getRatingById(ratingId);
        if (existingRating == null) {
            throw new BusinessException("评分不存在");
        }
        
        // 验证评分值是否有效
        if (!isValidRating(rating)) {
            throw new BusinessException("评分值无效，必须在1-5之间");
        }
        
        // 更新评分信息
        existingRating.setRating(rating);
        if (StringUtils.hasText(comment)) {
            existingRating.setComment(comment);
        }
        // KnowledgeRating没有updatedAt字段，暂时注释掉
        // existingRating.setUpdatedAt(LocalDateTime.now());
        
        // 保存评分
        KnowledgeRating updatedRating = ratingRepository.save(existingRating);
        
        // 更新文章的评分统计
        updateArticleRatingStats(existingRating.getArticleId());
        
        log.info("评分更新成功: ratingId={}", ratingId);
        return updatedRating;
    }
    
    @Override
    @Transactional(readOnly = true)
    public KnowledgeRating getRatingById(Long ratingId) {
        return ratingRepository.findById(ratingId).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public KnowledgeRating getRatingByArticleAndUser(Long articleId, Long userId) {
        return ratingRepository.findByArticleIdAndUserId(articleId, userId).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeRating> getRatingsByArticle(Long articleId, Pageable pageable) {
        return ratingRepository.findByArticleId(articleId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeRating> getRatingsByUser(Long userId, Pageable pageable) {
        return ratingRepository.findByUserId(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeRating> getRatingsByValue(Integer rating) {
        if (!isValidRating(rating)) {
            throw new BusinessException("评分值无效，必须在1-5之间");
        }
        return ratingRepository.findByRating(rating);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeRating> getHighRatingsByArticle(Long articleId) {
        // 使用实际存在的方法，暂时返回空列表
        return List.of();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeRating> getLowRatingsByArticle(Long articleId) {
        // 使用实际存在的方法，暂时返回空列表
        return List.of();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageRating(Long articleId) {
        Double avgRating = ratingRepository.calculateAverageRatingByArticleId(articleId);
        if (avgRating == null) {
            return 0.0;
        }
        
        // 保留两位小数
        BigDecimal bd = BigDecimal.valueOf(avgRating);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Long> getRatingDistribution(Long articleId) {
        Map<Integer, Long> distribution = new HashMap<>();
        
        // 统计1-5分的数量
        for (int i = 1; i <= 5; i++) {
            long count = ratingRepository.countByArticleIdAndRating(articleId, i);
            distribution.put(i, count);
        }
        
        return distribution;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeRating> getLatestRatings(Pageable pageable) {
        // 使用实际存在的方法，暂时返回空分页
        return Page.empty(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeRating> getRatingsByRange(Integer minRating, Integer maxRating, Pageable pageable) {
        if (!isValidRating(minRating) || !isValidRating(maxRating)) {
            throw new BusinessException("评分值无效，必须在1-5之间");
        }
        
        if (minRating > maxRating) {
            throw new BusinessException("最小评分不能大于最大评分");
        }
        
        // 使用实际存在的方法，暂时返回空分页
        return Page.empty(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeRating> getRatingsWithComments(Pageable pageable) {
        // 使用实际存在的方法，暂时返回空分页
        return Page.empty(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeRating> searchRatingsByComment(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            return Page.empty(pageable);
        }
        // 使用实际存在的方法，暂时返回空分页
        return Page.empty(pageable);
    }
    
    @Override
    public void deleteRating(Long articleId, Long userId) {
        log.info("删除评分: articleId={}, userId={}", articleId, userId);
        
        KnowledgeRating rating = getRatingByArticleAndUser(articleId, userId);
        if (rating == null) {
            throw new BusinessException("评分不存在");
        }
        
        ratingRepository.delete(rating);
        
        // 更新文章的评分统计
        updateArticleRatingStats(articleId);
        
        log.info("评分删除成功: articleId={}, userId={}", articleId, userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserRated(Long articleId, Long userId) {
        return getRatingByArticleAndUser(articleId, userId) != null;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getRatingCount() {
        return ratingRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getRatingCountByArticle(Long articleId) {
        return ratingRepository.countByArticleId(articleId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getRatingCountByUser(Long userId) {
        return ratingRepository.countByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getRatingCountByValue(Integer rating) {
        if (!isValidRating(rating)) {
            throw new BusinessException("评分值无效，必须在1-5之间");
        }
        return ratingRepository.countByRating(rating);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getRatingCountByArticleAndValue(Long articleId, Integer rating) {
        if (!isValidRating(rating)) {
            throw new BusinessException("评分值无效，必须在1-5之间");
        }
        return ratingRepository.countByArticleIdAndRating(articleId, rating);
    }
    
    @Override
    public boolean isValidRating(Integer rating) {
        return rating != null && rating >= 1 && rating <= 5;
    }
    
    @Override
    public void updateArticleRatingStats(Long articleId) {
        log.info("更新文章评分统计: articleId={}", articleId);
        
        // 计算平均评分
        Double avgRating = calculateAverageRating(articleId);
        
        // 获取评分人数
        long ratingCount = getRatingCountByArticle(articleId);
        
        // 更新文章表
        KnowledgeArticle article = articleRepository.findById(articleId).orElse(null);
        if (article != null) {
            article.setRatingScore(BigDecimal.valueOf(avgRating));
            article.setRatingCount((int) ratingCount);
            article.setUpdatedAt(LocalDateTime.now());
            articleRepository.save(article);
            
            log.info("文章评分统计更新成功: articleId={}, avgRating={}, ratingCount={}", 
                    articleId, avgRating, ratingCount);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Long> getTopRatedArticleIds(int limit) {
        if (limit <= 0) {
            throw new BusinessException("限制数量必须大于0");
        }
        
        // 使用实际存在的方法，暂时返回空列表
        return List.of();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getUserRatingTrend(Long userId, int days) {
        if (days <= 0) {
            throw new BusinessException("天数必须大于0");
        }
        
        Map<String, Double> trend = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 获取最近N天的日期
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // 计算每天的评分趋势
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(formatter);
            
            // 使用实际存在的方法，暂时设置为0
            Double avgRating = 0.0;
            
            trend.put(dateStr, avgRating);
        }
        
        return trend;
    }
} 