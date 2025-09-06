# 评论功能修复验证说明

## 问题描述
之前出现的 `Column 'password' cannot be null` 错误是由于在 `KnowledgeCommentController.createSystemUser()` 方法中创建系统用户时没有设置 `password` 字段导致的。

## 修复内容
1. 在 `createSystemUser()` 方法中添加了 `password` 字段设置
2. 添加了 `phone` 字段设置（确保唯一性）
3. 使用 `PasswordEncoder` 来生成安全的密码哈希

## 修复后的代码
```java
private Long createSystemUser() {
    User user = new User();
    user.setNickname("系统用户");
    user.setPhone("system_" + System.currentTimeMillis()); // 设置唯一手机号
    user.setPassword(passwordEncoder.encode("system_default_password")); // 使用密码编码器生成安全密码
    user.setEnabled(true);
    user.setCanCreatePosts(true);
    user.setMemberLevel("free");
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    User savedUser = userRepository.save(user);
    log.info("创建系统用户: id={}", savedUser.getId());
    return savedUser.getId();
}
```

## 验证步骤

### 1. 重新编译项目
```bash
mvn clean compile
```

### 2. 启动应用
```bash
mvn spring-boot:run
```

### 3. 测试评论创建
使用以下请求测试评论创建功能：

```bash
curl -X POST "http://localhost:8081/api/v1/knowledge/comments" \
  -H "Content-Type: application/json" \
  -d '{
    "articleId": 1,
    "content": "测试评论内容"
  }'
```

### 4. 检查日志
观察应用日志，确认：
- 没有 `Column 'password' cannot be null` 错误
- 评论创建成功
- 系统用户创建成功（如果需要）

## 预期结果
- 评论应该能够正常创建
- 不再出现密码字段相关的数据库错误
- 系统用户能够正常创建（如果需要）

## 注意事项
1. 这个修复解决了密码字段为空的问题
2. 系统用户现在会有一个安全的默认密码
3. 每次创建的系统用户都有唯一的手机号
4. 密码使用BCrypt加密，符合安全标准

## 如果仍有问题
如果修复后仍有问题，请检查：
1. 数据库连接是否正常
2. 用户表结构是否正确
3. 是否有其他地方的代码在创建用户时没有设置密码 