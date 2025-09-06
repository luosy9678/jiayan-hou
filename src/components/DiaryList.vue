<template>
  <div class="diary-list">
    <van-nav-bar title="我的日记" />
    
    <!-- 筛选栏 -->
    <van-tabs v-model:active="activeTab" sticky>
      <van-tab title="全部" name="all">
        <diary-content :diaries="filteredDiaries" />
      </van-tab>
      <van-tab title="草稿" name="draft">
        <diary-content :diaries="filteredDiaries" />
      </van-tab>
      <van-tab title="私人" name="private">
        <diary-content :diaries="filteredDiaries" />
      </van-tab>
      <van-tab title="发布" name="public">
        <diary-content :diaries="filteredDiaries" />
      </van-tab>
    </van-tabs>

    <!-- 搜索栏 -->
    <van-search
      v-model="searchKeyword"
      placeholder="搜索日记内容或标签"
      @search="onSearch"
      @clear="onClearSearch"
    />

    <!-- 日记内容 -->
    <div class="diary-content">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div
            v-for="diary in displayDiaries"
            :key="diary.id"
            class="diary-item"
            @click="viewDiary(diary)"
          >
            <div class="diary-header">
              <div class="diary-date">
                <span class="date">{{ formatDate(diary.recordDate) }}</span>
                <span class="time">{{ formatTime(diary.recordTime) }}</span>
              </div>
              <div class="diary-status">
                <van-tag :type="getStatusType(diary.status)">
                  {{ getStatusText(diary.status) }}
                </van-tag>
                <van-tag v-if="diary.isForwarded" type="success" size="small">
                  已转发
                </van-tag>
              </div>
            </div>

            <div class="diary-body">
              <div class="diary-content-text">
                {{ diary.content }}
              </div>
              
              <!-- 图片预览 -->
              <div v-if="diary.images && diary.images.length > 0" class="diary-images">
                <van-image
                  v-for="(image, index) in diary.images.slice(0, 3)"
                  :key="index"
                  :src="image.fileUrl"
                  width="60"
                  height="60"
                  fit="cover"
                  radius="4"
                />
                <div v-if="diary.images.length > 3" class="more-images">
                  +{{ diary.images.length - 3 }}
                </div>
              </div>
            </div>

            <div class="diary-footer">
              <div class="diary-tags">
                <van-tag
                  v-for="tag in diary.tags.slice(0, 3)"
                  :key="tag"
                  type="primary"
                  size="small"
                  plain
                >
                  {{ tag }}
                </van-tag>
                <span v-if="diary.tags.length > 3" class="more-tags">
                  +{{ diary.tags.length - 3 }}
                </span>
              </div>
              
              <div class="diary-meta">
                <span class="mood-score">
                  <van-icon name="smile-o" />
                  {{ diary.moodScore }}/5
                </span>
                <span class="word-count">{{ diary.wordCount }}字</span>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="diary-actions">
              <van-button
                v-if="diary.status === 'draft'"
                size="small"
                type="primary"
                @click.stop="editDiary(diary)"
              >
                编辑
              </van-button>
              <van-button
                v-if="diary.status === 'private' && !diary.isForwarded"
                size="small"
                type="success"
                @click.stop="forwardDiary(diary)"
              >
                转发到论坛
              </van-button>
              <van-button
                size="small"
                type="danger"
                @click.stop="deleteDiary(diary)"
              >
                删除
              </van-button>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>

    <!-- 浮动按钮 -->
    <van-floating-button
      icon="plus"
      @click="createDiary"
      class="create-button"
    />

    <!-- 删除确认弹窗 -->
    <van-dialog
      v-model:show="showDeleteDialog"
      title="确认删除"
      message="确定要删除这篇日记吗？删除后无法恢复。"
      show-cancel-button
      @confirm="confirmDelete"
    />

    <!-- 转发弹窗 -->
    <van-popup
      v-model:show="showForwardDialog"
      position="bottom"
      :style="{ height: '50%' }"
    >
      <div class="forward-popup">
        <van-nav-bar
          title="转发到论坛"
          left-text="取消"
          right-text="确定"
          @click-left="showForwardDialog = false"
          @click-right="confirmForward"
        />
        <van-cell-group title="选择版块">
          <van-radio-group v-model="selectedSection">
            <van-cell
              v-for="section in forumSections"
              :key="section.value"
              :title="section.label"
              clickable
              @click="selectedSection = section.value"
            >
              <van-radio :name="section.value" />
            </van-cell>
          </van-radio-group>
        </van-cell-group>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import { getDiaryList, deleteDiary, forwardDiaryToForum } from '../api/diary'

// 响应式数据
const activeTab = ref('all')
const searchKeyword = ref('')
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const showDeleteDialog = ref(false)
const showForwardDialog = ref(false)
const selectedSection = ref('')
const diaryToDelete = ref(null)
const diaryToForward = ref(null)

// 日记数据
const diaries = ref([])
const page = ref(1)
const pageSize = 20

// 论坛版块
const forumSections = [
  { value: 'quit_experience', label: '戒烟心得' },
  { value: 'success_story', label: '成功故事' },
  { value: 'help_support', label: '求助交流' },
  { value: 'health_tips', label: '健康贴士' },
  { value: 'motivation', label: '激励分享' }
]

