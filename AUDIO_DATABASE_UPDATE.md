# 音频数据库更新说明

## 更新概述
为音频数据库添加了两个新字段，用于更好地管理音频的适用场景和访问权限。

## 新增字段

### 1. quit_mode (戒烟方式)
- **字段类型**: VARCHAR(20)
- **默认值**: 'both'
- **允许值**: 
  - `reduction` - 减量戒烟
  - `abstinence` - 戒断戒烟  
  - `both` - 两者都适合
- **用途**: 描述音频适用的戒烟方式，帮助用户选择合适的训练音频

### 2. is_premium_only (会员限制)
- **字段类型**: BOOLEAN
- **默认值**: false
- **允许值**: true/false
- **用途**: 标记音频是否仅限会员用户使用

## 数据库变更

### 执行SQL脚本
```sql
-- 添加戒烟方式字段
ALTER TABLE audios ADD COLUMN quit_mode VARCHAR(20) NOT NULL DEFAULT 'both' COMMENT '戒烟方式：reduction(减量), abstinence(戒断), both(两者都适合)';

-- 添加会员限制字段  
ALTER TABLE audios ADD COLUMN is_premium_only BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否仅会员可用';
```

### 索引建议（可选）
```sql
-- 为查询性能优化添加索引
CREATE INDEX idx_audios_quit_mode ON audios(quit_mode);
CREATE INDEX idx_audios_premium_only ON audios(is_premium_only);
```

## 代码变更

### 1. 实体类更新
- `Audio.java` - 添加新字段和枚举类型支持
- 新增 `QuitMode.java` 枚举类

### 2. DTO更新  
- `AudioRequest.java` - 请求参数添加新字段
- `AudioResponse.java` - 响应数据添加新字段

### 3. 枚举类
- `QuitMode.java` - 定义戒烟方式枚举值

## API更新

### 创建音频
```json
{
  "isPublic": true,
  "description": "音频描述",
  "fileName": "audio_001.mp3", 
  "voiceType": "female",
  "quitMode": "both",           // 新增
  "isPremiumOnly": false        // 新增
}
```

### 更新音频
```json
{
  "isPublic": true,
  "description": "更新后的描述",
  "fileName": "updated_audio.mp3",
  "voiceType": "male",
  "quitMode": "reduction",      // 新增
  "isPremiumOnly": true         // 新增
}
```

## 前端适配建议

### 1. 戒烟方式选择
- 提供下拉选择框，包含：减量戒烟、戒断戒烟、两者都适合
- 根据用户选择的戒烟方式过滤音频列表

### 2. 会员权限控制
- 非会员用户只能看到 `isPremiumOnly: false` 的音频
- 会员用户可以看到所有音频
- 在音频列表中标明哪些音频需要会员权限

### 3. 音频筛选
- 支持按戒烟方式筛选
- 支持按会员权限筛选
- 组合筛选功能

## 注意事项

1. **向后兼容**: 现有音频数据会自动设置默认值
2. **数据迁移**: 建议在低峰期执行数据库更新
3. **前端验证**: 确保新字段的验证逻辑正确
4. **测试**: 充分测试新字段的增删改查功能

## 版本信息
- 更新日期: 2024年
- 影响范围: 音频管理模块
- 兼容性: 向后兼容 