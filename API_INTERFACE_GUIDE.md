# 知行戒烟APP API接口说明文档

## 📋 文档信息

- **项目名称**: 知行戒烟APP后端服务
- **版本**: v1.0.0
- **基础URL**: `http://localhost:8081/api/v1`
- **更新时间**: 2025-08-15
- **维护人员**: 开发团队

## 🗄️ 数据来源说明

**✅ 当前版本**: 所有协议内容都从数据库读取真实数据，支持动态更新和管理。

**数据库表结构**:
- `agreement_types`: 协议类型表，存储协议类型信息
- `agreements`: 协议内容表，存储具体的协议内容

**数据特点**:
- 支持版本管理：每个协议类型可以有多个版本
- 支持当前版本标识：`is_current` 字段标识当前生效版本
- 支持动态更新：通过管理后台可以修改协议内容
- 支持多语言扩展：后续可扩展多语言支持

**管理方式**:
- 通过管理后台可以创建、编辑、删除协议
- 可以设置某个版本为当前生效版本
- 支持协议内容的富文本编辑

**⚠️ 重要提醒**: 数据库中的协议类型代码使用大写格式（如 `USER_AGREEMENT`），但API接口支持多种格式调用。

---

## ⚠️ 重要：路径配置说明

### 路径配置规则
```
实际访问路径 = context-path + @RequestMapping + @GetMapping
```

**示例：**
- `context-path` = `/api/v1`
- `@RequestMapping` = `/agreements`
- `@GetMapping` = `/types`
- 实际路径 = `/api/v1/agreements/types`

### 常见错误
- ❌ 不要在Controller中重复包含`context-path`
- ❌ 不要使用错误的测试基础URL
- ✅ 使用正确的完整路径进行测试

---

## 🔗 协议相关接口

### 1. 获取协议类型列表

**接口地址**: `GET /api/v1/agreements/types`

**功能描述**: 获取所有可用的协议类型列表

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "typeCode": "USER_AGREEMENT",
      "typeName": "用户协议",
      "description": "用户使用本应用需要遵守的协议条款",
      "url": "/agreements/user-agreement"
    },
    {
      "typeCode": "PRIVACY_POLICY",
      "typeName": "隐私政策",
      "description": "关于用户隐私保护的政策说明",
      "url": "/agreements/privacy-policy"
    },
    {
      "typeCode": "MEMBER_AGREEMENT",
      "typeName": "会员协议",
      "description": "会员服务相关的协议条款",
      "url": "/agreements/membership-agreement"
    }
  ]
}
```

**前端调用示例**:
```javascript
import { agreementApi } from '../api/agreement'

// 获取协议类型列表
const response = await agreementApi.getAgreementTypes()
console.log('协议类型:', response.data)
```

---

### 2. 获取用户服务协议

**接口地址**: `GET /api/v1/agreements/user-agreement`

**功能描述**: 获取用户服务协议的详细内容

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "title": "用户协议",
    "version": "初始版本",
    "effectiveDate": "2024-07-15",
    "content": "欢迎使用知行戒烟APP！\n\n本协议是您与知行戒烟APP之间关于使用本应用服务所订立的协议。\n\n1. 服务内容\n知行戒烟APP为用户提供戒烟相关的健康管理服务。\n\n2. 用户责任\n用户应遵守相关法律法规，不得利用本应用进行违法活动。\n\n3. 隐私保护\n我们承诺保护用户隐私，不会泄露用户个人信息。"
  }
}
```

**前端调用示例**:
```javascript
// 获取用户服务协议
const response = await agreementApi.getUserAgreement()
const agreement = response.data
console.log('协议标题:', agreement.title)
console.log('协议内容:', agreement.content)
```

---

### 3. 获取隐私政策

**接口地址**: `GET /api/v1/agreements/privacy-policy`

**功能描述**: 获取隐私政策的详细内容

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "title": "隐私政策",
    "version": "初始版本",
    "effectiveDate": "2024-07-25",
    "content": "隐私政策\n\n我们非常重视您的隐私保护。本隐私政策说明了我们如何收集、使用和保护您的个人信息。\n\n1. 信息收集\n我们可能收集您的设备信息、使用数据等。\n\n2. 信息使用\n收集的信息仅用于提供服务、改善用户体验。\n\n3. 信息保护\n我们采用行业标准的安全措施保护您的信息。"
  }
}
```

**前端调用示例**:
```javascript
// 获取隐私政策
const response = await agreementApi.getPrivacyPolicy()
const policy = response.data
console.log('政策标题:', policy.title)
console.log('政策内容:', policy.content)
```

---

### 4. 获取会员协议

**接口地址**: `GET /api/v1/agreements/membership-agreement`

**功能描述**: 获取会员协议的详细内容

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "title": "会员协议",
    "version": "初始版本",
    "effectiveDate": "2024-08-05",
    "content": "会员服务协议\n\n感谢您选择我们的会员服务！\n\n1. 会员权益\n会员用户可享受更多高级功能和服务。\n\n2. 会员费用\n会员服务需要支付相应的费用。\n\n3. 服务期限\n会员服务有明确的服务期限。\n\n4. 退费政策\n我们提供合理的退费政策。"
  }
}
```

