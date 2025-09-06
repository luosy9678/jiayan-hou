<template>
  <div class="diary-create">
    <van-nav-bar
      title="写日记"
      left-text="取消"
      right-text="保存"
      @click-left="onCancel"
      @click-right="onSave"
    />
    
    <div class="diary-content">
      <!-- 心情评分 -->
      <van-cell-group title="今天的心情">
        <van-cell>
          <div class="mood-rating">
            <span class="mood-label">心情评分：</span>
            <van-rate
              v-model="diaryData.moodScore"
              :count="5"
              size="20"
              color="#ff6b6b"
              void-color="#ddd"
            />
            <span class="mood-text">{{ getMoodText(diaryData.moodScore) }}</span>
          </div>
        </van-cell>
      </van-cell-group>

      <!-- 日记内容 -->
      <van-cell-group title="日记内容">
        <van-field
          v-model="diaryData.content"
          type="textarea"
          placeholder="记录今天的戒烟感受..."
          :rows="8"
          maxlength="1000"
          show-word-limit
          @input="onContentInput"
        />
      </van-cell-group>

      <!-- 标签管理 -->
      <van-cell-group title="标签">
        <van-cell>
          <div class="tags-container">
            <van-tag
              v-for="tag in diaryData.tags"
              :key="tag"
              type="primary"
              closeable
              @close="removeTag(tag)"
            >
              {{ tag }}
            </van-tag>
            <van-button
              v-if="diaryData.tags.length < 5"
              size="small"
              type="primary"
              plain
              @click="showTagInput = true"
            >
              + 添加标签
            </van-button>
          </div>
        </van-cell>
      </van-cell-group>

      <!-- 图片上传 -->
      <van-cell-group title="图片附件">
        <van-cell>
          <div class="images-container">
            <div
              v-for="(image, index) in diaryData.images"
              :key="index"
              class="image-item"
            >
              <van-image
                :src="image.fileUrl"
                width="80"
                height="80"
                fit="cover"
              />
              <van-icon
                name="cross"
                class="delete-icon"
                @click="removeImage(index)"
              />
            </div>
            <van-uploader
              v-if="diaryData.images.length < 6"
              :after-read="afterRead"
              :max-count="6 - diaryData.images.length"
              accept="image/*"
            >
              <div class="upload-button">
                <van-icon name="plus" size="24" />
                <span>添加图片</span>
              </div>
            </van-uploader>
          </div>
          <div class="image-tip">
            最多上传6张图片，单张不超过5MB
          </div>
        </van-cell>
      </van-cell-group>

      <!-- 日记状态 -->
      <van-cell-group title="日记设置">
        <van-cell title="日记状态">
          <van-radio-group v-model="diaryData.status" direction="horizontal">
            <van-radio name="draft">草稿</van-radio>
            <van-radio name="private">私人日记</van-radio>
            <van-radio name="public">发布日记</van-radio>
          </van-radio-group>
        </van-cell>
      </van-cell-group>
    </div>

    <!-- 标签输入弹窗 -->
    <van-popup
      v-model:show="showTagInput"
      position="bottom"
      :style="{ height: '40%' }"
    >
      <div class="tag-input-popup">
        <van-nav-bar
          title="添加标签"
          left-text="取消"
          right-text="确定"
          @click-left="showTagInput = false"
          @click-right="addTag"
        />
        <van-field
          v-model="newTag"
          placeholder="输入标签名称"
          maxlength="10"
          show-word-limit
        />
        <div class="suggested-tags">
          <p>推荐标签：</p>
          <van-tag
            v-for="tag in suggestedTags"
            :key="tag"
            type="primary"
            plain
            @click="selectSuggestedTag(tag)"
          >
            {{ tag }}
          </van-tag>
        </div>
      </div>
    </van-popup>

    <!-- 加载提示 -->
    <van-loading v-if="loading" class="loading" />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { showToast, showSuccessToast } from 'vant'
import { createDiary } from '../api/diary'

// 响应式数据
const loading = ref(false)
const showTagInput = ref(false)
const newTag = ref('')

// 日记数据
const diaryData = reactive({
  content: '',
  moodScore: 3,
  tags: [],
  images: [],
  status: 'draft'
})

// 推荐标签
const suggestedTags = [
  '戒烟', '坚持', '健康', '困难', '成功', '焦虑', '放松', '运动', '饮食', '睡眠'
]

// 计算属性
const canSave = computed(() => {
  return diaryData.content.trim().length > 0
})

