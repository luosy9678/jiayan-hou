<template>
  <div class="personal-info">
    <van-cell-group>
      <!-- 头像部分 -->
      <van-cell title="头像" is-link @click="showAvatarUpload = true">
        <template #right-icon>
          <van-image
            :src="avatarUrl"
            width="50"
            height="50"
            round
            fit="cover"
            @error="handleAvatarError"
          />
        </template>
      </van-cell>
      
      <!-- 昵称 -->
      <van-field
        v-model="userInfo.nickname"
        label="昵称"
        placeholder="请输入昵称"
        @blur="updateUserInfo"
      />
      
      <!-- 手机号 -->
      <van-field
        v-model="userInfo.phone"
        label="手机号"
        readonly
        disabled
      />
      
      <!-- 邮箱 -->
      <van-field
        v-model="userInfo.email"
        label="邮箱"
        placeholder="请输入邮箱"
        @blur="updateUserInfo"
      />
      
      <!-- 每日吸烟量 -->
      <van-field
        v-model="userInfo.dailyCigarettes"
        label="每日吸烟量"
        type="number"
        placeholder="请输入每日吸烟量"
        @blur="updateUserInfo"
      />
      
      <!-- 每包价格 -->
      <van-field
        v-model="userInfo.pricePerPack"
        label="每包价格"
        type="number"
        placeholder="请输入每包价格"
        @blur="updateUserInfo"
      />
      
      <!-- 戒烟开始日期 -->
      <van-field
        v-model="userInfo.quitStartDate"
        label="戒烟开始日期"
        type="date"
        @change="updateUserInfo"
      />
    </van-cell-group>

    <!-- 头像上传弹窗 -->
    <van-dialog
      v-model:show="showAvatarUpload"
      title="更换头像"
      :show-confirm-button="false"
      :show-cancel-button="false"
    >
      <div class="avatar-upload-content">
        <van-button type="primary" @click="uploadFromFile">从相册选择</van-button>
        <van-button type="primary" @click="uploadFromUrl">从URL下载</van-button>
        <van-button v-if="userInfo.avatar" type="danger" @click="deleteAvatar">删除头像</van-button>
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
import { Toast } from 'vant';
import avatarUtils from './avatar-utils';

export default {
  name: 'PersonalInfo',
  setup() {
    const userInfo = reactive({
      id: '',
      nickname: '',
      phone: '',
      email: '',
      avatar: '',
      dailyCigarettes: '',
      pricePerPack: '',
      quitStartDate: ''
    });

    const showAvatarUpload = ref(false);
    const fileInput = ref(null);

    // 计算头像URL
    const avatarUrl = computed(() => {
      console.log('PersonalInfo - 当前头像值:', userInfo.avatar);
      const url = avatarUtils.buildAvatarUrl(userInfo.avatar);
      console.log('PersonalInfo - 构建的头像URL:', url);
      return url;
    });

    // 获取用户信息
    const loadUserInfo = async () => {
      try {
        const token = localStorage.getItem('token');
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

    // 更新用户信息
    const updateUserInfo = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await fetch('http://localhost:8081/api/v1/user/profile', {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            nickname: userInfo.nickname,
            email: userInfo.email,
            dailyCigarettes: parseInt(userInfo.dailyCigarettes) || null,
            pricePerPack: parseFloat(userInfo.pricePerPack) || null,
            quitStartDate: userInfo.quitStartDate
          })
        });
        
        const result = await response.json();
        if (result.code === 200) {
          Toast.success('更新成功');
        } else {
          Toast.fail('更新失败');
        }
      } catch (error) {
        console.error('更新用户信息失败:', error);
        Toast.fail('更新失败');
      }
    };

    // 头像错误处理
    const handleAvatarError = () => {
      console.log('头像加载失败，使用默认头像');
    };

    // 从文件上传头像
    const uploadFromFile = () => {
      fileInput.value?.click();
    };

    // 处理文件选择
    const handleFileSelect = async (event) => {
      const file = event.target.files[0];
      if (!file) return;

      try {
        const formData = new FormData();
        formData.append('file', file);

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
          showAvatarUpload.value = false;
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
    const uploadFromUrl = async () => {
      const url = prompt('请输入头像URL:');
      if (!url) return;

      try {
        const token = localStorage.getItem('token');
        const response = await fetch('http://localhost:8081/api/v1/avatar/download', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: `avatarUrl=${encodeURIComponent(url)}`
        });

        const result = await response.json();
        if (result.code === 200) {
          userInfo.avatar = result.data;
          showAvatarUpload.value = false;
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
    const deleteAvatar = async () => {
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
          showAvatarUpload.value = false;
          Toast.success('头像删除成功');
        } else {
          Toast.fail('头像删除失败');
        }
      } catch (error) {
        console.error('头像删除失败:', error);
        Toast.fail('头像删除失败');
      }
    };

    onMounted(() => {
      loadUserInfo();
    });

    return {
      userInfo,
      avatarUrl,
      showAvatarUpload,
      fileInput,
      handleAvatarError,
      updateUserInfo,
      uploadFromFile,
      handleFileSelect,
      uploadFromUrl,
      deleteAvatar
    };
  }
};
</script>

<style scoped>
.personal-info {
  padding: 16px;
}

.avatar-upload-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.avatar-upload-content .van-button {
  margin-bottom: 8px;
}
</style> 