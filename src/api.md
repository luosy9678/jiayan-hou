# 戒烟APP后端接口文档

## 基础信息

**基础URL**: `http://localhost:8081`  

**路由前缀说明**：
- 认证模块：`/auth/*`
- 知识库模块：`/api/v1/knowledge/*` 
- 管理员模块：`/api/v1/admin/*`
- 协议模块：`/agreements/*`
- 其他模块：直接使用模块名作为前缀（如 `/user/*`, `/audios/*`, `/records/*`, `/background/*`, `/avatar/*` 等）

**API版本**: v1  
**认证方式**: Bearer Token  
**数据格式**: JSON

---

## 时间字段说明

### 时区处理规则

**重要说明**：本系统设计为中国用户使用，涉及时间字段的时区处理遵循以下规则：

#### 时间字段含义
- **`timestamp`字段**：
  - 前端传递：北京时间
  - 后端存储：转换为UTC时间存储
  - 返回前端：从UTC转换回北京时间
  - 用途：用于时间计算和业务逻辑

- **`created_at`字段**：
  - 存储：北京时间
  - 用途：用于统计查询和显示

- **`updated_at`字段**：
  - 存储：北京时间
  - 用途：用于记录更新时间

#### 业务逻辑规则
- **统计查询时**：基于`created_at`字段（北京时间）进行日期统计
- **时间显示时**：使用`created_at`字段（北京时间）进行显示
- **时间计算时**：使用`timestamp`字段（UTC时间）进行计算

#### 示例场景
1. **创建记录**：前端发送北京时间 → 后端转换为UTC存储 → 统计基于北京时间
2. **查询记录**：后端返回北京时间给前端 → 确保显示一致性
3. **今日统计**：基于北京时间的"今天"概念进行统计

---  

---

## 1. 用户认证模块

### 1.1 用户注册
```
POST /auth/register
Content-Type: application/json

请求参数:
{
  "phone": "13800138000",        // 手机号（必填，格式：1[3-9]xxxxxxxxx）
  "email": "user@example.com",   // 邮箱（可选）
  "password": "123456",          // 密码（必填）
  "nickname": "用户昵称",         // 昵称（必填）
  "verificationCode": "1234"     // 验证码（必填）
}

响应格式:
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": "123456",
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "expiresIn": 7200,
    "user": {
      "id": "123456",
      "nickname": "用户昵称",
      "phone": "13800138000",
      "email": "user@example.com",
      "avatar": "",
      "backgroundImage": "/uploads/backgrounds/default-bg.jpg",
      "userBio": "这家伙很懒，什么都没有留下",
      "dailyCigarettes": null,
      "pricePerPack": null,
      "quitStartDate": null,
      "memberLevel": "basic",
      "memberExpireDate": null,
      "createdAt": "2024-01-01T00:00:00"
    }
  }
}
```

### 1.2 用户登录
```
POST /auth/login
Content-Type: application/json

请求参数:
{
  "account": "13800138000",      // 手机号或邮箱（必填）
  "password": "123456"           // 密码（必填）
}

响应格式:
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "123456",
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "expiresIn": 7200,
    "user": {
      "id": "123456",
      "nickname": "用户昵称",
      "phone": "13800138000",
      "email": "user@example.com",
      "avatar": "https://example.com/avatar.jpg",
      "backgroundImage": "/uploads/backgrounds/default-bg.jpg",
      "userBio": "这家伙很懒，什么都没有留下",
      "dailyCigarettes": 20,
      "pricePerPack": 40.0,
      "quitStartDate": "2024-01-01",
      "memberLevel": "basic",
      "memberExpireDate": null,
      "createdAt": "2024-01-01T00:00:00"
    }
  }
}
```

### 1.3 发送验证码
```
POST /auth/send-code
Content-Type: application/json

请求参数:
{
  "phone": "13800138000",        // 手机号（必填，格式：1[3-9]xxxxxxxxx）
  "type": "register"             // 类型（必填）：register/login/reset
}

或者使用GET请求：
GET /auth/send-code?phone=13800138000&type=register

响应格式:
{
  "code": 200,
  "message": "验证码发送成功",
  "data": {
    "expiresIn": 300            // 过期时间（秒）
  }
}
```

### 1.4 验证验证码
```
POST /auth/verify-code
Content-Type: application/json

请求参数:
{
  "phone": "13800138000",        // 手机号（必填）
  "code": "1234"                 // 验证码（必填）
}

或者使用GET请求：
GET /auth/verify-code?phone=13800138000&code=1234

响应格式:
{
  "code": 200,
  "message": "验证成功",
  "data": true
}
```

### 1.5 检查手机号是否存在
```
GET /auth/check-phone?phone=13800138000

响应格式:
{
  "code": 200,
  "message": "检查成功",
  "data": {
    "exists": true,              // 是否存在
    "phone": "13800138000"
  }
}
```

### 1.6 检查邮箱是否存在
```
GET /auth/check-email?email=user@example.com

响应格式:
{
  "code": 200,
  "message": "检查成功",
  "data": {
    "exists": false,             // 是否存在
    "email": "user@example.com"
  }
}
```

### 1.7 刷新Token
```
POST /auth/refresh
Authorization: Bearer <refresh_token>

响应格式:
{
  "code": 200,
  "message": "Token刷新成功",
  "data": {
    "userId": "123456",
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "expiresIn": 7200,
    "user": {
      "id": "123456",
      "nickname": "用户昵称",
      "phone": "13800138000",
      "email": "user@example.com",
      "avatar": "https://example.com/avatar.jpg",
      "backgroundImage": "https://example.com/avatar.jpg",
      "userBio": "这家伙很懒，什么都没有留下",
      "dailyCigarettes": 20,
      "pricePerPack": 40.0,
      "quitStartDate": "2024-01-01",
      "memberLevel": "basic",
      "memberExpireDate": null,
      "createdAt": "2024-01-01T00:00:00"
    }
  }
}
```

---

## 2. 第三方登录模块

### 2.1 微信登录 - 获取Access Token
```
POST /auth/wechat/access_token
Content-Type: application/json

请求参数:
{
  "code": "wx_auth_code"         // 微信授权码（必填）
}

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "accessToken": "ACCESS_TOKEN",
    "expiresIn": 7200,
    "refreshToken": "REFRESH_TOKEN",
    "openid": "OPENID",
    "scope": "snsapi_login"
  }
}
```

### 2.2 微信登录 - 获取用户信息
```
POST /auth/wechat/userinfo
Content-Type: application/json

请求参数:
{
  "accessToken": "ACCESS_TOKEN",  // 访问令牌（必填）
  "openid": "OPENID"              // 用户唯一标识（必填）
}

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "openid": "OPENID",
    "nickname": "微信用户",
    "sex": 1,                     // 性别：1-男，2-女，0-未知
    "province": "广东",
    "city": "深圳",
    "country": "中国",
    "headimgurl": "https://thirdwx.qlogo.cn/mmopen/...",
    "unionid": "UNIONID"
  }
}
```

### 2.3 第三方登录绑定（推荐使用）
```
POST /auth/third-party/bind
Content-Type: application/json

请求参数:
{
  "platform": "wechat",         // 平台（必填）：wechat/qq/weibo
  "openid": "OPENID",           // 第三方用户ID（必填）
  "nickname": "微信用户",        // 昵称（可选）
  "avatar": "https://example.com/avatar.jpg", // 头像（可选）
  "sex": 1,                     // 性别（可选）：1-男，2-女，0-未知
  "province": "广东",            // 省份（可选）
  "city": "深圳",                // 城市（可选）
  "unionid": "UNIONID"          // 联合ID（可选）
}

响应格式:
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "123456",
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "expiresIn": 7200,
    "user": {
      "id": "123456",
      "nickname": "微信用户",
      "phone": "third_3420726",  // 自动生成的虚拟手机号
      "email": null,
      "avatar": "https://example.com/avatar.jpg",
      "backgroundImage": "/uploads/backgrounds/default-bg.jpg",
      "userBio": "这家伙很懒，什么都没有留下",
      "dailyCigarettes": null,
      "pricePerPack": null,
      "quitStartDate": null,
      "memberLevel": "basic",
      "memberExpireDate": null,
      "createdAt": "2024-01-01T00:00:00"
    }
  }
}
```

