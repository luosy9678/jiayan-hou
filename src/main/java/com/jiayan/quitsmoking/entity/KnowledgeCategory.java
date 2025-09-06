package com.jiayan.quitsmoking.entity;

import com.jiayan.quitsmoking.enums.AccessLevel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识分类实体类
 */
@Entity
@Table(name = "knowledge_categories")
@Data
@EqualsAndHashCode(callSuper = false)
public class KnowledgeCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", length = 20)
    private AccessLevel accessLevel = AccessLevel.FREE;
    
    @Column(name = "member_only")
    private Boolean memberOnly = false;
    
    @Column(name = "icon_name", length = 100)
    private String iconName;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ========== 关联关系 ==========
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private KnowledgeCategory parent;
    
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<KnowledgeCategory> children;
    
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<KnowledgeArticle> articles;
    
    // ========== 业务方法 ==========
    
    /**
     * 检查是否为顶级分类
     */
    public boolean isRootCategory() {
        return parentId == null;
    }
    
    /**
     * 检查是否为叶子分类
     */
    public boolean isLeafCategory() {
        return children == null || children.isEmpty();
    }
    
    /**
     * 获取分类层级路径
     */
    public String getCategoryPath() {
        if (isRootCategory()) {
            return name;
        }
        
        StringBuilder path = new StringBuilder();
        KnowledgeCategory current = this;
        while (current != null) {
            if (path.length() > 0) {
                path.insert(0, " > ");
            }
            path.insert(0, current.getName());
            current = current.getParent();
        }
        return path.toString();
    }
    
    /**
     * 检查用户是否有访问权限
     */
    public boolean canUserAccess(String userMemberLevel, Boolean isPremiumMember) {
        if (!isActive) {
            return false;
        }
        
        if (accessLevel == AccessLevel.FREE) {
            return true;
        }
        
        if (accessLevel == AccessLevel.MEMBER) {
            return "member".equals(userMemberLevel) || Boolean.TRUE.equals(isPremiumMember);
        }
        
        if (accessLevel == AccessLevel.PREMIUM) {
            return Boolean.TRUE.equals(isPremiumMember);
        }
        
        return false;
    }
}