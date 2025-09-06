-- 修改用户表权限字段默认值 - 详细版本
-- 执行时间：2025-08-27
-- 目的：将can_create_posts默认值改为true，post_permission_level默认值改为limited

-- ========== 第一步：备份当前表结构 ==========
-- 在执行修改前，先查看当前表结构
SHOW CREATE TABLE users;

-- ========== 第二步：查看当前字段状态 ==========
SELECT 
    COLUMN_NAME,
    COLUMN_DEFAULT,
    IS_NULLABLE,
    DATA_TYPE,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME IN ('can_create_posts', 'post_permission_level');

-- ========== 第三步：执行字段修改 ==========

-- 3.1 修改can_create_posts字段默认值为true
ALTER TABLE users MODIFY COLUMN can_create_posts BOOLEAN DEFAULT TRUE COMMENT '是否可以发帖';

-- 3.2 修改post_permission_level字段默认值为limited
ALTER TABLE users MODIFY COLUMN post_permission_level ENUM('none', 'limited', 'full') DEFAULT 'limited' COMMENT '发帖权限级别';

-- ========== 第四步：验证修改结果 ==========

-- 4.1 查看修改后的表结构
DESCRIBE users;

-- 4.2 查看修改后的字段默认值
SELECT 
    COLUMN_NAME,
    COLUMN_DEFAULT,
    IS_NULLABLE,
    DATA_TYPE,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME IN ('can_create_posts', 'post_permission_level');

-- 4.3 查看现有用户权限状态
SELECT 
    id,
    nickname,
    can_create_posts,
    post_permission_level,
    forum_banned,
    created_at
FROM users 
ORDER BY created_at DESC
LIMIT 20;

-- ========== 第五步：为新用户设置默认权限 ==========

-- 5.1 为现有用户更新权限（可选操作）
-- 注意：这会修改现有用户的权限，请谨慎执行
/*
UPDATE users 
SET 
    can_create_posts = TRUE, 
    post_permission_level = 'limited',
    updated_at = NOW()
WHERE 
    can_create_posts = FALSE 
    OR post_permission_level = 'none'
    OR can_create_posts IS NULL
    OR post_permission_level IS NULL;
*/

-- 5.2 查看权限更新后的用户状态
SELECT 
    COUNT(*) as total_users,
    SUM(CASE WHEN can_create_posts = TRUE THEN 1 ELSE 0 END) as users_with_post_permission,
    SUM(CASE WHEN post_permission_level = 'limited' THEN 1 ELSE 0 END) as users_with_limited_permission,
    SUM(CASE WHEN post_permission_level = 'full' THEN 1 ELSE 0 END) as users_with_full_permission,
    SUM(CASE WHEN post_permission_level = 'none' THEN 1 ELSE 0 END) as users_with_no_permission
FROM users;

-- ========== 第六步：测试新用户创建 ==========

-- 6.1 测试插入新用户（可选，用于验证默认值）
/*
INSERT INTO users (
    nickname, 
    phone, 
    email, 
    password, 
    enabled, 
    created_at, 
    updated_at
) VALUES (
    '测试用户_' || UNIX_TIMESTAMP(),
    '1380000' || LPAD(FLOOR(RAND() * 9999), 4, '0'),
    'test' || UNIX_TIMESTAMP() || '@example.com',
    'test_password_hash',
    TRUE,
    NOW(),
    NOW()
);

-- 6.2 验证新用户的默认权限
SELECT 
    id,
    nickname,
    can_create_posts,
    post_permission_level,
    created_at
FROM users 
WHERE nickname LIKE '测试用户_%'
ORDER BY created_at DESC
LIMIT 1;
*/

-- ========== 第七步：回滚操作（如果需要） ==========

-- 如果需要回滚到原来的默认值，执行以下SQL：
/*
-- 回滚can_create_posts到false
ALTER TABLE users MODIFY COLUMN can_create_posts BOOLEAN DEFAULT FALSE COMMENT '是否可以发帖';

-- 回滚post_permission_level到none
ALTER TABLE users MODIFY COLUMN post_permission_level ENUM('none', 'limited', 'full') DEFAULT 'none' COMMENT '发帖权限级别';

-- 验证回滚结果
DESCRIBE users;
*/

-- ========== 第八步：权限测试 ==========

-- 8.1 测试权限检查逻辑
SELECT 
    u.id,
    u.nickname,
    u.can_create_posts,
    u.post_permission_level,
    u.forum_banned,
    CASE 
        WHEN u.forum_banned = TRUE THEN '用户被禁言，无权限'
        WHEN u.can_create_posts = TRUE THEN '用户有发帖权限'
        ELSE '用户无发帖权限'
    END as permission_status
FROM users u
ORDER BY u.created_at DESC
LIMIT 10;

-- 完成！
-- 现在新创建的用户将默认具有：
-- - can_create_posts = TRUE
-- - post_permission_level = 'limited' 