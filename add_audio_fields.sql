-- 为音频表添加新字段的数据库迁移脚本
-- 执行时间：2024年

-- 1. 添加戒烟方式字段
ALTER TABLE audios ADD COLUMN quit_mode VARCHAR(20) NOT NULL DEFAULT 'both' COMMENT '戒烟方式：reduction(减量), abstinence(戒断), both(两者都适合)';

-- 2. 添加会员限制字段
ALTER TABLE audios ADD COLUMN is_premium_only BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否仅会员可用';

-- 3. 为现有数据设置默认值（如果需要）
-- UPDATE audios SET quit_mode = 'both' WHERE quit_mode IS NULL;
-- UPDATE audios SET is_premium_only = FALSE WHERE is_premium_only IS NULL;

-- 4. 添加索引（可选，用于提高查询性能）
-- CREATE INDEX idx_audios_quit_mode ON audios(quit_mode);
-- CREATE INDEX idx_audios_premium_only ON audios(is_premium_only);

-- 5. 验证字段添加成功
-- DESCRIBE audios; 