<template>
  <div class="wechat-callback">
    <div class="loading">
      <div class="spinner"></div>
      <p>正在处理微信授权...</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WechatCallback',
  mounted() {
    this.handleCallback();
  },
  
  methods: {
    handleCallback() {
      // 获取URL参数
      const urlParams = new URLSearchParams(window.location.search);
      const code = urlParams.get('code');
      const state = urlParams.get('state');
      
      if (code) {
        // 从localStorage获取之前保存的openid
        const openid = localStorage.getItem('openid');
        
        if (openid) {
          // 发送消息给父窗口
          if (window.opener) {
            window.opener.postMessage({
              code: code,
              openid: openid,
              type: 'wechat_auth_success'
            }, window.location.origin);
          }
        }
      }
      
      // 关闭当前窗口
      setTimeout(() => {
        window.close();
      }, 1000);
    }
  }
};
</script>

<style scoped>
.wechat-callback {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: #f8f9fa;
}

.loading {
  text-align: center;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #07c160;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 