package com.jiayan.quitsmoking.service;

import com.jiayan.quitsmoking.entity.User;
import com.jiayan.quitsmoking.enums.PostPermissionLevel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 论坛权限管理服务接口
 */
public interface ForumPermissionService {
    
    /**
     * 检查用户是否有发帖权限
     */
    boolean canUserCreatePosts(Long userId);
    
    /**
     * 检查用户的发帖权限级别
     */
    PostPermissionLevel getUserPostPermissionLevel(Long userId);
    
    /**
     * 授予用户发帖权限
     */
    void grantPostPermission(Long userId, PostPermissionLevel level, Long grantedBy, LocalDateTime expiresAt);
    
    /**
     * 撤销用户发帖权限
     */
    void revokePostPermission(Long userId, Long revokedBy);
    
    /**
     * 更新用户发帖权限级别
     */
    void updatePostPermissionLevel(Long userId, PostPermissionLevel level, Long updatedBy);
    
    /**
     * 检查用户是否被禁言
     */
    boolean isUserBanned(Long userId);
    
    /**
     * 禁言用户
     */
    void banUser(Long userId, String reason, LocalDateTime endTime, Long adminId);
    
    /**
     * 解除用户禁言
     */
    void unbanUser(Long userId, Long adminId);
    
    /**
     * 警告用户
     */
    void warnUser(Long userId, String reason, Long adminId);
    
    /**
     * 获取用户的禁言信息
     */
    User getUserBanInfo(Long userId);
    
    /**
     * 获取用户的警告次数
     */
    int getUserWarningCount(Long userId);
    
    /**
     * 获取用户的累计禁言次数
     */
    int getUserBanCount(Long userId);
    
    /**
     * 检查用户是否可以访问特定内容
     */
    boolean canUserAccessContent(Long userId, String contentAccessLevel);
    
    /**
     * 获取有发帖权限的用户列表
     */
    List<User> getUsersWithPostPermission();
    
    /**
     * 获取被禁言的用户列表
     */
    List<User> getBannedUsers();
    
    /**
     * 获取用户的权限历史记录
     */
    List<Object> getUserPermissionHistory(Long userId);
    
    /**
     * 检查用户权限是否过期
     */
    boolean isUserPermissionExpired(Long userId);
    
    /**
     * 自动清理过期的权限
     */
    void cleanupExpiredPermissions();
    
    /**
     * 获取用户的完整权限信息
     */
    Object getUserFullPermissionInfo(Long userId);
    
    /**
     * 批量更新用户权限
     */
    void batchUpdateUserPermissions(List<Long> userIds, PostPermissionLevel level, Long updatedBy);
    
    /**
     * 检查用户是否有管理权限
     */
    boolean hasAdminPermission(Long userId);
    
    /**
     * 检查用户是否有审核权限
     */
    boolean hasAuditPermission(Long userId);
    
    /**
     * 获取用户的角色权限
     */
    List<String> getUserRoles(Long userId);
    
    /**
     * 验证用户操作权限
     */
    boolean validateUserOperation(Long userId, String operation, Long targetId);
} 