### 2.4 第三方登录流程说明

#### 方式一：直接绑定（推荐）
1. 前端获取第三方平台授权码或用户信息
2. 直接调用 `/auth/third-party/bind` 接口
3. 后端自动处理用户创建/登录逻辑
4. 返回JWT Token和用户信息

#### 方式二：分步获取（适用于需要微信官方API的场景）
1. 调用 `/auth/wechat/access_token` 获取访问令牌
2. 调用 `/auth/wechat/userinfo` 获取用户信息
3. 使用获取的信息调用 `/auth/third-party/bind` 完成登录

### 2.5 第三方登录示例

#### 微信登录示例：
```javascript
// 方式一：直接绑定（推荐）
const wechatLogin = async (openid, nickname, avatar) => {
  const response = await fetch('/auth/third-party/bind', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      platform: 'wechat',
      openid: openid,
      nickname: nickname,
      avatar: avatar
    })
  });
  
  const result = await response.json();
  if (result.code === 200) {
    // 登录成功，保存token
    localStorage.setItem('token', result.data.token);
    return result.data;
  }
};

// 方式二：分步获取
const wechatLoginStep = async (code) => {
  // 1. 获取access token
  const tokenResponse = await fetch('/auth/wechat/access_token', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ code: code })
  });
  const tokenData = await tokenResponse.json();
  
  // 2. 获取用户信息
  const userResponse = await fetch('/auth/wechat/userinfo', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      accessToken: tokenData.data.accessToken,
      openid: tokenData.data.openid
    })
  });
  const userData = await userResponse.json();
  
  // 3. 绑定登录
  const bindResponse = await fetch('/auth/third-party/bind', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      platform: 'wechat',
      openid: userData.data.openid,
      nickname: userData.data.nickname,
      avatar: userData.data.headimgurl,
      sex: userData.data.sex,
      province: userData.data.province,
      city: userData.data.city,
      unionid: userData.data.unionid
    })
  });
  
  const bindData = await bindResponse.json();
  if (bindData.code === 200) {
    localStorage.setItem('token', bindData.data.token);
    return bindData.data;
  }
};
```

### 2.6 错误处理

#### 常见错误码：
- `1001`: 用户不存在（新用户首次登录）
- `1002`: 参数错误（缺少必填字段）
- `1003`: 第三方平台错误（授权失败）
- `1004`: 用户已存在（重复绑定）

#### 错误响应示例：
```json
{
  "code": 1002,
  "message": "openid不能为空",
  "data": null
}
```

---

## 2.5 微信重新授权登录

### 2.5.1 微信重新授权登录接口
```
POST /auth/wechat/re-auth
Content-Type: application/json

请求参数:
{
  "code": "wx_re_auth_code"         // 微信重新授权码（必填）
}

响应格式:
{
  "code": 200,
  "message": "重新授权成功",
  "data": {
    "accessToken": "ACCESS_TOKEN",
    "expiresIn": 7200,
    "refreshToken": "REFRESH_TOKEN",
    "openid": "OPENID",
    "scope": "snsapi_login"
  }
}
```

---

## 3. 用户信息模块

### 3.1 获取用户信息
```
GET /user/profile
Authorization: Bearer <token>

响应格式:
{
  "code": 200,
  "message": "获取用户档案成功",
  "data": {
    "id": "123456",
    "nickname": "用户昵称",
    "phone": "13800138000",
    "email": "user@example.com",
    "avatar": "https://example.com/avatar.jpg",
    "backgroundImage": "/uploads/backgrounds/default-bg.jpg",
    "userBio": "这家伙很懒，什么都没有留下",
    "dailyCigarettes": 20,
    "pricePerPack": 40.0,
    "quitStartDate": "2024-01-01",
    "memberLevel": "premium",
    "memberExpireDate": "2024-12-31T23:59:59",
    "customTrainingCount": 800,
    "audioPreference": "female",
    "loginType": "wechat",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "age": 30,
    "smokingYears": 10,
    "currentDailyCigarettes": 15,
    "originalDailyCigarettes": 20,
    "cigaretteBrand": "中华",
    "tarContent": 12.0,
    "gender": "male",
    "quitMode": "reduction",
    "quitDays": 30,
    "savedMoney": 240.0,
    "savedCigarettes": 150,
    "isPremiumMember": true,
    "reducedCigarettes": 5,
    "healthImprovement": 15.0,
    "quitModeText": "减量模式",
    "genderText": "男"
  }
}
```

### 3.2 更新用户信息
```
PUT /user/profile
Authorization: Bearer <token>
Content-Type: application/json

请求参数:
{
  "nickname": "新昵称",                    // 昵称（可选，1-50字符）
  "email": "newemail@example.com",        // 邮箱（可选，需符合邮箱格式）
  "dailyCigarettes": 25,                  // 每日吸烟数量（可选，大于0）
  "pricePerPack": 50.0,                   // 每包香烟价格（可选，大于0）
  "quitStartDate": "2024-01-01",          // 戒烟开始日期（可选，YYYY-MM-DD格式）
  "audioPreference": "male",              // 语音偏好（可选，male/female，最大20字符）
  "avatar": "https://example.com/new.jpg", // 头像URL（可选，最大500字符）
  "backgroundImage": "https://example.com/new.jpg", // 背景图URL（可选，最大500字符）
  "userBio": "这是我的个人简介",           // 用户简介（可选，最大500字符）
  "age": 30,                              // 年龄（可选，1-120）
  "smokingYears": 10,                     // 烟龄（可选，0-100年）
  "currentDailyCigarettes": 15,           // 当前每日吸烟量（可选，大于0）
  "cigaretteBrand": "中华",               // 香烟品牌（可选，最大50字符）
  "tarContent": 12.0,                     // 焦油含量（可选，0.1-99.9）
  "gender": "male",                       // 性别（可选，male/female/secret）
  "quitMode": "reduction"                 // 戒烟模式（可选，reduction/abstinence）
}

响应格式:
{
  "code": 200,
  "message": "用户信息更新成功",
  "data": {
    "id": "123456",
    "nickname": "新昵称",
    "phone": "13800138000",
    "email": "newemail@example.com",
    "avatar": "https://example.com/new.jpg",
    "backgroundImage": "https://example.com/new.jpg",
    "userBio": "这是我的个人简介",
    "dailyCigarettes": 25,
    "pricePerPack": 50.0,
    "quitStartDate": "2024-01-01",
    "memberLevel": "premium",
    "memberExpireDate": "2024-12-31T23:59:59",
    "customTrainingCount": 800,
    "audioPreference": "male",
    "loginType": "wechat",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T12:00:00",
    "quitDays": 30,
    "savedMoney": 300.0,
    "savedCigarettes": 150,
    "isPremiumMember": true
  }
}
```

### 3.3 背景图管理

#### 3.3.1 上传背景图文件
```
POST /background/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

请求参数:
- file: 背景图文件（必填，支持jpg/jpeg/png/gif/webp格式，最大10MB）

响应格式:
{
  "code": 200,
  "message": "背景图上传成功",
  "data": "background_123_1703123456789_abc12345.jpg"
}
```

