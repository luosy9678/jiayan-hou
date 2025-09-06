# 协议API接口文档

## ⚠️ 重要：路径配置说明

### 问题总结
在开发过程中，我们遇到了一个**路径配置错误**的问题，导致所有协议接口返回404或500错误，检查花费了很长时间。

**问题原因：**
1. 配置文件设置了 `server.servlet.context-path=/api/v1`
2. Controller使用了 `@RequestMapping("/api/v1/agreements")`
3. 导致实际访问路径变成了 `/api/v1/api/v1/agreements`（路径重复）

**解决方案：**
1. Controller路径改为 `@RequestMapping("/agreements")`
2. 实际访问路径变为 `/api/v1/agreements`（正确）

**经验教训：**
- 当配置文件设置了 `context-path` 时，Controller的 `@RequestMapping` 不要重复包含该路径
- 实际访问路径 = `context-path` + `@RequestMapping` 路径
- 测试时要注意使用正确的完整路径

### 正确的路径配置示例
```java
// 配置文件 application.properties
server.servlet.context-path=/api/v1

// Controller配置
@RestController
@RequestMapping("/agreements")  // ✅ 正确：不要包含 /api/v1
public class AgreementController {
    // 方法路径
    @GetMapping("/types")        // 实际路径：/api/v1/agreements/types
    @GetMapping("/user-agreement") // 实际路径：/api/v1/agreements/user-agreement
}
```

---

## 概述

本文档描述了知行戒烟APP的协议相关API接口，包括用户服务协议、隐私政策、会员协议和服务条款等。

## 接口列表

### 1. 获取协议类型列表

**接口地址：** `GET /api/v1/agreements/types`

**功能描述：** 获取所有可用的协议类型列表

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "typeCode": "user-agreement",
      "typeName": "用户服务协议",
      "description": "用户使用本应用的基本服务协议",
      "url": "/api/v1/agreements/user-agreement"
    },
    {
      "typeCode": "privacy-policy",
      "typeName": "隐私政策",
      "description": "关于用户隐私保护的政策说明",
      "url": "/api/v1/agreements/privacy-policy"
    },
    {
      "typeCode": "membership-agreement",
      "typeName": "会员协议",
      "description": "会员用户的服务协议和权益说明",
      "url": "/api/v1/agreements/membership-agreement"
    },
    {
      "typeCode": "terms-of-service",
      "typeName": "服务条款",
      "description": "应用服务的使用条款和规则",
      "url": "/api/v1/agreements/terms-of-service"
    }
  ]
}
```

### 2. 获取用户服务协议

**接口地址：** `GET /api/v1/agreements/user-agreement`

**功能描述：** 获取用户服务协议的详细内容

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "title": "用户服务协议",
    "version": "v1.0.0",
    "effectiveDate": "2024-01-01",
    "content": "用户服务协议\n\n欢迎使用知行戒烟APP！\n\n第一条 服务内容\n本应用为用户提供戒烟相关的健康管理服务..."
  }
}
```

### 3. 获取隐私政策

**接口地址：** `GET /api/v1/agreements/privacy-policy`

**功能描述：** 获取隐私政策的详细内容

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "title": "隐私政策",
    "version": "v1.0.0",
    "effectiveDate": "2024-01-01",
    "content": "隐私政策\n\n我们非常重视您的隐私保护..."
  }
}
```

### 4. 获取会员协议

**接口地址：** `GET /api/v1/agreements/membership-agreement`

**功能描述：** 获取会员协议的详细内容

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "title": "会员服务协议",
    "version": "v1.0.0",
    "effectiveDate": "2024-01-01",
    "content": "会员服务协议\n\n欢迎成为知行戒烟APP的会员用户！..."
  }
}
```

### 5. 获取服务条款

**接口地址：** `GET /api/v1/agreements/terms-of-service`

**功能描述：** 获取服务条款的详细内容

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "title": "服务条款",
    "version": "v1.0.0",
    "effectiveDate": "2024-01-01",
    "content": "服务条款\n\n本条款规定了知行戒烟APP的服务规则..."
  }
}
```

### 6. 根据类型获取协议

**接口地址：** `GET /api/v1/agreements/{typeCode}`