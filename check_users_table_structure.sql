-- 检查users表结构
-- 执行时间：2025-08-27
-- 目的：确认users表的实际字段结构，特别是password字段

-- 1. 查看users表的完整结构
DESCRIBE users;

-- 2. 查看users表的创建语句
SHOW CREATE TABLE users;

-- 3. 查看users表的所有字段信息
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_KEY,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'users'
ORDER BY ORDINAL_POSITION;

-- 4. 查看users表的索引信息
SHOW INDEX FROM users;

-- 5. 查看users表的约束信息
SELECT 
    CONSTRAINT_NAME,
    CONSTRAINT_TYPE,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'users';

-- 6. 查看users表的样本数据（前5条）
SELECT 
    id,
    nickname,
    phone,
    email,
    -- 如果password字段存在，显示其长度（不显示实际密码）
    CASE 
        WHEN password IS NOT NULL THEN CONCAT('长度:', LENGTH(password))
        ELSE 'NULL'
    END as password_info,
    enabled,
    created_at
FROM users 
LIMIT 5;

-- 7. 检查是否有password字段的NOT NULL约束
SELECT 
    COLUMN_NAME,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'password'; 