#### 3.3.2 从URL下载背景图
```
POST /background/download
Authorization: Bearer <token>
Content-Type: application/json

请求参数:
{
  "backgroundUrl": "https://example.com/background.jpg"
}

示例:
POST /background/download
Content-Type: application/json

{
  "backgroundUrl": "https://example.com/background.jpg"
}

响应格式:
{
  "code": 200,
  "message": "背景图下载成功",
  "data": "background_123_1703123456789_abc12345.jpg"
}
```

#### 3.3.3 获取背景图文件
```
GET /background/{fileName}

路径参数:
- fileName: 背景图文件名

响应格式:
直接返回图片文件，Content-Type: image/jpeg 或 image/png 等
```

#### 3.3.4 删除背景图
```
DELETE /background/delete
Authorization: Bearer <token>

响应格式:
{
  "code": 200,
  "message": "背景图删除成功",
  "data": null
}
```

#### 3.3.5 更新用户背景图
```
POST /user/background
Authorization: Bearer <token>
Content-Type: application/json

请求参数:
{
  "backgroundImage": "/uploads/backgrounds/custom-bg.jpg"
}

示例:
POST /user/background
Content-Type: application/json

{
  "backgroundImage": "/uploads/backgrounds/custom-bg.jpg"
}

响应格式:
{
  "code": 200,
  "message": "背景图更新成功",
  "data": {
    "id": "123456",
    "nickname": "用户昵称",
    "phone": "13800138000",
    "email": "user@example.com",
    "avatar": "https://example.com/avatar.jpg",
    "backgroundImage": "/uploads/backgrounds/custom-bg.jpg",
    "userBio": "这家伙很懒，什么都没有留下",
    "dailyCigarettes": 20,
    "pricePerPack": 40.0,
    "quitStartDate": "2024-01-01",
    "memberLevel": "basic",
    "memberExpireDate": null,
    "customTrainingCount": null,
    "audioPreference": null,
    "loginType": "wechat",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T12:00:00",
    "quitDays": 30,
    "savedMoney": 240.0,
    "savedCigarettes": 150,
    "isPremiumMember": false
  }
}
```

### 3.4 用户简介管理

#### 3.4.1 更新用户简介
```
POST /user/bio
Authorization: Bearer <token>
Content-Type: application/json

请求参数:
{
  "userBio": "这是我的个人简介，记录戒烟历程"
}

示例:
POST /user/bio
Content-Type: application/json

{
  "userBio": "这是我的个人简介，记录戒烟历程"
}

响应格式:
{
  "code": 200,
  "message": "用户简介更新成功",
  "data": {
    "id": "123456",
    "nickname": "用户昵称",
    "phone": "13800138000",
    "email": "user@example.com",
    "avatar": "https://example.com/avatar.jpg",
    "backgroundImage": "/uploads/backgrounds/default-bg.jpg",
    "userBio": "这是我的个人简介，记录戒烟历程",
    "dailyCigarettes": 20,
    "pricePerPack": 40.0,
    "quitStartDate": "2024-01-01",
    "memberLevel": "basic",
    "memberExpireDate": null,
    "customTrainingCount": null,
    "audioPreference": null,
    "loginType": "wechat",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T12:00:00",
    "quitDays": 30,
    "savedMoney": 240.0,
    "savedCigarettes": 150,
    "isPremiumMember": false
  }
}
```

### 3.5 批量更新用户自定义信息
```
POST /user/profile-custom
Authorization: Bearer <token>
Content-Type: application/json

请求参数:
{
  "backgroundImage": "/uploads/backgrounds/custom-bg.jpg",
  "userBio": "这是我的个人简介"
}

示例:
POST /user/profile-custom
Content-Type: application/json

{
  "backgroundImage": "/uploads/backgrounds/custom-bg.jpg",
  "userBio": "这是我的个人简介"
}

响应格式:
{
  "code": 200,
  "message": "用户自定义信息更新成功",
  "data": {
    "id": "123456",
    "nickname": "用户昵称",
    "phone": "13800138000",
    "email": "user@example.com",
    "avatar": "https://example.com/avatar.jpg",
    "backgroundImage": "/uploads/backgrounds/custom-bg.jpg",
    "userBio": "这是我的个人简介",
    "dailyCigarettes": 20,
    "pricePerPack": 40.0,
    "quitStartDate": "2024-01-01",
    "memberLevel": "basic",
    "memberExpireDate": null,
    "customTrainingCount": null,
    "audioPreference": null,
    "loginType": "wechat",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T12:00:00",
    "quitDays": 30,
    "savedMoney": 240.0,
    "savedCigarettes": 150,
    "isPremiumMember": false
  }
}
```

### 3.6 测试接口（无需Token）

#### 3.6.1 测试更新背景图
```
GET /user/update-background?userId=123&backgroundImage=/uploads/backgrounds/test-bg.jpg

响应格式:
{
  "code": 200,
  "message": "背景图更新成功",
  "data": {
    "id": "123",
    "nickname": "用户昵称",
    "phone": "13800138000",
    "email": "user@example.com",
    "avatar": "https://example.com/avatar.jpg",
    "backgroundImage": "/uploads/backgrounds/test-bg.jpg",
    "userBio": "这家伙很懒，什么都没有留下",
    "dailyCigarettes": 20,
    "pricePerPack": 40.0,
    "quitStartDate": "2024-01-01",
    "memberLevel": "basic",
    "memberExpireDate": null,
    "customTrainingCount": null,
    "audioPreference": null,
    "loginType": "wechat",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T12:00:00",
    "quitDays": 30,
    "savedMoney": 240.0,
    "savedCigarettes": 150,
    "isPremiumMember": false
  }
}
```

#### 3.6.2 测试更新用户简介
```
GET /user/update-bio?userId=123&userBio=测试用户简介

响应格式:
{
  "code": 200,
  "message": "用户简介更新成功",
  "data": {
    "id": "123",
    "nickname": "用户昵称",
    "phone": "13800138000",
    "email": "user@example.com",
    "avatar": "https://example.com/avatar.jpg",
    "backgroundImage": "/uploads/backgrounds/default-bg.jpg",
    "userBio": "测试用户简介",
    "dailyCigarettes": 20,
    "pricePerPack": 40.0,
    "quitStartDate": "2024-01-01",
    "memberLevel": "basic",
    "memberExpireDate": null,
    "customTrainingCount": null,
    "audioPreference": null,
    "loginType": "wechat",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T12:00:00",
    "quitDays": 30,
    "savedMoney": 240.0,
    "savedCigarettes": 150,
    "isPremiumMember": false
  }
}
```

---

## 4. 记录管理模块

**⚠️ 重要说明：时间字段处理**

本模块涉及的时间字段遵循以下规则：

- **`timestamp`字段**：前端传递北京时间，后端转换为UTC时间存储，返回时转换为北京时间
- **`created_at`字段**：存储北京时间，用于统计和显示
- **统计查询**：基于`created_at`字段（北京时间）进行日期统计

---

### 4.1 记录吸烟
```
POST /records/smoking
Authorization: Bearer <token>
Content-Type: application/json

请求参数:
{
  "timestamp": "2024-01-01T12:00:00.000",  // 吸烟时间（北京时间，ISO 8601格式，不包含时区标识）
  "cigarette_count": 1,                     // 支数（可选，默认1）
  "note": "压力大"                          // 备注（可选，最大500字符）
}

响应格式:
{
  "code": 200,
  "message": "记录成功",
  "data": {
    "record_id": "rec_123456",
    "timestamp": "2024-01-01T12:00:00.000",  // 返回北京时间
    "cigarette_count": 1,
    "note": "压力大",
    "created_at": "2024-01-01T12:00:00.000"   // 返回北京时间
  }
}
```

