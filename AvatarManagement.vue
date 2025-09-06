<template>
  <div class="avatar-management">
    <van-nav-bar
      title="头像管理"
      left-text="返回"
      left-arrow
      @click-left="goBack"
    />
    
    <div class="content">
      <!-- 当前头像显示 -->
      <div class="current-avatar-section">
        <h3>当前头像</h3>
        <div class="avatar-display">
          <van-image
            :src="avatarUrl"
            width="120"
            height="120"
            round
            fit="cover"
            @error="handleAvatarError"
          />
          <p class="user-name">{{ userInfo.nickname || '用户' }}</p>
        </div>
      </div>

      <!-- 头像操作按钮 -->
      <div class="avatar-actions">
        <van-button 
          type="primary" 
          block 
          @click="showUploadDialog = true"
          icon="photo-o"
        >
          更换头像
        </van-button>
        
        <van-button 
          type="info" 
          block 
          @click="showUrlDialog = true"
          icon="link-o"
        >
          从URL下载
        </van-button>
        
        <van-button 
          v-if="userInfo.avatar"
          type="danger" 
          block 
          @click="deleteAvatar"
          icon="delete-o"
        >
          删除头像
        </van-button>
      </div>

      <!-- 头像信息 -->
      <div class="avatar-info">
        <van-cell-group>
          <van-cell title="头像文件名" :value="userInfo.avatar || '无'" />
          <van-cell title="头像URL" :value="avatarUrl" />
        </van-cell-group>
      </div>
    </div>

    <!-- 上传头像弹窗 -->
    <van-dialog
      v-model:show="showUploadDialog"
      title="上传头像"
      :show-confirm-button="false"
      :show-cancel-button="false"
    >
      <div class="upload-content">
        <van-uploader
          v-model="fileList"
          :max-count="1"
          :after-read="afterRead"
          accept="image/*"
        />
        <div class="upload-tips">
          <p>支持格式：JPG、PNG、GIF、WebP</p>
          <p>文件大小：最大5MB</p>
        </div>
      </div>
    </van-dialog>

    <!-- URL下载弹窗 -->
    <van-dialog
      v-model:show="showUrlDialog"
      title="从URL下载头像"
      :show-confirm-button="false"
      :show-cancel-button="false"
    >
      <div class="url-content">
        <van-field
          v-model="avatarUrlInput"
          label="头像URL"
          placeholder="请输入头像URL"
          clearable
        />
        <div class="url-actions">
          <van-button type="primary" @click="downloadFromUrl">下载</van-button>
          <van-button @click="showUrlDialog = false">取消</van-button>
        </div>
      </div>
    </van-dialog>

    <!-- 文件上传输入 -->
    <input
      ref="fileInput"
      type="file"
      accept="image/*"
      style="display: none"
      @change="handleFileSelect"
    />
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue';
import { Toast, Dialog } from 'vant';
import avatarUtils from './avatar-utils.js';

export default {
  name: 'AvatarManagement',
  setup() {
    const userInfo = reactive({
      id: '',
      nickname: '',
      avatar: ''
    });

    const showUploadDialog = ref(false);
    const showUrlDialog = ref(false);
    const fileList = ref([]);
    const avatarUrlInput = ref('');
    const fileInput = ref(null);

    // 计算头像URL
    const avatarUrl = computed(() => {
      return avatarUtils.buildAvatarUrl(userInfo.avatar);
    });

    // 获取用户信息
    const loadUserInfo = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          Toast.fail('请先登录');
          return;
        }

        const response = await fetch('http://localhost:8081/api/v1/user/profile', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        const result = await response.json();
        if (result.code === 200) {
          Object.assign(userInfo, result.data);
        } else {
          Toast.fail('获取用户信息失败');
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        Toast.fail('获取用户信息失败');
      }
    };

    // 头像错误处理
    const handleAvatarError = () => {
      console.log('头像加载失败');
    };

    // 文件上传后处理
    const afterRead = async (file) => {
      try {
        const formData = new FormData();
        formData.append('file', file.file);

        const token = localStorage.getItem('token');
        const response = await fetch('http://localhost:8081/api/v1/avatar/upload', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`
          },
          body: formData
        });

        const result = await response.json();
        if (result.code === 200) {
          userInfo.avatar = result.data;
          showUploadDialog.value = false;
          fileList.value = [];
          Toast.success('头像上传成功');
        } else {
          Toast.fail('头像上传失败');
        }
      } catch (error) {
        console.error('头像上传失败:', error);
        Toast.fail('头像上传失败');
      }
    };

    // 从URL下载头像
    const downloadFromUrl = async () => {
      if (!avatarUrlInput.value.trim()) {
        Toast.fail('请输入头像URL');
        return;
      }

      try {
        const token = localStorage.getItem('token');
        const response = await fetch('http://localhost:8081/api/v1/avatar/download', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: `avatarUrl=${encodeURIComponent(avatarUrlInput.value.trim())}`
        });

        const result = await response.json();
        if (result.code === 200) {
          userInfo.avatar = result.data;
          showUrlDialog.value = false;
          avatarUrlInput.value = '';
          Toast.success('头像下载成功');
        } else {
          Toast.fail('头像下载失败');
        }
      } catch (error) {
        console.error('头像下载失败:', error);
        Toast.fail('头像下载失败');
      }
    };

    // 删除头像
    const deleteAvatar = () => {
      Dialog.confirm({
        title: '确认删除',
        message: '确定要删除当前头像吗？',
      }).then(async () => {
        try {
          const token = localStorage.getItem('token');
          const response = await fetch('http://localhost:8081/api/v1/avatar/delete', {
            method: 'DELETE',
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });

          const result = await response.json();
          if (result.code === 200) {
            userInfo.avatar = '';
            Toast.success('头像删除成功');
          } else {
            Toast.fail('头像删除失败');
          }
        } catch (error) {
          console.error('头像删除失败:', error);
          Toast.fail('头像删除失败');
        }
      }).catch(() => {
        // 用户取消
      });
    };

    // 返回上一页
    const goBack = () => {
      window.history.back();
    };

    onMounted(() => {
      loadUserInfo();
    });

    return {
      userInfo,
      avatarUrl,
      showUploadDialog,
      showUrlDialog,
      fileList,
      avatarUrlInput,
      fileInput,
      handleAvatarError,
      afterRead,
      downloadFromUrl,
      deleteAvatar,
      goBack
    };
  }
};
</script>

<style scoped>
.avatar-management {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.content {
  padding: 16px;
}

.current-avatar-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  text-align: center;
}

.current-avatar-section h3 {
  margin: 0 0 16px 0;
  color: #323233;
}

.avatar-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.user-name {
  margin: 0;
  color: #646566;
  font-size: 14px;
}

.avatar-actions {
  background: white;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.avatar-info {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.upload-content {
  padding: 20px;
}

.upload-tips {
  margin-top: 16px;
  text-align: center;
  color: #969799;
  font-size: 12px;
}

.upload-tips p {
  margin: 4px 0;
}

.url-content {
  padding: 20px;
}

.url-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
  justify-content: center;
}
</style> 