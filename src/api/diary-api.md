# 戒烟日记API接口文档

## 基础信息

**基础URL**: `http://localhost:8081`  
**API版本**: v1  
**认证方式**: Bearer Token  
**数据格式**: JSON  

---

## 1. 日记管理模块

### 1.1 创建日记 ✅ 已开发

**接口地址**: `POST /diary/create`  
**请求方式**: POST  
**认证要求**: 需要Bearer Token  

**请求参数**:
```json
{
  "content": "今天开始戒烟，感觉有点紧张...",
  "tags": ["戒烟", "坚持", "健康"],
  "images": [
    {
      "fileName": "uuid_generated.jpg",
      "fileUrl": "/diary/image/1/uuid_generated.jpg",
      "fileSize": 1024000,
      "uploadTime": "2024-01-15T10:30:00.000Z"
    }
  ],
  "status": "草稿"
}
```

**参数说明**:
- `content` (必填): 日记内容，最大1000字符
- `tags` (可选): 标签数组，最多5个标签
- `images` (可选): 图片数组，最多6张图片
- `status` (可选): 日记状态，可选值：草稿、存档、发布、删除，默认为"草稿"

**响应格式**:
```text
日记创建成功！用户ID: 1, 日记ID: 1, 内容: 今天开始戒烟，感觉有点紧张..., 字数: 45, 图片数量: 1
```

### 1.2 获取日记列表 ✅ 已开发

**接口地址**: `GET /diary/list`  
**请求方式**: GET  
**认证要求**: 需要Bearer Token  

**响应格式**:
```json
{
  "code": 200,
  "message": "用户 51 的日记列表: 总数=2",
  "data": [
    {
      "id": 1,
      "userId": 51,
      "recordDate": "2024-01-15",
      "recordTime": "2024-01-15T10:30:00",
      "content": "今天开始戒烟，感觉有点紧张，但是为了健康，我一定要坚持下去！",
      "moodScore": 4,
      "tags": ["戒烟", "坚持", "健康"],
      "images": [
        {
          "fileName": "uuid_generated.jpg",
          "originalName": "photo1.jpg",
          "fileUrl": "/diary/image/51/uuid_generated.jpg",
          "fileSize": 1024000,
          "uploadTime": "2024-01-15T10:30:00"
        }
      ],
      "wordCount": 45,
      "status": "发布",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    },
    {
      "id": 2,
      "userId": 51,
      "recordDate": "2024-01-16",
      "recordTime": "2024-01-16T14:20:00",
      "content": "戒烟第二天，虽然有时候还是会想抽烟，但是感觉呼吸更顺畅了。",
      "moodScore": 3,
      "tags": ["戒烟", "坚持", "进步"],
      "images": [],
      "wordCount": 38,
      "status": "发布",
      "createdAt": "2024-01-16T14:20:00",
      "updatedAt": "2024-01-16T14:20:00"
    }
  ]
}
```

**参数说明**:
- `code`: 响应状态码，200表示成功
- `message`: 响应消息，包含用户ID和日记总数
- `data`: 日记列表数组，每个日记包含以下字段：
  - `id`: 日记唯一标识
  - `userId`: 用户ID
  - `recordDate`: 记录日期
  - `recordTime`: 记录时间
  - `content`: 日记内容
  - `moodScore`: 心情评分 (1-5)
  - `tags`: 标签数组
  - `images`: 图片信息数组
  - `wordCount`: 字数统计
  - `status`: 日记状态
  - `createdAt`: 创建时间
  - `updatedAt`: 更新时间

**业务规则**:
- 只返回当前用户的日记
- 排除删除状态的日记
- 按记录时间倒序排列（最新的在前）
- 自动处理标签和图片信息的格式转换

### 1.3 获取日记详情 ✅ 已开发

**接口地址**: `GET /diary/{id}`  
**请求方式**: GET  
**认证要求**: 需要Bearer Token  

**路径参数**:
- `id`: 日记ID

