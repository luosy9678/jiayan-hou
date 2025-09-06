-- 修改用户表权限字段默认值 - 简化版本
-- 执行时间：2025-08-27
-- 目的：将can_create_posts默认值改为true，post_permission_level默认值改为limited

-- 1. 修改can_create_posts字段默认值为true
ALTER TABLE users MODIFY COLUMN can_create_posts BOOLEAN DEFAULT TRUE COMMENT '是否可以发帖';

-- 2. 修改post_permission_level字段默认值为limited
ALTER TABLE users MODIFY COLUMN post_permission_level ENUM('none', 'limited', 'full') DEFAULT 'limited' COMMENT '发帖权限级别';

-- 3. 验证修改结果
DESCRIBE users;

-- 4. 查看修改后的字段默认值
SELECT 
    COLUMN_NAME,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME IN ('can_create_posts', 'post_permission_level'); 