### 4.2 记录训练
```
POST /records/training
Authorization: Bearer <token>
Content-Type: application/json

请求参数:
{
  "timestamp": "2024-01-01T12:00:00.000",  // 训练时间（北京时间，ISO 8601格式，不包含时区标识）
  "duration": 300,                          // 训练时长（秒，必填，最小1秒）
  "audio_type": "female1",                  // 音频类型（可选，最大50字符）
  "completed": true,                        // 是否完成训练（必填）
  "note": "完成戒烟训练"                     // 备注（可选，最大500字符）
}

响应格式:
{
  "code": 200,
  "message": "记录成功",
  "data": {
    "record_id": "rec_123456",
    "timestamp": "2024-01-01T12:00:00.000",  // 返回北京时间
    "duration": 300,
    "audio_type": "female1",
    "completed": true,
    "note": "完成戒烟训练",
    "created_at": "2024-01-01T12:00:00.000"   // 返回北京时间
  }
}
```

### 4.3 获取记录列表
```
GET /records?type=smoking&date=2024-01-01&limit=20&offset=0
Authorization: Bearer <token>

请求参数:
- type: 记录类型 smoking/training（默认smoking）
- date: 日期 YYYY-MM-DD（可选，获取指定日期的记录）
- limit: 限制数量（默认20，最大100）
- offset: 偏移量（默认0，用于分页）

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 100,
    "records": [
      {
        "record_id": "rec_123456",
        "timestamp": "2024-01-01T12:00:00.000",  // 返回北京时间
        "cigarette_count": 1,
        "note": "压力大",
        "created_at": "2024-01-01T12:00:00.000"   // 返回北京时间
      }
    ]
  }
}
```

### 4.4 获取今日吸烟次数
```
GET /records/smoking/today-count
Authorization: Bearer <token>

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": 5
}

错误响应:
{
  "code": 401,
  "message": "未提供认证令牌",
  "data": null
}
```

### 4.5 获取今日训练次数
```
GET /records/training/today-count
Authorization: Bearer <token>

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": 3
}

错误响应:
{
  "code": 401,
  "message": "未提供认证令牌",
  "data": null
}
```

### 4.6 获取最后一次吸烟记录
```
GET /records/smoking/last
Authorization: Bearer <token>

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "record_id": "rec_123456",
    "timestamp": "2024-01-01T12:00:00.000",  // 返回北京时间
    "cigarette_count": 1,
    "note": "压力大",
    "created_at": "2024-01-01T12:00:00.000"   // 返回北京时间
  }
}

无记录时的响应:
{
  "code": 200,
  "message": "暂无吸烟记录",
  "data": null
}

错误响应:
{
  "code": 401,
  "message": "未提供认证令牌",
  "data": null
}
```

### 4.7 获取最后一次训练记录
```
GET /records/training/last
Authorization: Bearer <token>

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "record_id": "rec_123456",
    "timestamp": "2024-01-01T12:00:00.000",  // 返回北京时间
    "duration": 300,
    "audio_type": "female1",
    "completed": true,
    "note": "完成戒烟训练",
    "created_at": "2024-01-01T12:00:00.000"   // 返回北京时间
  }
}

无记录时的响应:
{
  "code": 200,
  "message": "暂无训练记录",
  "data": null
}

错误响应:
{
  "code": 401,
  "message": "未提供认证令牌",
  "data": null
}
```

### 4.8 删除吸烟记录
```
DELETE /records/smoking/{record_id}
Authorization: Bearer <token>

路径参数:
- record_id: 记录ID（数字，不包含"rec_"前缀）

响应格式:
{
  "code": 200,
  "message": "删除成功",
  "data": null
}

错误响应:
{
  "code": 400,
  "message": "ID格式错误",
  "data": null
}
{
  "code": 401,
  "message": "未提供认证令牌",
  "data": null
}
```

### 4.9 删除训练记录
```
DELETE /records/training/{record_id}
Authorization: Bearer <token>

路径参数:
- record_id: 记录ID（数字，不包含"rec_"前缀）

响应格式:
{
  "code": 200,
  "message": "删除成功",
  "data": null
}

错误响应:
{
  "code": 400,
  "message": "ID格式错误",
  "data": null
}
{
  "code": 401,
  "message": "未提供认证令牌",
  "data": null
}
```

---

## 5. 统计数据模块（待实现）

### 5.1 获取统计数据
```
GET /api/v1/statistics?period=today
Authorization: Bearer <token>

请求参数:
- period: 统计周期 today/week/month/year/all

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "period": "today",
    "quit_days": 30,                    // 戒烟天数
    "smoking_count": 5,                 // 吸烟次数
    "training_count": 10,               // 训练次数
    "saved_money": 240.0,               // 节省金额
    "reduced_cigarettes": 150,          // 减少香烟数
    "last_smoke_minutes": 120,          // 距离上次吸烟分钟数
    "weekly_trend": [                   // 周趋势
      {"date": "2024-01-01", "smoking": 5, "training": 10},
      {"date": "2024-01-02", "smoking": 3, "training": 12}
    ]
  }
}
```

### 5.2 获取成就列表（待实现）
```
GET /api/v1/achievements
Authorization: Bearer <token>

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "achievements": [
      {
        "id": "ach_001",
        "title": "第一天",
        "description": "成功戒烟第一天",
        "icon": "https://example.com/icon/day1.png",
        "achieved": true,
        "achieved_at": "2024-01-01T00:00:00Z"
      },
      {
        "id": "ach_007",
        "title": "一周达成",
        "description": "成功戒烟一周",
        "icon": "https://example.com/icon/week1.png",
        "achieved": false,
        "achieved_at": null
      }
    ]
  }
}
```

---

## 6. 会员系统模块（待实现）

### 6.1 获取会员信息
```
GET /api/v1/membership/status
Authorization: Bearer <token>

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "is_premium": true,
    "member_level": "premium",
    "expire_date": "2024-12-31T23:59:59Z",
    "privileges": [
      "unlimited_training",
      "detailed_analytics", 
      "custom_settings",
      "premium_support",
      "monthly_content"
    ],
    "remaining_days": 90
  }
}
```

### 6.2 获取会员套餐（待实现）
```
GET /api/v1/membership/packages

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "packages": [
      {
        "id": "monthly",
        "name": "月度会员",
        "price": 19.0,
        "original_price": 19.0,
        "duration": 30,
        "description": "适合体验会员功能"
      },
      {
        "id": "yearly", 
        "name": "年度会员",
        "price": 168.0,
        "original_price": 228.0,
        "duration": 365,
        "description": "省钱首选，平均¥14/月",
        "recommended": true
      }
    ]
  }
}
```

### 6.3 创建订单（待实现）
```
POST /api/v1/membership/orders
Authorization: Bearer <token>
Content-Type: application/json

请求参数:
{
  "package_id": "yearly",           // 套餐ID
  "payment_method": "wechat"        // 支付方式
}

响应格式:
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "order_id": "ord_123456",
    "package_id": "yearly",
    "amount": 168.0,
    "payment_method": "wechat",
    "payment_info": {
      "prepay_id": "wx_prepay_123456",
      "code_url": "weixin://wxpay/bizpayurl?pr=...",
      "expires_in": 900
    },
    "created_at": "2024-01-01T12:00:00Z"
  }
}
```

### 6.4 获取订单列表（待实现）
```
GET /api/v1/membership/orders?status=paid&limit=20&offset=0
Authorization: Bearer <token>

请求参数:
- status: 订单状态 pending/paid/failed/cancelled
- limit: 限制数量
- offset: 偏移量

响应格式:
{
  "code": 200,
  "message": "获取成功", 
  "data": {
    "total": 5,
    "orders": [
      {
        "id": "ord_123456",
        "package_name": "年度会员",
        "amount": 168.0,
        "status": "paid",
        "payment_method": "wechat",
        "created_at": "2024-01-01T12:00:00Z",
        "paid_at": "2024-01-01T12:05:00Z"
      }
    ]
  }
}
```

---

## 7. 内容管理模块（部分待实现）