**响应格式**:
```json
{
  "id": 1,
  "userId": 1,
  "recordDate": "2024-01-15",
  "recordTime": "2024-01-15T10:30:00",
  "content": "今天开始戒烟，感觉有点紧张...",
  "moodScore": 4,
  "tags": ["戒烟", "坚持", "健康"],
  "images": [
    {
      "fileName": "uuid_generated.jpg",
      "originalName": "photo1.jpg",
      "fileUrl": "/diary/image/1/uuid_generated.jpg",
      "fileSize": 1024000,
      "uploadTime": "2024-01-15T10:30:00"
    }
  ],
  "wordCount": 45,
  "status": "草稿",
  "isForwarded": false,
  "forumPostId": null,
  "forumSection": null,
  "forwardTime": null,
  "forumViews": 0,
  "forumLikes": 0,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**参数说明**:
- `id`: 日记唯一标识
- `userId`: 用户ID
- `recordDate`: 记录日期
- `recordTime`: 记录时间
- `content`: 日记内容
- `moodScore`: 心情评分 (1-5)
- `tags`: 标签数组
- `images`: 图片信息数组
- `wordCount`: 字数统计
- `status`: 日记状态
- `isForwarded`: 是否已转发到论坛
- `forumPostId`: 论坛帖子ID
- `forumSection`: 论坛版块
- `forwardTime`: 转发时间
- `forumViews`: 论坛浏览量
- `forumLikes`: 论坛点赞数
- `createdAt`: 创建时间
- `updatedAt`: 更新时间

### 1.4 更新日记 ✅ 已开发

**接口地址**: `PUT /diary/{id}`  
**请求方式**: PUT  
**认证要求**: 需要Bearer Token  

**路径参数**:
- `id`: 日记ID

**请求参数**:
```json
{
  "content": "今天坚持减量第三天，感觉比前两天好多了...",
  "tags": ["减量", "坚持", "进步"],
  "images": [
    {
      "fileName": "uuid_generated.jpg",
      "fileUrl": "/diary/image/1/uuid_generated.jpg",
      "fileSize": 1024000,
      "uploadTime": "2024-01-15T10:30:00.000Z"
    }
  ],
  "moodScore": 4,
  "status": "已发布"
}
```

**参数说明**:
- `content` (可选): 日记内容，最大1000字符
- `tags` (可选): 标签数组，最多5个标签
- `images` (可选): 图片数组，最多6张图片
- `moodScore` (可选): 心情评分，1-5分
- `status` (可选): 日记状态，可选值：草稿、存档、发布、删除

**响应格式**:
```text
日记更新成功！用户ID: 1, 日记ID: 1, 内容: 今天坚持减量第三天，感觉比前两天好多了..., 字数: 67, 图片数量: 1, 更新时间: 2024-01-15T15:30:00
```

**业务规则**:
- 只能更新自己的日记
- 已转发到论坛的日记不能修改
- 如果没有内容变化，返回"日记内容无变化，无需更新"
- 自动重新计算字数统计
- 自动更新修改时间

### 1.5 删除日记 ✅ 已开发

**接口地址**: `DELETE /diary/{id}`  
**请求方式**: DELETE  
**认证要求**: 需要Bearer Token  

**路径参数**:
- `id`: 日记ID

**响应格式**:
```text
日记删除成功！用户ID: 1, 日记ID: 1, 内容: 今天坚持减量第三天，感觉比前两天好多了..., 删除时间: 2024-01-15T15:30:00, 注意：这是软删除，日记数据仍然保留在数据库中
```

**业务规则**:
- 只能删除自己的日记
- 已转发到论坛的日记不能删除
- 已删除状态的日记不能重复删除
- 默认使用软删除（状态设为"删除"）
- 软删除后日记数据仍然保留在数据库中

---

### 1.5.1 物理删除日记 ✅ 已开发

**接口地址**: `DELETE /diary/{id}/permanent`  
**请求方式**: DELETE  
**认证要求**: 需要Bearer Token  

**路径参数**:
- `id`: 日记ID

**响应格式**:
```text
日记物理删除成功！用户ID: 1, 日记ID: 1, 内容: 今天坚持减量第三天，感觉比前两天好多了..., 字数: 67, 标签: 减量,坚持,进步, 注意：这是物理删除，日记数据已从数据库中永久移除
```

**业务规则**:
- 只能物理删除自己的日记
- 已转发到论坛的日记不能物理删除
- 物理删除后日记数据永久从数据库中移除
- 谨慎使用，建议优先使用软删除

### 1.6 转发日记到论坛 ❌ 待开发

**接口地址**: `POST /diary/{id}/forward`  
**请求方式**: POST  
**认证要求**: 需要Bearer Token  

**路径参数**:
- `id`: 日记ID

**响应格式**: 待开发

**备注**: 实体中已有相关字段(isForwarded, forumPostId, forumSection, forwardTime, forumViews, forumLikes)，但业务逻辑未实现

### 1.7 获取日记统计信息 ✅ 已开发

**接口地址**: `GET /diary/stats`  
**请求方式**: GET  
**认证要求**: 需要Bearer Token  

**响应格式**:
```text
用户 1 的日记统计: 总数=1
```

### 1.8 日记搜索 ❌ 待开发

**接口地址**: `GET /diary/search`  
**请求方式**: GET  
**认证要求**: 需要Bearer Token  

**响应格式**: 待开发

**备注**: 可考虑按内容、标签、日期范围等条件搜索

---

## 2. 图片管理模块 ✅ 已开发

### 2.1 上传图片

**接口地址**: `POST /diary/image/upload`  
**请求方式**: POST  
**认证要求**: 需要Bearer Token  
**内容类型**: `multipart/form-data`

**请求参数**:
- `files` (必填): 图片文件数组，最多6张
- `userId` (必填): 用户ID

**文件限制**:
- 最大文件大小: 10MB
- 支持格式: jpg, jpeg, png, gif, webp
- 最大数量: 6张

**响应格式**:
```json
{
  "success": true,
  "message": "图片上传成功，共上传 2 张图片",
  "data": [
    {
      "fileName": "uuid_generated_1.jpg",
      "originalName": "photo1.jpg",
      "fileUrl": "/diary/image/1/uuid_generated_1.jpg",
      "fileSize": 1024000,
      "uploadTime": "2024-01-15T10:30:00.000Z"
    }
  ]
}
```

### 2.2 获取图片

**接口地址**: `GET /diary/image/{userId}/{fileName}`  
**请求方式**: GET  
**认证要求**: 无（公开访问）

**路径参数**:
- `userId`: 用户ID
- `fileName`: 图片文件名

**响应**: 图片二进制数据

### 2.3 删除图片

**接口地址**: `DELETE /diary/image/{userId}/{fileName}`  
**请求方式**: DELETE  
**认证要求**: 需要Bearer Token

**路径参数**:
- `userId`: 用户ID
- `fileName`: 图片文件名

**响应格式**:
```json
{
  "success": true,
  "message": "图片删除成功"
}
```

---

## 3. 图片存储说明

### 3.1 存储路径结构 