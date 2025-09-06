-- 修复音频表中的userId问题
-- 执行时间：2024年

-- 1. 检查当前音频数据
SELECT id, file_name, user_id, quit_mode, is_premium_only FROM audios;

-- 2. 将系统音频的userId设置为NULL（推荐方案）
UPDATE audios SET user_id = NULL WHERE user_id = 0;

-- 3. 验证修改结果
SELECT id, file_name, user_id, quit_mode, is_premium_only FROM audios;

-- 4. 如果users表支持id=0，也可以尝试重新创建系统用户
-- 先删除可能存在的记录
-- DELETE FROM users WHERE id = 0;
-- 
-- 然后重新插入（如果表结构允许）
-- INSERT INTO users (id, nickname, phone, email, password, avatar, enabled, created_at, updated_at) 
-- VALUES (0, '系统用户', '00000000000', 'system@jiayan.com', 'system_hash', '', true, NOW(), NOW()); 