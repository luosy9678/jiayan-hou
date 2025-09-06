<template>
  <div class="wechat-login">
    <div v-if="!isLoggedIn" class="login-prompt">
      <h3>微信登录</h3>
      <p>请使用微信扫码登录</p>
      <button @click="startWechatLogin" class="wechat-btn">
        <i class="wechat-icon"></i>
        微信登录
      </button>
    </div>
    
    <div v-else class="user-info">
      <img :src="userInfo.avatar" :alt="userInfo.nickname" class="avatar">
      <span class="nickname">{{ userInfo.nickname }}</span>
      <button @click="logout" class="logout-btn">退出登录</button>
    </div>
  </div>
</template>

<script>
import { wechatReAuth, checkWechatLoginStatus } from '@/api/wechat';
import { useUserStore } from '@/stores/user';

export default {
  name: 'WechatLogin',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      wechatAppId: 'your_wechat_app_id', // 你的微信AppID
      redirectUri: encodeURIComponent(window.location.origin + '/wechat-callback')
    };
  },
  
  async mounted() {
    await this.checkLoginStatus();
  },
  
  methods: {
    async checkLoginStatus() {
      const status = await checkWechatLoginStatus();
      this.isLoggedIn = status.isLoggedIn;
      
      if (this.isLoggedIn) {
        const userStore = useUserStore();
        this.userInfo = userStore.userInfo;
      }
    },
    
    startWechatLogin() {
      // 构建微信授权URL
      const authUrl = `https://open.weixin.qq.com/connect/qrconnect?appid=${this.wechatAppId}&redirect_uri=${this.redirectUri}&response_type=code&scope=snsapi_login&state=STATE#wechat_redirect`;
      
      // 打开微信授权页面
      const popup = window.open(authUrl, 'wechat_auth', 'width=600,height=600');
      
      // 监听授权结果
      window.addEventListener('message', this.handleWechatAuth, false);
    },
    
    async handleWechatAuth(event) {
      if (event.origin !== window.location.origin) return;
      
      const { code, openid, userInfo } = event.data;
      
      if (code && openid) {
        try {
          // 调用重新授权接口
          const result = await wechatReAuth(code, openid, userInfo);
          
          // 更新用户信息
          const userStore = useUserStore();
          await userStore.setUserInfo(result.user);
          
          this.isLoggedIn = true;
          this.userInfo = result.user;
          
          // 关闭弹窗
          if (window.wechatAuthPopup) {
            window.wechatAuthPopup.close();
          }
          
          this.$emit('login-success', result);
          
        } catch (error) {
          console.error('微信登录失败:', error);
          this.$emit('login-error', error);
        }
      }
    },
    
    async logout() {
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('openid');
      
      this.isLoggedIn = false;
      this.userInfo = {};
      
      const userStore = useUserStore();
      userStore.clearUserInfo();
      
      this.$emit('logout');
    }
  }
};
</script>

<style scoped>
.wechat-login {
  text-align: center;
  padding: 20px;
}

.wechat-btn {
  background: #07c160;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 auto;
}

.wechat-btn:hover {
  background: #06ad56;
}

.wechat-icon {
  width: 20px;
  height: 20px;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="white" d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 4.882-1.932 7.621-.896.276-2.029-.896-3.772-2.551-4.72a9.78 9.78 0 0 0-1.898-.713zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 