### 7.1 获取知识库内容（待实现）
```
GET /api/v1/content/knowledge?category=method&limit=20&offset=0

请求参数:
- category: 分类 method/benefits/myths/tips/success
- limit: 限制数量
- offset: 偏移量

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 50,
    "articles": [
      {
        "id": "art_001",
        "title": "戒烟的科学方法",
        "summary": "科学有效的戒烟方法介绍",
        "content": "详细内容...",
        "category": "method",
        "author": "专家名称",
        "read_count": 1000,
        "like_count": 50,
        "cover_image": "https://example.com/cover.jpg",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-01T00:00:00Z"
      }
    ]
  }
}
```

### 7.2 获取文章详情（待实现）
```
GET /api/v1/content/knowledge/{article_id}

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": "art_001",
    "title": "戒烟的科学方法",
    "content": "详细内容HTML...",
    "category": "method",
    "author": "专家名称",
    "read_count": 1001,
    "like_count": 50,
    "cover_image": "https://example.com/cover.jpg",
    "tags": ["戒烟", "健康", "科学"],
    "related_articles": [
      {"id": "art_002", "title": "相关文章"}
    ],
    "created_at": "2024-01-01T00:00:00Z"
  }
}
```

### 7.3 获取音频资源

#### 7.3.1 获取公有音频列表
```
GET /audios/public
Authorization: Bearer <token>（可选）

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "isPublic": true,
      "description": "专业的戒烟训练音频",
      "fileName": "female_training_001.mp3",
      "voiceType": "female",
      "userId": 5,
      "isDisabled": false,
      "createdAt": "2024-01-01T12:00:00.000Z",
      "updatedAt": "2024-01-01T12:00:00.000Z",
      "userNickname": "管理员",
      "userAvatar": "https://example.com/avatar.jpg"
    }
  ]
}
```

#### 7.3.2 根据音色类型获取公有音频
```
GET /audios/public/voice-type/{voiceType}
Authorization: Bearer <token>（可选）

路径参数:
- voiceType: 音色类型 female/male/child/custom

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "isPublic": true,
      "description": "女声戒烟训练音频",
      "fileName": "female_training_001.mp3",
      "voiceType": "female",
      "userId": 5,
      "isDisabled": false,
      "createdAt": "2024-01-01T12:00:00.000Z",
      "updatedAt": "2024-01-01T12:00:00.000Z",
      "userNickname": "管理员",
      "userAvatar": "https://example.com/avatar.jpg"
    }
  ]
}
```

#### 7.3.3 获取我的音频列表
```
GET /audios/my
Authorization: Bearer <token>（必填）

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "isPublic": false,
      "description": "我的私有音频",
      "fileName": "my_audio_001.mp3",
      "voiceType": "male",
      "userId": 3,
      "isDisabled": false,
      "createdAt": "2024-01-01T12:00:00.000Z",
      "updatedAt": "2024-01-01T12:00:00.000Z",
      "userNickname": "我的昵称",
      "userAvatar": "https://example.com/avatar.jpg"
    }
  ]
}
```

#### 7.3.4 获取音频详情
```
GET /audios/{audioId}
Authorization: Bearer <token>（可选）

路径参数:
- audioId: 音频ID（数字）

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "isPublic": true,
    "description": "专业的戒烟训练音频",
    "fileName": "female_training_001.mp3",
    "voiceType": "female",
    "userId": 5,
    "isDisabled": false,
    "createdAt": "2024-01-01T12:00:00.000Z",
    "updatedAt": "2024-01-01T12:00:00.000Z",
    "userNickname": "管理员",
    "userAvatar": "https://example.com/avatar.jpg"
  }
}
```

#### 7.3.5 创建音频
```
POST /audios
Authorization: Bearer <token>（必填）
Content-Type: application/json

请求参数:
{
  "isPublic": true,                    // 是否公有（必填）
  "description": "音频描述",            // 描述（可选，最大500字符）
  "fileName": "audio_001.mp3",         // 文件名（必填，最大255字符）
  "voiceType": "female",               // 音色类型（可选，最大50字符）
  "quitMode": "both",                  // 戒烟方式（可选）：reduction(减量), abstinence(戒断), both(两者都适合)
  "isPremiumOnly": false               // 是否仅会员可用（可选，默认false）
}

响应格式:
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "isPublic": true,
    "description": "音频描述",
    "fileName": "audio_001.mp3",
    "voiceType": "female",
    "userId": 3,
    "isDisabled": false,
    "createdAt": "2024-01-01T12:00:00.000Z",
    "updatedAt": "2024-01-01T12:00:00.000Z",
    "userNickname": "用户昵称",
    "userAvatar": "https://example.com/avatar.jpg"
  }
}
```

#### 7.3.6 更新音频
```
PUT /audios/{audioId}
Authorization: Bearer <token>（必填）
Content-Type: application/json

路径参数:
- audioId: 音频ID（数字）

请求参数:
{
  "isPublic": true,                    // 是否公有（必填）
  "description": "更新后的描述",        // 描述（可选，最大500字符）
  "fileName": "updated_audio.mp3",     // 文件名（必填，最大255字符）
  "voiceType": "male",                 // 音色类型（可选，最大50字符）
  "quitMode": "reduction",             // 戒烟方式（可选）：reduction(减量), abstinence(戒断), both(两者都适合)
  "isPremiumOnly": true                // 是否仅会员可用（可选）
}

响应格式:
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "isPublic": true,
    "description": "更新后的描述",
    "fileName": "updated_audio.mp3",
    "voiceType": "male",
    "userId": 3,
    "isDisabled": false,
    "createdAt": "2024-01-01T12:00:00.000Z",
    "updatedAt": "2024-01-01T12:05:00.000Z",
    "userNickname": "用户昵称",
    "userAvatar": "https://example.com/avatar.jpg"
  }
}
```

#### 7.3.7 删除音频
```
DELETE /audios/{audioId}
Authorization: Bearer <token>（必填）

路径参数:
- audioId: 音频ID（数字）

响应格式:
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

#### 7.3.8 启用/禁用音频
```
PUT /audios/{audioId}/toggle
Authorization: Bearer <token>（必填）

路径参数:
- audioId: 音频ID（数字）

响应格式:
{
  "code": 200,
  "message": "状态更新成功",
  "data": {
    "id": 1,
    "isPublic": true,
    "description": "音频描述",
    "fileName": "audio_001.mp3",
    "voiceType": "female",
    "userId": 3,
    "isDisabled": true,  // 状态已切换
    "createdAt": "2024-01-01T12:00:00.000Z",
    "updatedAt": "2024-01-01T12:10:00.000Z",
    "userNickname": "用户昵称",
    "userAvatar": "https://example.com/avatar.jpg"
  }
}
```

### 7.3.9 音频字段说明

#### 音频基本信息字段：
- `id`: 音频ID（数字）
- `isPublic`: 是否公有（true/false）
- `description`: 音频描述（可选，最大500字符）
- `fileName`: 音频文件名（必填，最大255字符）
- `voiceType`: 音色类型（可选，最大50字符）
- `userId`: 上传用户ID（数字）
- `isDisabled`: 是否禁用（true/false）
- `quitMode`: 戒烟方式（reduction-减量戒烟, abstinence-戒断戒烟, both-两者都适合）
- `isPremiumOnly`: 是否仅会员可用（true/false）
- `createdAt`: 创建时间（ISO 8601格式）
- `updatedAt`: 更新时间（ISO 8601格式）

#### 用户信息字段：
- `userNickname`: 上传用户昵称
- `userAvatar`: 上传用户头像URL

#### 音色类型说明：
- `female`: 女声
- `male`: 男声
- `child`: 童声
- `custom`: 自定义音色（需要在前端显示具体内容）

### 7.3.10 错误处理

#### 常见错误码：
- `401`: 未授权/Token无效
- `403`: 禁止访问/权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

#### 错误响应示例：
```json
{
  "code": 401,
  "message": "Token无效或已过期",
  "data": null
}
```

---

## 8. 音频下载模块

### 8.1 下载音频文件
```
GET /audios/download/{audioId}
Authorization: Bearer <token>（可选）