// 计算属性
const filteredDiaries = computed(() => {
  let filtered = diaries.value
  
  // 按状态筛选
  if (activeTab.value !== 'all') {
    filtered = filtered.filter(diary => diary.status === activeTab.value)
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(diary => 
      diary.content.toLowerCase().includes(keyword) ||
      diary.tags.some(tag => tag.toLowerCase().includes(keyword))
    )
  }
  
  return filtered
})

const displayDiaries = computed(() => {
  return filteredDiaries.value.slice(0, page.value * pageSize)
})

// 方法
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  
  if (date.toDateString() === today.toDateString()) {
    return '今天'
  } else if (date.toDateString() === yesterday.toDateString()) {
    return '昨天'
  } else {
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }
}

const formatTime = (timeStr) => {
  const time = new Date(timeStr)
  return `${time.getHours().toString().padStart(2, '0')}:${time.getMinutes().toString().padStart(2, '0')}`
}

const getStatusType = (status) => {
  const types = {
    draft: 'default',
    private: 'primary',
    public: 'success',
    deleted: 'danger'
  }
  return types[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    draft: '草稿',
    private: '私人',
    public: '发布',
    deleted: '已删除'
  }
  return texts[status] || '未知'
}

const onSearch = () => {
  page.value = 1
  loadDiaries()
}

const onClearSearch = () => {
  searchKeyword.value = ''
  page.value = 1
  loadDiaries()
}

const onRefresh = async () => {
  page.value = 1
  await loadDiaries()
  refreshing.value = false
}

const onLoad = () => {
  if (!finished.value) {
    page.value++
    loadDiaries()
  }
}

const loadDiaries = async () => {
  try {
    loading.value = true
    const response = await getDiaryList({
      page: page.value,
      pageSize: pageSize
    })
    
    if (response.code === 200) {
      if (page.value === 1) {
        diaries.value = response.data.list || []
      } else {
        diaries.value.push(...(response.data.list || []))
      }
      
      if (response.data.list.length < pageSize) {
        finished.value = true
      }
    }
  } catch (error) {
    console.error('加载日记失败:', error)
    showToast('加载失败，请重试')
  } finally {
    loading.value = false
  }
}

const viewDiary = (diary) => {
  // 跳转到日记详情页
  // router.push(`/diary/${diary.id}`)
  console.log('查看日记:', diary.id)
}

const editDiary = (diary) => {
  // 跳转到日记编辑页
  // router.push(`/diary/edit/${diary.id}`)
  console.log('编辑日记:', diary.id)
}

const createDiary = () => {
  // 跳转到日记创建页
  // router.push('/diary/create')
  console.log('创建日记')
}

const deleteDiary = (diary) => {
  diaryToDelete.value = diary
  showDeleteDialog.value = true
}

const confirmDelete = async () => {
  if (!diaryToDelete.value) return
  
  try {
    const response = await deleteDiary(diaryToDelete.value.id)
    if (response.code === 200) {
      showSuccessToast('删除成功')
      // 从列表中移除
      const index = diaries.value.findIndex(d => d.id === diaryToDelete.value.id)
      if (index > -1) {
        diaries.value.splice(index, 1)
      }
    }
  } catch (error) {
    console.error('删除日记失败:', error)
    showToast('删除失败，请重试')
  } finally {
    diaryToDelete.value = null
    showDeleteDialog.value = false
  }
}

const forwardDiary = (diary) => {
  diaryToForward.value = diary
  selectedSection.value = forumSections[0].value
  showForwardDialog.value = true
}

const confirmForward = async () => {
  if (!diaryToForward.value || !selectedSection.value) return
  
  try {
    const response = await forwardDiaryToForum(diaryToForward.value.id, {
      section: selectedSection.value
    })
    
    if (response.code === 200) {
      showSuccessToast('转发成功')
      // 更新日记状态
      diaryToForward.value.isForwarded = true
      diaryToForward.value.forumSection = selectedSection.value
    }
  } catch (error) {
    console.error('转发失败:', error)
    showToast('转发失败，请重试')
  } finally {
    diaryToForward.value = null
    showForwardDialog.value = false
  }
}

// 生命周期
onMounted(() => {
  loadDiaries()
})
</script>

<style scoped>
.diary-list {
  background-color: #f7f8fa;
  min-height: 100vh;
}

.diary-content {
  padding: 16px;
}

.diary-item {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.diary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.diary-date {
  display: flex;
  flex-direction: column;
}

.date {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
}

.time {
  font-size: 12px;
  color: #969799;
  margin-top: 2px;
}

.diary-status {
  display: flex;
  gap: 8px;
}

.diary-body {
  margin-bottom: 12px;
}

.diary-content-text {
  font-size: 14px;
  line-height: 1.6;
  color: #323233;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.diary-images {
  display: flex;
  gap: 8px;
  align-items: center;
}

.more-images {
  width: 60px;
  height: 60px;
  background: #f2f3f5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #969799;
  font-size: 12px;
}

.diary-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.diary-tags {
  display: flex;
  gap: 6px;
  align-items: center;
}

.more-tags {
  font-size: 12px;
  color: #969799;
}

.diary-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #646566;
}

.mood-score, .word-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.diary-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.create-button {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 999;
}

.forward-popup {
  padding: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .diary-content {
    padding: 12px;
  }
  
  .diary-item {
    padding: 12px;
    margin-bottom: 8px;
  }
  
  .diary-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .diary-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style> 