// 简单的头像URL构建测试
class AvatarUtils {
  constructor(baseUrl = 'http://localhost:8081/api/v1') {
    this.baseUrl = baseUrl;
  }

  // 构建头像URL
  buildAvatarUrl(avatar) {
    console.log('构建头像URL，输入:', avatar);
    
    if (!avatar || avatar.trim() === '') {
      return '';
    }
    
    // 如果已经是完整的URL，直接返回
    if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
      return avatar;
    }
    
    // 如果以/api/v1/avatar/开头，说明已经是完整的相对路径，需要拼接baseUrl
    if (avatar.startsWith('/api/v1/avatar/')) {
      // 提取文件名部分
      const fileName = avatar.replace('/api/v1/avatar/', '');
      const fullUrl = `${this.baseUrl}/avatar/${fileName}`;
      console.log('构建的完整URL:', fullUrl);
      return fullUrl;
    }
    
    // 如果以/api/v1开头但不是avatar路径，需要特殊处理
    if (avatar.startsWith('/api/v1/')) {
      const cleanPath = avatar.replace('/api/v1', '');
      const fullUrl = `${this.baseUrl}${cleanPath}`;
      console.log('构建的完整URL:', fullUrl);
      return fullUrl;
    }
    
    // 如果是纯文件名，直接拼接
    const fullUrl = `${this.baseUrl}/avatar/${avatar}`;
    console.log('构建的完整URL:', fullUrl);
    return fullUrl;
  }
}

// 创建实例
const avatarUtils = new AvatarUtils();

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

console.log('=== 测试完成 ==='); 