**前端调用示例**:
```javascript
// 获取会员协议
const response = await agreementApi.getMembershipAgreement()
const agreement = response.data
console.log('协议标题:', agreement.title)
console.log('协议内容:', agreement.content)
```

---

### 5. 获取服务条款

**接口地址**: `GET /api/v1/agreements/terms-of-service`

**功能描述**: 获取服务条款的详细内容

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "title": "服务条款",
    "version": "初始版本",
    "effectiveDate": "2024-08-15",
    "content": "服务条款\n\n本条款规定了知行戒烟APP的服务规则和使用条件。\n\n1. 服务说明\n本应用是一个健康管理平台，主要功能包括戒烟计划管理、健康数据记录等。\n\n2. 使用规则\n用户在使用过程中应遵守相关法律法规，不得发布违法信息。\n\n3. 服务限制\n我们保留限制违规用户使用的权利。\n\n4. 免责声明\n服务按\"现状\"提供，不保证服务无中断或错误。"
  }
}
```

**前端调用示例**:
```javascript
// 获取服务条款
const response = await agreementApi.getTermsOfService()
const terms = response.data
console.log('条款标题:', terms.title)
console.log('条款内容:', terms.content)
```

---

### 6. 根据类型获取协议

**接口地址**: `GET /api/v1/agreements/{typeCode}`

**功能描述**: 根据协议类型代码获取对应的协议内容

**路径参数**:
- `typeCode`: 协议类型代码，支持以下值：
  - `user-agreement` 或 `user_agreement`: 用户服务协议
  - `privacy-policy` 或 `privacy_policy`: 隐私政策
  - `membership-agreement` 或 `membership_agreement`: 会员协议
  - `terms-of-service` 或 `terms_of_service`: 服务条款

**请求示例**:
```
GET /api/v1/agreements/user-agreement
GET /api/v1/agreements/privacy-policy
GET /api/v1/agreements/membership-agreement
GET /api/v1/agreements/terms-of-service
```

**响应示例**: 同上述具体接口的响应格式

**前端调用示例**:
```javascript
// 根据类型获取协议
const typeCode = 'user-agreement'
const response = await agreementApi.getAgreementByType(typeCode)
const agreement = response.data
console.log('协议内容:', agreement.content)
```

---

### 7. 获取协议摘要

**接口地址**: `GET /api/v1/agreements/summary`

**功能描述**: 获取所有协议的摘要信息

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 3,
    "lastUpdated": "2024-08-05",
    "agreements": [
      {
        "type": "用户协议",
        "version": "初始版本",
        "status": "生效中"
      },
      {
        "type": "隐私政策",
        "version": "初始版本",
        "status": "生效中"
      },
      {
        "type": "会员协议",
        "version": "初始版本",
        "status": "生效中"
      }
    ]
  }
}
```

**前端调用示例**:
```javascript
// 获取协议摘要
const response = await agreementApi.getAgreementsSummary()
const summary = response.data
console.log('协议总数:', summary.totalCount)
console.log('最后更新:', summary.lastUpdated)
console.log('协议列表:', summary.agreements)
```

---

## 🛠️ 前端集成指南

### 1. 安装依赖

确保项目中已安装必要的依赖：

```bash
npm install axios
# 或者
yarn add axios
```

### 2. 配置API基础设置

在 `src/api/request.js` 中配置：

```javascript
import axios from 'axios'
import { showToast } from 'vant'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8081/api/v1', // 统一使用8081端口
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

export default service
```

### 3. 使用协议API

```javascript
import { agreementApi } from '../api/agreement'

// 获取所有协议类型
const loadAgreementTypes = async () => {
  try {
    const response = await agreementApi.getAgreementTypes()
    return response.data
  } catch (error) {
    console.error('获取协议类型失败:', error)
    throw error
  }
}

// 获取指定协议内容
const loadAgreementContent = async (typeCode) => {
  try {
    const response = await agreementApi.getAgreementByType(typeCode)
    return response.data
  } catch (error) {
    console.error('获取协议内容失败:', error)
    throw error
  }
}
```

