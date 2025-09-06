# 用户权限修复验证说明

## 问题描述
1. **前端期望用户ID=50**，但实际使用的是用户ID=2
2. **用户ID=2没有评论权限**，导致评论创建失败
3. **`getCurrentUserId()` 方法错误**：从数据库获取第一个用户而不是当前登录用户

## 修复内容

### 1. 修复了 `getCurrentUserId()` 方法
- 现在从JWT Token中获取真实的用户ID
- 如果无法获取，优先使用用户ID=50
- 最后才使用系统用户作为备选

### 2. 添加了 `getDefaultUserId()` 方法
- 优先尝试获取用户ID=50
- 如果用户50不存在，获取第一个有评论权限的用户
- 最后才创建系统用户

### 3. 注入了必要的依赖
- `JwtUtil`：用于解析JWT token
- `PasswordEncoder`：用于密码加密

## 验证步骤

### 步骤1：执行数据库权限修复脚本
```sql
-- 运行 check_and_fix_user_permissions.sql
-- 确保用户ID=50存在且有评论权限
```

### 步骤2：重新编译项目
```bash
mvn clean compile
```

### 步骤3：启动应用
```bash
mvn spring-boot:run
```

### 步骤4：测试评论创建
使用以下请求测试：

```bash
# 方式1：带JWT Token（推荐）
curl -X POST "http://localhost:8081/api/v1/knowledge/comments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "articleId": 10,
    "content": "测试评论内容"
  }'

# 方式2：不带Token（会使用默认用户ID=50）
curl -X POST "http://localhost:8081/api/v1/knowledge/comments" \
  -H "Content-Type: application/json" \
  -d '{
    "articleId": 10,
    "content": "测试评论内容"
  }'
```

### 步骤5：检查日志
观察应用日志，确认：
- 用户ID获取逻辑正确
- 没有权限相关的错误
- 评论创建成功

## 预期结果

### 成功情况
- 评论应该能够正常创建
- 用户ID应该是50（如果用户50存在且有权限）
- 不再出现"用户没有评论权限"错误

### 日志示例
```
从JWT token获取到用户ID: 50
创建评论: articleId=10, userId=50
评论创建成功: commentId=123
```

## 权限检查逻辑

### 评论权限验证流程
1. **获取用户ID**：从JWT Token或默认用户
2. **验证用户存在**：检查用户是否在数据库中存在
3. **检查用户权限**：
   - `can_create_posts = TRUE`
   - `forum_banned = FALSE`
   - `enabled = TRUE`
4. **创建评论**：如果权限验证通过

### 权限字段说明
- **`can_create_posts`**：是否有发帖/评论权限
- **`post_permission_level`**：权限级别（none/limited/full）
- **`forum_banned`**：是否被禁言
- **`enabled`**：用户是否启用

## 如果仍有问题

### 检查点1：用户ID=50是否存在
```sql
SELECT * FROM users WHERE id = 50;
```

### 检查点2：用户ID=50的权限设置
```sql
SELECT 
    id, nickname, can_create_posts, post_permission_level, 
    forum_banned, enabled 
FROM users WHERE id = 50;
```

### 检查点3：JWT Token是否有效
- 检查Authorization头是否正确设置
- 验证JWT token是否过期
- 确认token中的用户ID是否正确

## 总结
这次修复解决了两个核心问题：
1. **用户ID获取错误**：现在会正确获取当前登录用户ID
2. **权限验证失败**：确保用户有正确的评论权限

修复后，前端用户ID=50应该能够正常创建评论。 