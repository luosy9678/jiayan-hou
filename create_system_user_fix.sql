-- 创建系统用户用于音频管理
-- 执行时间：2024年

-- 1. 检查users表结构
DESCRIBE users;

-- 2. 创建系统用户
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

-- 3. 验证系统用户创建成功
SELECT id, nickname, phone, email, enabled FROM users WHERE id = 0;

-- 4. 检查音频表中的系统音频
SELECT id, file_name, user_id, quit_mode, is_premium_only FROM audios WHERE user_id = 0;

-- 5. 如果还有问题，可以更新音频的userId为NULL
-- UPDATE audios SET user_id = NULL WHERE user_id = 0; 

-- 将系统音频的userId设置为NULL
UPDATE audios SET user_id = NULL WHERE user_id = 0; 