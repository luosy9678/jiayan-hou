# 开发注意事项文档

## 🚨 重要：Spring Boot路径配置陷阱

### 问题描述
在开发协议API接口时，我们遇到了一个**路径配置错误**的问题，导致所有接口返回404或500错误，检查花费了很长时间。

### 问题原因分析

#### 1. 配置文件设置
```properties
# application.properties
server.servlet.context-path=/api/v1
```

#### 2. 错误的Controller配置
```java
@RestController
@RequestMapping("/api/v1/agreements")  // ❌ 错误：重复包含 /api/v1
public class AgreementController {
    @GetMapping("/types")
    public ApiResponse<List<Map<String, Object>>> getAgreementTypes() {
        // ...
    }
}
```

#### 3. 实际访问路径
- 期望路径：`/api/v1/agreements/types`
- 实际路径：`/api/v1/api/v1/agreements/types` （路径重复！）

### 正确的配置方式

#### 1. 配置文件
```properties
# application.properties
server.servlet.context-path=/api/v1
```

#### 2. Controller配置
```java
@RestController
@RequestMapping("/agreements")  // ✅ 正确：不要包含 /api/v1
public class AgreementController {
    @GetMapping("/types")
    public ApiResponse<List<Map<String, Object>>> getAgreementTypes() {
        // ...
    }
}
```

#### 3. 实际访问路径
- 正确路径：`/api/v1/agreements/types`
- 路径组成：`context-path` + `@RequestMapping` + `@GetMapping`

### 路径配置公式

```
实际访问路径 = server.servlet.context-path + @RequestMapping路径 + @GetMapping路径
```

**示例：**
- `context-path` = `/api/v1`
- `@RequestMapping` = `/agreements`
- `@GetMapping` = `/types`
- 实际路径 = `/api/v1/agreements/types`

### 常见错误模式

#### ❌ 错误模式1：Controller重复包含context-path
```java
@RequestMapping("/api/v1/agreements")  // 错误：重复了 /api/v1
```

#### ❌ 错误模式2：测试时使用错误的基础URL
```javascript
// 错误
const API_BASE = 'http://localhost:8081';

// 正确
const API_BASE = 'http://localhost:8081/api/v1';
```

#### ❌ 错误模式3：前端API调用路径错误
```javascript
// 错误：如果baseURL已经包含 /api/v1
url: '/api/v1/agreements/types'

// 正确：使用相对路径
url: '/agreements/types'
```

### 调试步骤

当遇到404错误时，按以下步骤检查：

1. **检查配置文件**
   - 确认 `server.servlet.context-path` 设置
   - 确认 `server.port` 设置

2. **检查Controller注解**
   - 确认 `@RequestMapping` 路径
   - 确认 `@GetMapping` 路径

3. **计算实际路径**
   - 使用公式：`context-path + @RequestMapping + @GetMapping`
   - 验证路径是否正确

4. **测试接口**
   - 使用正确的完整URL测试
   - 检查返回的错误信息

### 最佳实践

1. **命名规范**
   - Controller的 `@RequestMapping` 使用简洁的路径
   - 避免在Controller中重复context-path

2. **测试策略**
   - 先测试简单的GET接口
   - 使用Postman或curl测试
   - 检查应用启动日志

3. **文档维护**
   - 在API文档中明确标注完整路径
   - 提供路径配置示例
   - 记录常见问题和解决方案

### 相关文件

- `application.properties` - 应用配置文件
- `AgreementController.java` - 协议控制器
- `test-agreement-apis.html` - 接口测试页面
- `agreement.js` - 前端API调用文件

### 总结

**核心原则：**
- `context-path` 是全局前缀，Controller中不要重复
- 实际路径 = 配置文件路径 + Controller路径 + 方法路径
- 测试时使用完整的正确路径

**避免方法：**
- 仔细阅读配置文件
- 理解Spring Boot的路径映射机制
- 建立标准的调试流程
- 记录和分享经验教训

---

*本文档记录了开发过程中的重要经验教训，请团队成员仔细阅读，避免重复犯错。* 