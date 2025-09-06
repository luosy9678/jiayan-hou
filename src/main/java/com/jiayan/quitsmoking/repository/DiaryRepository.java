
package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // 基础CRUD操作由JpaRepository提供
    
    /**
     * 根据用户ID和状态查询日记，按记录时间倒序排列
     * @param userId 用户ID
     * @param status 日记状态
     * @return 日记列表
     */
    List<Diary> findByUserIdAndStatusNotOrderByRecordTimeDesc(Long userId, String status);
} 