### 4. 错误处理

```javascript
try {
  const response = await agreementApi.getUserAgreement()
  // 处理成功响应
  console.log('协议内容:', response.data)
} catch (error) {
  if (error.response) {
    // 服务器返回错误状态码
    console.error('错误状态码:', error.response.status)
    console.error('错误信息:', error.response.data)
  } else if (error.request) {
    // 请求发送失败
    console.error('网络请求失败:', error.request)
  } else {
    // 其他错误
    console.error('请求配置错误:', error.message)
  }
}
```

---

## 📱 组件使用示例

### AgreementViewer 组件

```vue
<template>
  <div class="agreement-viewer">
    <AgreementViewer :default-type="'user-agreement'" />
  </div>
</template>

<script>
import AgreementViewer from '../components/AgreementViewer.vue'

export default {
  name: 'AgreementPage',
  components: {
    AgreementViewer
  }
}
</script>
```

### Agreements 页面

```vue
<template>
  <div class="agreements-page">
    <Agreements />
  </div>
</template>

<script>
import Agreements from '../views/Agreements.vue'

export default {
  name: 'AgreementsPage',
  components: {
    Agreements
  }
}
</script>
```

---

## 🧪 接口测试

### 1. 使用测试页面

打开 `test-agreement-apis.html` 文件，可以测试所有协议相关的API接口。

### 2. 使用Postman测试

**基础URL**: `http://localhost:8081/api/v1`

**测试接口**:
- `GET /agreements/types` - 获取协议类型列表
- `GET /agreements/user-agreement` - 获取用户服务协议
- `GET /agreements/privacy-policy` - 获取隐私政策
- `GET /agreements/membership-agreement` - 获取会员协议
- `GET /agreements/terms-of-service` - 获取服务条款
- `GET /agreements/summary` - 获取协议摘要

### 3. 使用curl测试

```bash
# 获取协议类型列表
curl -X GET "http://localhost:8081/api/v1/agreements/types"

# 获取用户服务协议
curl -X GET "http://localhost:8081/api/v1/agreements/user-agreement"

# 获取隐私政策
curl -X GET "http://localhost:8081/api/v1/agreements/privacy-policy"
```

---

## 📊 数据结构说明

### 协议数据结构

```json
{
  "title": "协议标题",
  "version": "版本名称",
  "effectiveDate": "生效日期",
  "content": "协议内容（支持换行符）"
}
```

### 协议类型数据结构

```json
{
  "typeCode": "类型代码（大写格式）",
  "typeName": "类型名称",
  "description": "类型描述",
  "url": "接口地址"
}
```

### 协议摘要数据结构

```json
{
  "totalCount": "协议总数",
  "lastUpdated": "最后更新时间",
  "agreements": [
    {
      "type": "协议类型名称",
      "version": "版本名称",
      "status": "状态"
    }
  ]
}
```

---

## ⚠️ 注意事项

1. **端口配置**: 确保后端服务运行在8080端口
2. **CORS配置**: 前端跨域请求需要后端支持
3. **错误处理**: 所有接口都返回统一的ApiResponse格式
4. **内容格式**: 协议内容支持换行符，前端会自动转换为HTML格式
5. **版本管理**: 当前所有协议都是"初始版本"，后续可扩展版本管理功能
6. **路径配置**: 注意不要重复包含context-path路径
7. **数据一致性**: 文档中的示例数据现在完全基于数据库中的真实数据

---

## 🔄 更新日志

### v1.0.1 (2025-08-15)
- ✅ 更新所有示例数据为数据库真实数据
- ✅ 统一协议类型代码格式
- ✅ 修正响应数据结构
- ✅ 完善数据来源说明

### v1.0.0 (2025-08-15)
- ✅ 新增用户服务协议接口
- ✅ 新增隐私政策接口
- ✅ 新增会员协议接口
- ✅ 新增服务条款接口
- ✅ 新增协议类型列表接口
- ✅ 新增协议摘要接口
- ✅ 新增根据类型获取协议接口
- ✅ 修复路径配置问题
- ✅ 完善错误处理和日志记录

---

## 📞 技术支持

如有问题或建议，请联系开发团队：

- **项目负责人**: 开发团队
- **技术支持**: 通过项目Issue反馈
- **文档维护**: 定期更新，保持最新

---

*本文档为知行戒烟APP前端开发提供，请确保使用最新版本。* 