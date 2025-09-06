-- 修复音频表结构，允许user_id为NULL
-- 执行时间：2024年

-- 1. 检查当前表结构
DESCRIBE audios;

-- 2. 修改user_id字段，允许NULL值
ALTER TABLE audios MODIFY COLUMN user_id BIGINT NULL;

-- 3. 验证修改结果
DESCRIBE audios;

-- 4. 现在可以将系统音频的userId设置为NULL
UPDATE audios SET user_id = NULL WHERE user_id = 0;

-- 5. 验证修改结果
SELECT id, file_name, user_id, quit_mode, is_premium_only FROM audios;

-- 6. 如果users表支持id=0，也可以创建系统用户
-- 先检查users表结构
DESCRIBE users;

-- 如果users表允许id=0，可以尝试创建系统用户
-- INSERT INTO users (id, nickname, phone, email, password, avatar, enabled, created_at, updated_at) 
-- VALUES (0, '系统用户', '00000000000', 'system@jiayan.com', 'system_hash', '', true, NOW(), NOW()); 