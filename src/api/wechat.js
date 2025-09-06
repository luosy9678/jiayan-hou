/**
 * 微信重新授权登录
 */
export const wechatReAuth = async (code, openid, userInfo = {}) => {
  try {
    const response = await fetch('/api/v1/auth/wechat/re-auth', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        code: code,
        openid: openid,
        nickname: userInfo.nickname,
        avatar: userInfo.avatar,
        sex: userInfo.sex,
        province: userInfo.province,
        city: userInfo.city,
        unionid: userInfo.unionid
      })
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      // 保存新的token
      localStorage.setItem('token', result.data.token);
      localStorage.setItem('refreshToken', result.data.refreshToken);
      localStorage.setItem('openid', openid);
      
      return result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('微信重新授权登录失败:', error);
    throw error;
  }
};

/**
 * 检查token是否过期
 */
export const isTokenExpired = (token) => {
  if (!token) return true;
  
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Math.floor(Date.now() / 1000);
    return payload.exp < currentTime;
  } catch (error) {
    return true;
  }
};

/**
 * 微信登录状态检查
 */
export const checkWechatLoginStatus = async () => {
  const token = localStorage.getItem('token');
  const openid = localStorage.getItem('openid');
  
  if (!token || !openid) {
    return { isLoggedIn: false, needReAuth: true };
  }
  
  if (isTokenExpired(token)) {
    return { isLoggedIn: false, needReAuth: true };
  }
  
  // 验证token有效性
  try {
    const response = await fetch('/api/v1/auth/verify-token', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (response.ok) {
      return { isLoggedIn: true, needReAuth: false };
    } else {
      return { isLoggedIn: false, needReAuth: true };
    }
  } catch (error) {
    return { isLoggedIn: false, needReAuth: true };
  }
}; 