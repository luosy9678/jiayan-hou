// 头像工具类
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

  // 验证头像URL格式
  validateAvatarUrl(avatar) {
    if (!avatar) return false;
    
    // 检查是否是有效的URL格式
    try {
      new URL(avatar);
      return true;
    } catch {
      // 如果不是完整URL，检查是否是有效的文件名
      return /^[a-zA-Z0-9_-]+\.(jpg|jpeg|png|gif|webp)$/.test(avatar);
    }
  }

  // 获取头像文件名（从完整URL中提取）
  getAvatarFileName(avatar) {
    if (!avatar) return null;
    
    // 如果是完整URL，提取文件名
    if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
      const url = new URL(avatar);
      const pathParts = url.pathname.split('/');
      return pathParts[pathParts.length - 1];
    }
    
    // 如果已经是文件名，直接返回
    return avatar;
  }
}

// 创建全局实例
const avatarUtils = new AvatarUtils();

export default avatarUtils;
export { AvatarUtils }; 