<template>
  <div class="settings">
    <van-cell-group>
      <!-- 通知设置 -->
      <van-cell title="推送通知">
        <template #right-icon>
          <van-switch
            v-model="settings.notifications"
            @change="updateSettings"
          />
        </template>
      </van-cell>
      
      <!-- 声音设置 -->
      <van-cell title="声音提醒">
        <template #right-icon>
          <van-switch
            v-model="settings.sound"
            @change="updateSettings"
          />
        </template>
      </van-cell>
      
      <!-- 震动设置 -->
      <van-cell title="震动提醒">
        <template #right-icon>
          <van-switch
            v-model="settings.vibration"
            @change="updateSettings"
          />
        </template>
      </van-cell>
      
      <!-- 自动保存 -->
      <van-cell title="自动保存">
        <template #right-icon>
          <van-switch
            v-model="settings.autoSave"
            @change="updateSettings"
          />
        </template>
      </van-cell>
    </van-cell-group>

    <!-- 其他设置 -->
    <van-cell-group title="其他">
      <!-- 清除缓存 -->
      <van-cell
        title="清除缓存"
        is-link
        @click="clearCache"
      />
      
      <!-- 关于我们 -->
      <van-cell
        title="关于我们"
        is-link
        @click="showAbout = true"
      />
      
      <!-- 退出登录 -->
      <van-cell
        title="退出登录"
        is-link
        @click="logout"
      />
    </van-cell-group>

    <!-- 关于我们弹窗 -->
    <van-dialog
      v-model:show="showAbout"
      title="关于我们"
      :show-confirm-button="false"
      :show-cancel-button="false"
    >
      <div class="about-content">
        <p>戒烟助手 v1.0.0</p>
        <p>帮助您科学戒烟，重获健康生活</p>
        <p>© 2024 戒烟助手团队</p>
      </div>
    </van-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import { Toast, Dialog } from 'vant';
import { useRouter } from 'vue-router';

export default {
  name: 'Settings',
  setup() {
    const router = useRouter();
    const showAbout = ref(false);

    const settings = reactive({
      notifications: true,
      sound: true,
      vibration: false,
      autoSave: true
    });

    // 加载设置
    const loadSettings = () => {
      const savedSettings = localStorage.getItem('appSettings');
      if (savedSettings) {
        Object.assign(settings, JSON.parse(savedSettings));
      }
    };

    // 更新设置
    const updateSettings = () => {
      localStorage.setItem('appSettings', JSON.stringify(settings));
      Toast.success('设置已保存');
    };

    // 清除缓存
    const clearCache = () => {
      Dialog.confirm({
        title: '确认清除',
        message: '确定要清除所有缓存数据吗？',
      }).then(() => {
        localStorage.clear();
        Toast.success('缓存已清除');
        // 重新加载设置
        loadSettings();
      }).catch(() => {
        // 用户取消
      });
    };

    // 退出登录
    const logout = () => {
      Dialog.confirm({
        title: '确认退出',
        message: '确定要退出登录吗？',
      }).then(() => {
        localStorage.removeItem('token');
        Toast.success('已退出登录');
        router.push('/login');
      }).catch(() => {
        // 用户取消
      });
    };

    onMounted(() => {
      loadSettings();
    });

    return {
      settings,
      showAbout,
      updateSettings,
      clearCache,
      logout
    };
  }
};
</script>

<style scoped>
.settings {
  padding: 16px;
}

.about-content {
  padding: 20px;
  text-align: center;
}

.about-content p {
  margin: 8px 0;
  color: #666;
}
</style> 