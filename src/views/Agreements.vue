<template>
  <div class="agreements-page">
    <van-nav-bar
      title="用户协议"
      left-arrow
      @click-left="$router.go(-1)"
      fixed
      placeholder
    />
    
    <div class="page-content">
      <!-- 协议摘要 -->
      <van-cell-group title="协议概览" :title-style="titleStyle">
        <van-cell v-if="summary" center>
          <template #title>
            <div class="summary-info">
              <span>共 {{ summary.totalCount }} 个协议</span>
              <span>最后更新: {{ summary.lastUpdated }}</span>
            </div>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 协议类型列表 -->
      <van-cell-group title="协议类型" :title-style="titleStyle">
        <van-cell 
          v-for="type in agreementTypes" 
          :key="type.typeCode"
          :title="type.typeName"
          :label="type.description"
          is-link
          @click="showAgreement(type.typeCode)"
        />
      </van-cell-group>

      <!-- 快速查看区域 -->
      <van-cell-group title="快速查看" :title-style="titleStyle">
        <van-grid :column-num="2" :gutter="16">
          <van-grid-item 
            v-for="quickType in quickTypes" 
            :key="quickType.code"
            :text="quickType.name"
            :icon="quickType.icon"
            @click="showAgreement(quickType.code)"
          />
        </van-grid>
      </van-cell-group>
    </div>

    <!-- 协议详情弹窗 -->
    <van-popup
      v-model:show="showPopup"
      position="bottom"
      :style="{ height: '80%' }"
      round
    >
      <div class="agreement-popup">
        <van-nav-bar
          :title="currentAgreement?.title || '协议详情'"
          left-text="关闭"
          @click-left="showPopup = false"
        />
        
        <div class="popup-content">
          <div v-if="currentAgreement" class="agreement-detail">
            <div class="agreement-header">
              <h3>{{ currentAgreement.title }}</h3>
              <div class="agreement-meta">
                <van-tag type="primary">版本: {{ currentAgreement.version }}</van-tag>
                <van-tag type="success">生效日期: {{ currentAgreement.effectiveDate }}</van-tag>
              </div>
            </div>
            
            <div class="content-text" v-html="formatContent(currentAgreement.content)"></div>
          </div>
          
          <van-loading v-if="loading" />
          <van-empty v-if="error" :description="error" />
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { agreementApi } from '../api/agreement'
import { showToast } from 'vant'

export default {
  name: 'Agreements',
  setup() {
    const agreementTypes = ref([])
    const summary = ref(null)
    const currentAgreement = ref(null)
    const showPopup = ref(false)
    const loading = ref(false)
    const error = ref('')

    // 快速查看类型
    const quickTypes = ref([
      { code: 'user-agreement', name: '用户协议', icon: 'description' },
      { code: 'privacy-policy', name: '隐私政策', icon: 'shield-o' },
      { code: 'membership-agreement', name: '会员协议', icon: 'vip-card-o' },
      { code: 'terms-of-service', name: '服务条款', icon: 'service-o' }
    ])

    // 标题样式
    const titleStyle = {
      background: '#4CAF50',
      color: 'white',
      fontSize: '12px',
      padding: '8px 16px 4px'
    }

    // 加载协议类型列表
    const loadAgreementTypes = async () => {
      try {
        const response = await agreementApi.getAgreementTypes()
        agreementTypes.value = response.data
      } catch (err) {
        console.error('加载协议类型失败:', err)
        showToast('加载协议类型失败')
      }
    }

    // 加载协议摘要
    const loadSummary = async () => {
      try {
        const response = await agreementApi.getAgreementsSummary()
        summary.value = response.data
      } catch (err) {
        console.error('加载协议摘要失败:', err)
      }
    }

    // 显示协议详情
    const showAgreement = async (typeCode) => {
      try {
        loading.value = true
        error.value = ''
        showPopup.value = true
        
        const response = await agreementApi.getAgreementByType(typeCode)
        currentAgreement.value = response.data
      } catch (err) {
        console.error('加载协议内容失败:', err)
        error.value = '加载协议内容失败'
        showToast('加载协议内容失败')
      } finally {
        loading.value = false
      }
    }

    // 格式化协议内容
    const formatContent = (content) => {
      if (!content) return ''
      return content
        .replace(/\n/g, '<br>')
        .replace(/•/g, '• ')
    }

    // 组件挂载时加载数据
    onMounted(async () => {
      await Promise.all([
        loadAgreementTypes(),
        loadSummary()
      ])
    })

    return {
      agreementTypes,
      summary,
      currentAgreement,
      showPopup,
      loading,
      error,
      quickTypes,
      titleStyle,
      showAgreement,
      formatContent
    }
  }
}
</script>

<style scoped>
.agreements-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.page-content {
  padding: 16px;
}

.summary-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.agreement-popup {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.popup-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.agreement-detail {
  margin-bottom: 20px;
}

.agreement-header h3 {
  margin: 0 0 16px 0;
  color: #333;
  font-size: 20px;
  text-align: center;
}

.agreement-meta {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 20px;
}

.content-text {
  line-height: 1.8;
  color: #333;
  font-size: 14px;
  white-space: pre-line;
  background: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
}

.van-cell-group__title {
  background: #4CAF50 !important;
  color: white !important;
  font-size: 12px !important;
  padding: 8px 16px 4px !important;
}

.van-grid-item__text {
  font-size: 12px;
  margin-top: 8px;
}
</style> 