// 方法
const getMoodText = (score) => {
  const texts = ['', '很差', '不好', '一般', '不错', '很好']
  return texts[score] || '一般'
}

const onContentInput = (value) => {
  // 实时计算字数
  diaryData.wordCount = value.length
}

const addTag = () => {
  const tag = newTag.value.trim()
  if (!tag) {
    showToast('请输入标签名称')
    return
  }
  
  if (diaryData.tags.length >= 5) {
    showToast('最多只能添加5个标签')
    return
  }
  
  if (diaryData.tags.includes(tag)) {
    showToast('标签已存在')
    return
  }
  
  diaryData.tags.push(tag)
  newTag.value = ''
  showTagInput.value = false
  showToast('标签添加成功')
}

const removeTag = (tag) => {
  const index = diaryData.tags.indexOf(tag)
  if (index > -1) {
    diaryData.tags.splice(index, 1)
  }
}

const selectSuggestedTag = (tag) => {
  if (!diaryData.tags.includes(tag) && diaryData.tags.length < 5) {
    diaryData.tags.push(tag)
    showToast('标签添加成功')
  } else if (diaryData.tags.includes(tag)) {
    showToast('标签已存在')
  } else {
    showToast('最多只能添加5个标签')
  }
}

const afterRead = (file) => {
  // 检查文件大小
  if (file.file.size > 5 * 1024 * 1024) {
    showToast('图片大小不能超过5MB')
    return
  }
  
  // 检查图片数量
  if (diaryData.images.length >= 6) {
    showToast('最多只能上传6张图片')
    return
  }
  
  // 模拟上传过程
  loading.value = true
  setTimeout(() => {
    // 这里应该调用实际的图片上传API
    const imageData = {
      fileName: file.file.name,
      fileUrl: URL.createObjectURL(file.file),
      fileSize: file.file.size
    }
    diaryData.images.push(imageData)
    loading.value = false
    showToast('图片添加成功')
  }, 1000)
}

const removeImage = (index) => {
  diaryData.images.splice(index, 1)
}

const onCancel = () => {
  // 检查是否有未保存的内容
  if (diaryData.content.trim() || diaryData.tags.length > 0 || diaryData.images.length > 0) {
    // 显示确认对话框
    if (confirm('确定要取消吗？未保存的内容将丢失')) {
      // 返回上一页或清空数据
      history.back()
    }
  } else {
    history.back()
  }
}

const onSave = async () => {
  if (!canSave.value) {
    showToast('请输入日记内容')
    return
  }
  
  if (diaryData.content.trim().length < 10) {
    showToast('日记内容至少10个字符')
    return
  }
  
  loading.value = true
  
  try {
    // 准备请求数据
    const requestData = {
      content: diaryData.content.trim(),
      tags: diaryData.tags,
      images: diaryData.images.map(img => ({
        fileName: img.fileName,
        fileUrl: img.fileUrl,
        fileSize: img.fileSize
      }))
    }
    
    const response = await createDiary(requestData)
    
    if (response.code === 200) {
      showSuccessToast('日记保存成功')
      // 延迟返回，让用户看到成功提示
      setTimeout(() => {
        history.back()
      }, 1500)
    }
  } catch (error) {
    console.error('保存日记失败:', error)
    showToast('保存失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.diary-create {
  background-color: #f7f8fa;
  min-height: 100vh;
}

.diary-content {
  padding: 16px;
}

.mood-rating {
  display: flex;
  align-items: center;
  gap: 12px;
}

.mood-label {
  font-size: 14px;
  color: #323233;
}

.mood-text {
  font-size: 14px;
  color: #646566;
  margin-left: 8px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.images-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 8px;
}

.image-item {
  position: relative;
  display: inline-block;
}

.delete-icon {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #fff;
  border-radius: 50%;
  padding: 2px;
  color: #ee0a24;
  cursor: pointer;
}

.upload-button {
  width: 80px;
  height: 80px;
  border: 2px dashed #dcdee0;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #969799;
  cursor: pointer;
}

.upload-button:hover {
  border-color: #1989fa;
  color: #1989fa;
}

.image-tip {
  font-size: 12px;
  color: #969799;
  margin-top: 8px;
}

.tag-input-popup {
  padding: 16px;
}

.suggested-tags {
  margin-top: 16px;
}

.suggested-tags p {
  font-size: 14px;
  color: #323233;
  margin-bottom: 8px;
}

.suggested-tags .van-tag {
  margin-right: 8px;
  margin-bottom: 8px;
  cursor: pointer;
}

.loading {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 9999;
}
</style> 