路径参数:
- audioId: 音频ID（数字）

请求头:
- Authorization: Bearer <token>（可选，用于权限验证）

响应格式:
直接返回音频文件流，Content-Type: application/octet-stream

权限说明:
1. 公有音频：无需登录，直接下载
2. 私有音频：需要验证用户身份，只能下载自己的音频
3. 会员音频：需要验证用户是否为会员

错误响应:
- 401: 未授权（需要登录但未提供Token）
- 403: 禁止访问（无权限下载此音频）
- 404: 音频不存在或文件丢失
- 500: 服务器内部错误

使用示例:

#### JavaScript下载示例：
```javascript
// 下载公有音频（无需Token）
const downloadPublicAudio = async (audioId) => {
  try {
    const response = await fetch(`/audios/download/${audioId}`);
    if (response.ok) {
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      
      // 从响应头获取文件名
      const contentDisposition = response.headers.get('Content-Disposition');
      let filename = 'audio.mp3';
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/);
        if (filenameMatch) {
          filename = filenameMatch[1];
        }
      }
      
      a.download = filename;
      a.click();
      window.URL.revokeObjectURL(url);
    } else {
      console.error('下载失败:', response.status, response.statusText);
    }
  } catch (error) {
    console.error('下载出错:', error);
  }
};

// 下载私有音频（需要Token）
const downloadPrivateAudio = async (audioId, token) => {
  try {
    const response = await fetch(`/audios/download/${audioId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (response.ok) {
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      
      // 从响应头获取文件名
      const contentDisposition = response.headers.get('Content-Disposition');
      let filename = 'audio.mp3';
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/);
        if (filenameMatch) {
          filename = filenameMatch[1];
        }
      }
      
      a.download = filename;
      a.click();
      window.URL.revokeObjectURL(url);
    } else if (response.status === 401) {
      console.error('需要登录');
    } else if (response.status === 403) {
      console.error('无权限下载此音频');
    } else {
      console.error('下载失败:', response.status, response.statusText);
    }
  } catch (error) {
    console.error('下载出错:', error);
  }
};

// 带进度条的下载
const downloadAudioWithProgress = async (audioId, token = null, onProgress = null) => {
  try {
    const headers = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const response = await fetch(`/audios/download/${audioId}`, { headers });
    
    if (response.ok) {
      const reader = response.body.getReader();
      const contentLength = +response.headers.get('Content-Length');
      
      let receivedLength = 0;
      const chunks = [];
      
      while (true) {
        const { done, value } = await reader.read();
        
        if (done) break;
        
        chunks.push(value);
        receivedLength += value.length;
        
        // 报告进度
        if (onProgress && contentLength) {
          const progress = (receivedLength / contentLength) * 100;
          onProgress(progress);
        }
      }
      
      // 合并所有块
      const blob = new Blob(chunks);
      const url = window.URL.createObjectURL(blob);
      
      // 获取文件名
      const contentDisposition = response.headers.get('Content-Disposition');
      let filename = 'audio.mp3';
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/);
        if (filenameMatch) {
          filename = filenameMatch[1];
        }
      }
      
      // 下载文件
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      a.click();
      window.URL.revokeObjectURL(url);
      
      return true;
    } else {
      console.error('下载失败:', response.status, response.statusText);
      return false;
    }
  } catch (error) {
    console.error('下载出错:', error);
    return false;
  }
};
```

#### React组件示例：
```jsx
import React, { useState } from 'react';

const AudioDownloader = ({ audioId, token, onDownloadStart, onDownloadComplete, onError }) => {
  const [downloading, setDownloading] = useState(false);
  const [progress, setProgress] = useState(0);

  const handleDownload = async () => {
    setDownloading(true);
    setProgress(0);
    
    try {
      onDownloadStart?.();
      
      const success = await downloadAudioWithProgress(
        audioId, 
        token, 
        (progress) => setProgress(progress)
      );
      
      if (success) {
        onDownloadComplete?.();
      } else {
        onError?.('下载失败');
      }
    } catch (error) {
      onError?.(error.message);
    } finally {
      setDownloading(false);
      setProgress(0);
    }
  };

  return (
    <div className="audio-downloader">
      <button 
        onClick={handleDownload} 
        disabled={downloading}
        className="download-btn"
      >
        {downloading ? '下载中...' : '下载音频'}
      </button>
      
      {downloading && (
        <div className="progress-bar">
          <div 
            className="progress-fill" 
            style={{ width: `${progress}%` }}
          />
          <span className="progress-text">{Math.round(progress)}%</span>
        </div>
      )}
    </div>
  );
};

export default AudioDownloader;
```

#### Vue组件示例：
```vue
<template>
  <div class="audio-downloader">
    <button 
      @click="handleDownload" 
      :disabled="downloading"
      class="download-btn"
    >
      {{ downloading ? '下载中...' : '下载音频' }}
    </button>
    
    <div v-if="downloading" class="progress-bar">
      <div 
        class="progress-fill" 
        :style="{ width: progress + '%' }"
      />
      <span class="progress-text">{{ Math.round(progress) }}%</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AudioDownloader',
  props: {
    audioId: {
      type: [String, Number],
      required: true
    },
    token: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      downloading: false,
      progress: 0
    };
  },
  methods: {
    async handleDownload() {
      this.downloading = true;
      this.progress = 0;
      
      try {
        this.$emit('download-start');
        
        const success = await this.downloadAudioWithProgress(
          this.audioId, 
          this.token, 
          (progress) => this.progress = progress
        );
        
        if (success) {
          this.$emit('download-complete');
        } else {
          this.$emit('error', '下载失败');
        }
      } catch (error) {
        this.$emit('error', error.message);
      } finally {
        this.downloading = false;
        this.progress = 0;
      }
    },
    
    async downloadAudioWithProgress(audioId, token = null, onProgress = null) {
      try {
        const headers = {};
        if (token) {
          headers['Authorization'] = `Bearer ${token}`;
        }
        
        const response = await fetch(`/audios/download/${audioId}`, { headers });
        
        if (response.ok) {
          const reader = response.body.getReader();
          const contentLength = +response.headers.get('Content-Length');
          
          let receivedLength = 0;
          const chunks = [];
          
          while (true) {
            const { done, value } = await reader.read();
            
            if (done) break;
            
            chunks.push(value);
            receivedLength += value.length;
            
            // 报告进度
            if (onProgress && contentLength) {
              const progress = (receivedLength / contentLength) * 100;
              onProgress(progress);
            }
          }
          
          // 合并所有块
          const blob = new Blob(chunks);
          const url = window.URL.createObjectURL(blob);
          
          // 获取文件名
          const contentDisposition = response.headers.get('Content-Disposition');
          let filename = 'audio.mp3';
          if (contentDisposition) {
            const filenameMatch = contentDisposition.match(/filename="(.+)"/);
            if (filenameMatch) {
              filename = filenameMatch[1];
            }
          }
          
          // 下载文件
          const a = document.createElement('a');
          a.href = url;
          a.download = filename;
          a.click();
          window.URL.revokeObjectURL(url);
          
          return true;
        } else {
          console.error('下载失败:', response.status, response.statusText);
          return false;
        }
      } catch (error) {
        console.error('下载出错:', error);
        return false;
      }
    }
  }
};
</script>

