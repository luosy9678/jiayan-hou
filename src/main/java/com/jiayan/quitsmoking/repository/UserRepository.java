package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.User;
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
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    Optional<User> findByPhone(String phone);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByOpenid(String openid);
    
    Optional<User> findByUnionid(String unionid);
    
    boolean existsByPhone(String phone);
    
    boolean existsByEmail(String email);
    
    // 管理后台新增方法
    long countByEnabledTrue();
    
    long countByCreatedAtAfter(LocalDateTime dateTime);
    
    long countByMemberLevelNot(String memberLevel);
    
    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id IN :userIds")
    void updateUserStatusByIds(@Param("userIds") List<Long> userIds, @Param("enabled") Boolean enabled);
    
    // 修复缺失的方法
    @Query("SELECT u FROM User u WHERE u.phone = :account OR u.email = :account")
    Optional<User> findByPhoneOrEmail(@Param("account") String account);
    
    Optional<User> findByIdAndEnabled(Long id, Boolean enabled);
} 