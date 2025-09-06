// 测试头像URL构建逻辑
import avatarUtils from './avatar-utils.js';

console.log('=== 头像URL构建测试 ===');

// 测试用例1：空值
console.log('测试1 - 空值:');
console.log('输入:', '');
console.log('输出:', avatarUtils.buildAvatarUrl(''));
console.log('期望:', '');
console.log('');

// 测试用例2：完整URL
console.log('测试2 - 完整URL:');
console.log('输入:', 'https://example.com/avatar.jpg');
console.log('输出:', avatarUtils.buildAvatarUrl('https://example.com/avatar.jpg'));
console.log('期望:', 'https://example.com/avatar.jpg');
console.log('');

// 测试用例3：相对路径（问题用例）
console.log('测试3 - 相对路径（问题用例）:');
console.log('输入:', '/api/v1/avatar/avatar_42_1754228678367_22db36a6.png');
console.log('输出:', avatarUtils.buildAvatarUrl('/api/v1/avatar/avatar_42_1754228678367_22db36a6.png'));
console.log('期望:', 'http://localhost:8081/api/v1/avatar/avatar_42_1754228678367_22db36a6.png');
console.log('');

// 测试用例4：纯文件名
console.log('测试4 - 纯文件名:');
console.log('输入:', 'avatar_42_1754228678367_22db36a6.png');
console.log('输出:', avatarUtils.buildAvatarUrl('avatar_42_1754228678367_22db36a6.png'));
console.log('期望:', 'http://localhost:8081/api/v1/avatar/avatar_42_1754228678367_22db36a6.png');
console.log('');

// 测试用例5：其他API路径
console.log('测试5 - 其他API路径:');
console.log('输入:', '/api/v1/user/profile');
console.log('输出:', avatarUtils.buildAvatarUrl('/api/v1/user/profile'));
console.log('期望:', 'http://localhost:8081/api/v1/user/profile');
console.log('');

console.log('=== 测试完成 ==='); 