<style scoped>
.audio-downloader {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.download-btn {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.download-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.progress-bar {
  width: 100%;
  height: 20px;
  background: #e9ecef;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: #28a745;
  transition: width 0.3s ease;
}

.progress-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 12px;
  color: #495057;
}
</style>
```

### 8.2 下载接口特性

#### 权限控制：
- **公有音频**: 所有用户都可以下载，无需登录
- **私有音频**: 只有音频所有者可以下载，需要验证Token
- **会员音频**: 只有会员用户可以下载，需要验证会员状态

#### 文件处理：
- 支持所有常见音频格式（MP3、WAV、AAC等）
- 自动设置正确的Content-Type
- 支持中文文件名（自动编码）
- 可自定义下载文件名（优先使用音频描述）

#### 性能优化：
- 支持大文件下载
- 可选的进度条显示
- 支持断点续传（HTTP Range请求）
- 文件流式传输，内存占用低

#### 安全特性：
- 文件路径验证，防止目录遍历攻击
- 权限检查，确保用户只能下载有权限的音频
- 文件存在性验证，避免下载不存在的文件

### 8.3 使用注意事项

1. **权限验证**: 私有音频必须提供有效的Token
2. **会员检查**: 会员音频需要验证用户会员状态
3. **文件大小**: 大文件下载建议显示进度条
4. **错误处理**: 妥善处理各种HTTP状态码
5. **用户体验**: 提供下载状态反馈和错误提示
6. **浏览器兼容**: 确保在主流浏览器中正常工作

### 8.4 常见问题

**Q: 为什么下载失败返回401？**
A: 可能是Token过期或无效，需要重新登录获取新Token

**Q: 为什么下载失败返回403？**
A: 可能是权限不足，私有音频只能由所有者下载，会员音频需要会员身份

**Q: 为什么下载失败返回404？**
A: 音频不存在或音频文件已丢失，请联系管理员

**Q: 下载的文件名是乱码怎么办？**
A: 检查浏览器是否正确处理Content-Disposition头，建议使用现代浏览器

**Q: 大文件下载很慢怎么办？**
A: 可以考虑实现断点续传功能，或者使用CDN加速

---

## 9. 协议管理模块

### 9.1 获取协议类型列表
```
GET /agreements/types

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "typeCode": "USER_AGREEMENT",
      "typeName": "用户服务协议",
      "description": "用户使用本应用需要遵守的服务条款",
      "url": "/agreements/USER_AGREEMENT"
    },
    {
      "typeCode": "PRIVACY_POLICY",
      "typeName": "隐私政策",
      "description": "关于用户隐私保护的政策说明",
      "url": "/agreements/PRIVACY_POLICY"
    },
    {
      "typeCode": "MEMBER_AGREEMENT",
      "typeName": "会员协议",
      "description": "会员服务相关协议条款",
      "url": "/agreements/MEMBER_AGREEMENT"
    },
    {
      "typeCode": "TERMS_OF_SERVICE",
      "typeName": "服务条款",
      "description": "应用服务使用条款",
      "url": "/agreements/TERMS_OF_SERVICE"
    }
  ]
}
```

### 9.2 获取用户服务协议
```
GET /agreements/user-agreement

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "title": "用户服务协议",
    "version": "v1.0.0",
    "effectiveDate": "2024-01-01",
    "content": "协议内容HTML格式..."
  }
}
```

### 9.3 获取隐私政策
```
GET /agreements/privacy-policy

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "title": "隐私政策",
    "version": "v1.0.0",
    "effectiveDate": "2024-01-01",
    "content": "隐私政策内容HTML格式..."
  }
}
```

### 9.4 获取会员协议
```
GET /agreements/membership-agreement

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "title": "会员协议",
    "version": "v1.0.0",
    "effectiveDate": "2024-01-01",
    "content": "会员协议内容HTML格式..."
  }
}
```

### 9.5 获取服务条款
```
GET /agreements/terms-of-service

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "title": "服务条款",
    "version": "v1.0.0",
    "effectiveDate": "2024-01-01",
    "content": "服务条款内容HTML格式..."
  }
}
```

### 9.6 根据类型获取协议
```
GET /agreements/{typeCode}

路径参数:
- typeCode: 协议类型代码（如：USER_AGREEMENT, PRIVACY_POLICY等）

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "title": "协议标题",
    "version": "版本号",
    "effectiveDate": "生效日期",
    "content": "协议内容HTML格式..."
  }
}
```

### 9.7 获取协议摘要
```
GET /agreements/summary

响应格式:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalCount": 4,
    "lastUpdated": "2024-01-01",
    "agreements": [
      {
        "type": "用户服务协议",
        "version": "v1.0.0",
        "status": "生效中"
      },
      {
        "type": "隐私政策",
        "version": "v1.0.0",
        "status": "生效中"
      }
    ]
  }
}
```

---

## 10. 测试接口模块

### 10.1 测试账号登录

```java:src/main/java/com/jiayan/quitsmoking/controller/AuthController.java
/**
 * 测试账号登录接口（仅用于开发测试）
 */
@PostMapping("/test-login")
public ApiResponse<AuthResponse> testLogin(@RequestBody TestLoginRequest request) {
    
    try {
        log.info("测试账号登录，用户ID: {}", request.getUserId());
        
        // 查找用户
        User user = userService.findById(request.getUserId());
        if (user == null) {
            return ApiResponse.error(ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在");
        }
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString());
        
        // 构建响应
        AuthResponse response = AuthResponse.builder()
                .userId(user.getId().toString())
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(7200) // 2小时
                .user(UserProfileResponse.fromEntity(user))
                .build();
        
        log.info("测试账号登录成功，用户ID: {}, 昵称: {}", user.getId(), user.getNickname());
        return ApiResponse.success("登录成功", response);
        
    } catch (Exception e) {
        log.error("测试账号登录异常", e);
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "登录失败，请稍后重试");
    }
}

/**
 * 通过用户ID获取用户信息（测试用）
 */
@GetMapping("/test-user/{userId}")
public ApiResponse<UserProfileResponse> getTestUser(@PathVariable Long userId) {
    
    try {
        log.info("获取测试用户信息，用户ID: {}", userId);
        
        User user = userService.findById(userId);
        if (user == null) {
            return ApiResponse.error(ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在");
        }
        
        UserProfileResponse response = UserProfileResponse.fromEntity(user);
        return ApiResponse.success("获取用户信息成功", response);
        
    } catch (Exception e) {
        log.error("获取测试用户信息异常", e);
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "获取用户信息失败");
    }
}
```

### 10.2 创建测试登录请求DTO

```java:src/main/java/com/jiayan/quitsmoking/dto/TestLoginRequest.java
package com.jiayan.quitsmoking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 测试登录请求DTO
 */
@Data
public class TestLoginRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
```

### 10.3 在UserService中添加findById方法

```java:src/main/java/com/jiayan/quitsmoking/service/UserService.java
/**
 * 根据ID查找用户
 */
