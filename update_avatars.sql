-- 更新所有用户的头像字段
UPDATE users SET avatar = 'avatar_42_1754228678367_22db36a6.png' WHERE avatar IS NOT NULL OR avatar != 'avatar_42_1754228678367_22db36a6.png';

-- 查看更新结果
SELECT id, nickname, avatar FROM users LIMIT 10; 