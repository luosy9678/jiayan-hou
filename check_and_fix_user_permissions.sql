-- 检查并修复用户权限
-- 执行时间：2025-08-27
-- 目的：检查用户ID=50的权限状态，并修复权限问题

-- 1. 检查用户ID=50是否存在
SELECT 
    id,
    nickname,
    phone,
    can_create_posts,
    post_permission_level,
    forum_banned,
    enabled,
    created_at
FROM users 
WHERE id = 50;

-- 2. 如果用户50不存在，创建该用户
INSERT IGNORE INTO users (
    id,
    nickname,
    phone,
    email,
    password,
    avatar,
    enabled,
    can_create_posts,
    post_permission_level,
    member_level,
    created_at,
    updated_at
) VALUES (
    50,
    '用户50',
    '13800000050',
    'user50@example.com',
    '$2a$10$default_password_hash_for_user_50',
    '',
    true,
    true,
    'limited',
    'free',
    NOW(),
    NOW()
);

-- 3. 检查用户50的权限状态
SELECT 
    id,
    nickname,
    can_create_posts,
    post_permission_level,
    forum_banned,
    enabled
FROM users 
WHERE id = 50;

-- 4. 确保用户50有评论权限
UPDATE users 
SET 
    can_create_posts = TRUE,
    post_permission_level = 'limited',
    updated_at = NOW()
WHERE id = 50;

-- 5. 验证修复结果
SELECT 
    id,
    nickname,
    can_create_posts,
    post_permission_level,
    forum_banned,
    enabled
FROM users 
WHERE id = 50;

-- 6. 检查所有用户的权限状态
SELECT 
    id,
    nickname,
    can_create_posts,
    post_permission_level,
    forum_banned,
    enabled
FROM users 
ORDER BY id
LIMIT 20;

-- 7. 统计权限分布
SELECT 
    COUNT(*) as total_users,
    SUM(CASE WHEN can_create_posts = TRUE THEN 1 ELSE 0 END) as users_with_post_permission,
    SUM(CASE WHEN post_permission_level = 'limited' THEN 1 ELSE 0 END) as users_with_limited_permission,
    SUM(CASE WHEN post_permission_level = 'full' THEN 1 ELSE 0 END) as users_with_full_permission,
    SUM(CASE WHEN post_permission_level = 'none' THEN 1 ELSE 0 END) as users_with_no_permission
FROM users; 