User findById(Long userId);
```

### 10.4 在UserServiceImpl中实现findById方法

```java:src/main/java/com/jiayan/quitsmoking/service/impl/UserServiceImpl.java
@Override
public User findById(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在"));
}
```

### 9.5 创建测试用的HTML页面

```html:test-login.html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>测试账号登录</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background: #f5f5f5;
        }
        
        .container {
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #333;
        }
        
        input[type="number"] {
            width: 100%;
            padding: 12px;
            border: 2px solid #e1e5e9;
            border-radius: 6px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        
        input[type="number"]:focus {
            outline: none;
            border-color: #007bff;
        }
        
        .btn {
            background: #007bff;
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 6px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s;
            margin-right: 10px;
        }
        
        .btn:hover {
            background: #0056b3;
        }
        
        .btn-success {
            background: #28a745;
        }
        
        .btn-success:hover {
            background: #1e7e34;
        }
        
        .btn-danger {
            background: #dc3545;
        }
        
        .btn-danger:hover {
            background: #c82333;
        }
        
        .result {
            margin-top: 20px;
            padding: 15px;
            border-radius: 6px;
            display: none;
        }
        
        .result.success {
            background: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
        }
        
        .result.error {
            background: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }
        
        .result.info {
            background: #d1ecf1;
            border: 1px solid #bee5eb;
            color: #0c5460;
        }
        
        .token-info {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 6px;
            margin-top: 15px;
            font-family: monospace;
            font-size: 14px;
            word-break: break-all;
        }
        
        .user-info {
            background: #e9ecef;
            padding: 15px;
            border-radius: 6px;
            margin-top: 15px;
        }
        
        .user-info h3 {
            margin-top: 0;
            color: #495057;
        }
        
        .user-info p {
            margin: 5px 0;
            color: #6c757d;
        }
        
        .user-info strong {
            color: #495057;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🧪 测试账号登录</h1>
        
        <div class="form-group">
            <label for="userId">用户ID:</label>
            <input type="number" id="userId" placeholder="请输入用户ID" min="1">
        </div>
        
        <div class="form-group">
            <button class="btn" onclick="testLogin()">测试登录</button>
            <button class="btn btn-success" onclick="getUserInfo()">获取用户信息</button>
            <button class="btn btn-danger" onclick="clearResult()">清除结果</button>
        </div>
        
        <div id="result" class="result"></div>
        
        <div id="tokenInfo" class="token-info" style="display: none;"></div>
        <div id="userInfo" class="user-info" style="display: none;"></div>
    </div>

    <script>
        const API_BASE = 'http://localhost:8081';
        
        // 测试登录
        async function testLogin() {
            const userId = document.getElementById('userId').value.trim();
            
            if (!userId) {
                showResult('请输入用户ID', 'error');
                return;
            }
            
            try {
                showResult('正在登录...', 'info');
                
                const response = await fetch(`${API_BASE}/auth/test-login`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ userId: parseInt(userId) })
                });
                
                const result = await response.json();
                
                if (result.code === 200) {
                    showResult('登录成功！', 'success');
                    showTokenInfo(result.data);
                    showUserInfo(result.data.user);
                    
                    // 保存token到localStorage
                    localStorage.setItem('testToken', result.data.token);
                    localStorage.setItem('testRefreshToken', result.data.refreshToken);
                    localStorage.setItem('testUserId', result.data.userId);
                    
                } else {
                    showResult(`登录失败: ${result.message}`, 'error');
                }
                
            } catch (error) {
                console.error('登录异常:', error);
                showResult(`登录异常: ${error.message}`, 'error');
            }
        }
        
        // 获取用户信息
        async function getUserInfo() {
            const userId = document.getElementById('userId').value.trim();
            
            if (!userId) {
                showResult('请输入用户ID', 'error');
                return;
            }
            
            try {
                showResult('正在获取用户信息...', 'info');
                
                const response = await fetch(`${API_BASE}/auth/test-user/${userId}`);
                const result = await response.json();
                
                if (result.code === 200) {
                    showResult('获取用户信息成功！', 'success');
                    showUserInfo(result.data);
                } else {
                    showResult(`获取用户信息失败: ${result.message}`, 'error');
                }
                
            } catch (error) {
                console.error('获取用户信息异常:', error);
                showResult(`获取用户信息异常: ${error.message}`, 'error');
            }
        }
        
        // 显示结果
        function showResult(message, type) {
            const resultDiv = document.getElementById('result');
            resultDiv.textContent = message;
            resultDiv.className = `result ${type}`;
            resultDiv.style.display = 'block';
        }
        
        // 显示Token信息
        function showTokenInfo(data) {
            const tokenInfoDiv = document.getElementById('tokenInfo');
            tokenInfoDiv.innerHTML = `
                <h3>🔑 Token信息</h3>
                <p><strong>用户ID:</strong> ${data.userId}</p>
                <p><strong>Token:</strong> ${data.token}</p>
                <p><strong>Refresh Token:</strong> ${data.refreshToken}</p>
                <p><strong>过期时间:</strong> ${data.expiresIn}秒</p>
            `;
            tokenInfoDiv.style.display = 'block';
        }
        
        // 显示用户信息
        function showUserInfo(user) {
            const userInfoDiv = document.getElementById('userInfo');
            userInfoDiv.innerHTML = `
                <h3>👤 用户信息</h3>
                <p><strong>ID:</strong> ${user.id}</p>
                <p><strong>昵称:</strong> ${user.nickname || '未设置'}</p>
                <p><strong>手机号:</strong> ${user.phone || '未设置'}</p>
                <p><strong>邮箱:</strong> ${user.email || '未设置'}</p>
                <p><strong>头像:</strong> ${user.avatar || '未设置'}</p>
                <p><strong>背景图:</strong> ${user.backgroundImage || '未设置'}</p>
                <p><strong>用户简介:</strong> ${user.userBio || '未设置'}</p>
                <p><strong>每日吸烟量:</strong> ${user.dailyCigarettes || '未设置'}</p>
                <p><strong>每包价格:</strong> ${user.pricePerPack || '未设置'}</p>
                <p><strong>戒烟开始日期:</strong> ${user.quitStartDate || '未设置'}</p>
                <p><strong>会员等级:</strong> ${user.memberLevel || '未设置'}</p>
                <p><strong>创建时间:</strong> ${user.createdAt || '未设置'}</p>
            `;
            userInfoDiv.style.display = 'block';
        }
        
        // 清除结果
        function clearResult() {
            document.getElementById('result').style.display = 'none';
            document.getElementById('tokenInfo').style.display = 'none';
            document.getElementById('userInfo').style.display = 'none';
            document.getElementById('userId').value = '';
        }
        
        // 页面加载完成后自动填充一个测试用户ID
        window.addEventListener('load', function() {
            // 从localStorage获取上次使用的用户ID
            const lastUserId = localStorage.getItem('testUserId');
            if (lastUserId) {
                document.getElementById('userId').value = lastUserId;
            } else {
                // 默认填充一个测试用户ID
                document.getElementById('userId').value = '1';
            }
        });
    </script>
</body>
</html>
```

### 9.6 更新API文档

在API文档中添加测试接口说明：

```markdown:src/api.md
---

## 9. 测试接口模块

### 9.1 测试账号登录
```
POST /auth/test-login
Content-Type: application/json

请求参数:
{
  "userId": 123456
}

响应格式:
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "123456",
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "expiresIn": 7200,
    "user": {
      "id": "123456",
      "nickname": "用户昵称",
      "phone": "13800138000",
      "email": "user@example.com",
      "avatar": "https://example.com/avatar.jpg",
      "backgroundImage": "/uploads/backgrounds/default-bg.jpg",
      "userBio": "这家伙很懒，什么都没有留下",
      "dailyCigarettes": 20,
      "pricePerPack": 40.0,
      "quitStartDate": "2024-01-01",
      "memberLevel": "basic",
      "memberExpireDate": null,
      "customTrainingCount": 800,
      "audioPreference": "female",
      "loginType": "wechat",
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  }
}
```

### 9.2 通过用户ID获取用户信息
```
GET /auth/test-user/{userId}
Authorization: Bearer <token>（可选）

路径参数:
- userId: 用户ID（数字）

响应格式:
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "id": "123456",
    "nickname": "用户昵称",
    "phone": "13800138000",
    "email": "user@example.com",
    "avatar": "https://example.com/avatar.jpg",
    "backgroundImage": "/uploads/backgrounds/default-bg.jpg",
    "userBio": "这家伙很懒，什么都没有留下",
    "dailyCigarettes": 20,
    "pricePerPack": 40.0,
    "quitStartDate": "2024-01-01",
    "memberLevel": "basic",
    "memberExpireDate": null,
    "customTrainingCount": 800,
    "audioPreference": "female",
    "loginType": "wechat",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```