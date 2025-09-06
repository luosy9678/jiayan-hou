-- 修复agreements表的自增序列
-- 这个脚本会重新设置自增序列为当前最大ID + 1

-- 查看当前agreements表的最大ID
SELECT MAX(id) as max_id FROM agreements;

-- 查看当前自增序列值
SELECT AUTO_INCREMENT 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'quitsmoking' 
AND TABLE_NAME = 'agreements';

-- 重新设置自增序列为最大ID + 1
-- 注意：请先运行上面的查询，然后将结果替换到下面的语句中
-- 例如：如果最大ID是3，则设置为4
ALTER TABLE agreements AUTO_INCREMENT = 4;

-- 验证修复结果
SELECT AUTO_INCREMENT 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'quitsmoking' 
AND TABLE_NAME = 'agreements'; 