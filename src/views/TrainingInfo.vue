<template>
  <div class="training-info">
    <!-- 其他内容 -->
    
    <!-- 微信登录组件 -->
    <WechatLogin 
      @login-success="onLoginSuccess"
      @login-error="onLoginError"
      @logout="onLogout"
    />
  </div>
</template>

<script>
import WechatLogin from '@/components/WechatLogin.vue';

export default {
  components: {
    WechatLogin
  },
  
  methods: {
    onLoginSuccess(userData) {
      console.log('微信登录成功:', userData);
      // 处理登录成功逻辑
    },
    
    onLoginError(error) {
      console.error('微信登录失败:', error);
      // 处理登录失败逻辑
    },
    
    onLogout() {
      console.log('用户退出登录');
      // 处理退出登录逻辑
    }
  }
};
</script>