<template>
  <div class="agreement-viewer">
    <!-- 协议类型选择 -->
    <van-cell-group title="选择协议类型" :title-style="titleStyle">
      <van-cell 
        v-for="type in agreementTypes" 
        :key="type.typeCode"
        :title="type.typeName"
        :label="type.description"
        is-link
        @click="loadAgreement(type.typeCode)"
      />
    </van-cell-group>

    <!-- 协议内容展示 -->
    <div v-if="currentAgreement" class="agreement-content">
      <van-cell-group title="协议内容" :title-style="titleStyle">
        <van-cell>
          <template #title>
            <div class="agreement-header">
              <h3>{{ currentAgreement.title }}</h3>
              <div class="agreement-meta">
                <span class="version">版本: {{ currentAgreement.version }}</span>
                <span class="date">生效日期: {{ currentAgreement.effectiveDate }}</span>
              </div>
            </div>
          </template>
        </van-cell>
        
        <van-cell>
          <div class="content-text" v-html="formatContent(currentAgreement.content)"></div>
        </van-cell>
      </van-cell-group>
    </div>

    <!-- 加载状态 -->
    <van-loading v-if="loading" class="loading" />
    
    <!-- 错误提示 -->
    <van-empty v-if="error" :description="error" />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { agreementApi } from '../api/agreement'
import { showToast } from 'vant'

export default {
  name: 'AgreementViewer',
  props: {
    // 默认显示的协议类型
    defaultType: {
      type: String,
      default: 'user-agreement'
    }
  },
  setup(props) {
    const agreementTypes = ref([])
    const currentAgreement = ref(null)
    const loading = ref(false)
    const error = ref('')

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
        loading.value = true
        const response = await agreementApi.getAgreementTypes()
        agreementTypes.value = response.data
      } catch (err) {
        console.error('加载协议类型失败:', err)
        error.value = '加载协议类型失败'
        showToast('加载协议类型失败')
      } finally {
        loading.value = false
      }
    }

    // 加载指定协议内容
    const loadAgreement = async (typeCode) => {
      try {
        loading.value = true
        error.value = ''
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
      await loadAgreementTypes()
      if (props.defaultType) {
        await loadAgreement(props.defaultType)
      }
    })

    return {
      agreementTypes,
      currentAgreement,
      loading,
      error,
      titleStyle,
      loadAgreement,
      formatContent
    }
  }
}
</script>

<style scoped>
.agreement-viewer {
  padding: 16px;
}

.agreement-content {
  margin-top: 20px;
}

.agreement-header h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 18px;
}

.agreement-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #666;
}

.version, .date {
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 4px;
}

.content-text {
  line-height: 1.6;
  color: #333;
  font-size: 14px;
  white-space: pre-line;
}

.loading {
  text-align: center;
  margin: 40px 0;
}

.van-cell-group__title {
  background: #4CAF50 !important;
  color: white !important;
  font-size: 12px !important;
  padding: 8px 16px 4px !important;
}
</style> 