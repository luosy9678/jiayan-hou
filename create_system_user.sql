-- 创建系统用户用于音频管理
-- 执行时间：2024年

-- 1. 创建系统用户
INSERT INTO users (
    id,
    nickname,
    phone,
    email,
    password,
    avatar,
    enabled,
    created_at,
    updated_at
) VALUES (
    0,
    '系统用户',
    '00000000000',
    'system@jiayan.com',
    '$2a$10$system_user_password_hash_placeholder',
    '',
    true,
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    nickname = VALUES(nickname),
    updated_at = NOW();

-- 2. 验证系统用户创建成功
SELECT id, nickname, phone, email, enabled, created_at FROM users WHERE id = 0;

-- 3. 检查外键约束
-- 如果上面的插入失败，可能需要先检查users表的结构
-- DESCRIBE users; 