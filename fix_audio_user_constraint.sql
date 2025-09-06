-- 修复音频表的外键约束问题
-- 执行时间：2024年

-- 方案1：修改外键约束允许NULL值（推荐用于系统音频）
-- 1. 删除现有的外键约束
ALTER TABLE audios DROP FOREIGN KEY FKcjfi0tcraemsm3bu7qd8lw7w0;

-- 2. 重新添加外键约束，允许NULL值
ALTER TABLE audios ADD CONSTRAINT FK_audios_user_id 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

-- 3. 修改user_id字段允许NULL值
ALTER TABLE audios MODIFY COLUMN user_id BIGINT NULL;

-- 4. 为系统音频设置user_id为NULL
UPDATE audios SET user_id = NULL WHERE user_id = 0;

-- 5. 验证修改
DESCRIBE audios;
SHOW CREATE TABLE audios;

-- 方案2：如果方案1失败，可以创建系统用户
-- INSERT INTO users (id, nickname, phone, email, password, enabled, created_at, updated_at) 
-- VALUES (0, '系统用户', '00000000000', 'system@jiayan.com', 'system_hash', true, NOW(), NOW()); 