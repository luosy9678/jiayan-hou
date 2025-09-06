# 控制器修复指南

## 问题概述

经过检查，发现三个控制器存在用户逻辑问题，需要按照重构计划进行修复：

### 1. KnowledgeArticleController
- ❌ 缺少 JwtUtil 导入和依赖注入
- ❌ 仍然调用 `resolveAuthorId()` 方法
- ❌ 仍然有 `createSystemUser()` 方法
- ❌ 需要添加 `getCurrentUserId()` 方法

### 2. KnowledgeCommentController
- ❌ 仍然有 `getDefaultUserId()` 方法
- ❌ 仍然有 `createSystemUser()` 方法
- ❌ 仍然注入 `PasswordEncoder`
- ❌ `getCurrentUserId()` 方法仍有降级逻辑

### 3. KnowledgeRatingController
- ✅ 已经有正确的 `getCurrentUserId()` 方法
- ❌ 但仍然有 `createSystemUser()` 方法，需要移除

## 修复步骤

### 步骤1：修复 KnowledgeArticleController

#### 1.1 添加导入语句
在文件顶部添加以下导入：
```java
import com.jiayan.quitsmoking.util.JwtUtil;
import com.jiayan.quitsmoking.exception.BusinessException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
```

#### 1.2 添加 JwtUtil 依赖
在构造函数中添加：
```java
private final JwtUtil jwtUtil;
```

#### 1.3 修改文章创建方法
将：
```java
Long authorId = resolveAuthorId();
```
改为：
```java
Long authorId = getCurrentUserId();
```

#### 1.4 添加 getCurrentUserId() 方法
在类末尾添加：
```java
/**
 * 获取当前登录用户ID
 * 从JWT Token中获取用户ID，这是前端用户操作的标准方式
 * 注意：此方法不创建系统用户，只获取真实用户ID
 */
private Long getCurrentUserId() {
    try {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException("无法获取请求上下文");
        }
        
        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException("Authorization头缺失或格式错误");
        }
        
        // 从JWT token中提取用户ID
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException("JWT token无效");
        }
        
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            throw new BusinessException("无法从JWT token中提取用户ID");
        }
        
        Long userId = Long.valueOf(userIdStr);
        log.info("从JWT token获取到用户ID: {}", userId);
        return userId;
        
    } catch (BusinessException e) {
        // 重新抛出业务异常
        throw e;
    } catch (Exception e) {
        log.error("获取用户ID时发生异常: {}", e.getMessage(), e);
        throw new BusinessException("用户认证失败");
    }
}
```

#### 1.5 移除旧方法
删除以下方法：
- `resolveAuthorId()`
- `createSystemUser()`

### 步骤2：修复 KnowledgeCommentController

#### 2.1 添加导入语句
在文件顶部添加：
```java
import com.jiayan.quitsmoking.exception.BusinessException;
```

#### 2.2 移除 PasswordEncoder 依赖
从构造函数中移除：
```java
// 删除这一行
private final PasswordEncoder passwordEncoder;
```

#### 2.3 重构 getCurrentUserId() 方法
将现有的 `getCurrentUserId()` 方法替换为：
```java
/**
 * 获取当前登录用户ID
 * 从JWT Token中获取用户ID，这是前端用户评论的标准方式
 * 注意：此方法不创建系统用户，只获取真实用户ID
 */
private Long getCurrentUserId() {
    try {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException("无法获取请求上下文");
        }
        
        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException("Authorization头缺失或格式错误");
        }
        
        // 从JWT token中提取用户ID
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException("JWT token无效");
        }
        
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            throw new BusinessException("无法从JWT token中提取用户ID");
        }
        
        Long userId = Long.valueOf(userIdStr);
        log.info("从JWT token获取到用户ID: {}", userId);
        return userId;
        
    } catch (BusinessException e) {
        // 重新抛出业务异常
        throw e;
    } catch (Exception e) {
        log.error("获取用户ID时发生异常: {}", e.getMessage(), e);
        throw new BusinessException("用户认证失败");
    }
}
```

#### 2.4 移除旧方法
删除以下方法：
- `getDefaultUserId()`
- `createSystemUser()`

### 步骤3：修复 KnowledgeRatingController

#### 3.1 移除 createSystemUser() 方法
删除 `createSystemUser()` 方法，因为已经有正确的 `getCurrentUserId()` 方法。

## 修复后的效果

### ✅ 前端用户接口（严格认证）
- **文章创建**：必须提供有效JWT Token
- **评论创建**：必须提供有效JWT Token
- **点赞操作**：必须提供有效JWT Token
- **评分操作**：必须提供有效JWT Token

### ✅ 管理员功能（保持现有）
- **AdminController**：继续支持系统用户创建
- **admin.html**：继续正常工作

## 编译和测试

### 1. 应用所有修复
按照上述步骤修改三个控制器文件

### 2. 重新编译
```bash
mvn clean compile
```

### 3. 测试验证
- 测试前端评论功能
- 测试文章点赞功能
- 测试评分功能

## 注意事项

1. **向后兼容**：AdminController 功能保持不变
2. **错误处理**：前端接口不再降级到默认用户
3. **认证要求**：所有前端操作必须提供有效JWT Token
4. **日志记录**：详细的用户ID获取日志，便于调试

## 预期结果

修复完成后，所有前端接口将：
- 严格使用JWT Token中的用户ID
- 不创建系统用户
- 不降级到默认用户
- 提供明确的错误信息 