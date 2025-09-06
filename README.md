# 戒烟助手移动端应用

## 项目简介

戒烟助手是一款帮助用户科学戒烟的移动端应用，基于Vue 3 + Vant UI开发。

## 技术栈

- **前端框架**: Vue 3
- **UI组件库**: Vant 4
- **路由管理**: Vue Router 4
- **构建工具**: Vite
- **后端API**: Spring Boot (端口8080)

## 项目结构

```
src/
├── components/          # 公共组件
├── views/              # 页面组件
│   ├── PersonalInfo.vue    # 个人信息页面
│   └── Settings.vue        # 设置页面
├── utils/              # 工具类
│   └── avatar-utils.js     # 头像工具类
├── router/             # 路由配置
├── main.js             # 应用入口
└── App.vue             # 根组件
```

## 安装和运行

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

应用将在 http://localhost:3000 启动

### 3. 构建生产版本

```bash
npm run build
```

## 功能特性

### 头像管理
- ✅ 头像上传（支持文件选择）
- ✅ 头像下载（支持URL下载）
- ✅ 头像删除
- ✅ 自动URL构建
- ✅ 错误处理和回退

### 用户信息管理
- ✅ 个人信息编辑
- ✅ 实时保存
- ✅ 表单验证

### 设置功能
- ✅ 通知设置
- ✅ 声音设置
- ✅ 震动设置
- ✅ 自动保存设置
- ✅ 缓存管理
- ✅ 退出登录

## 已解决的问题

### 1. Vant组件引入问题
- ✅ 创建了 `vant-components.js` 配置文件
- ✅ 在 `main.js` 中全局注册组件
- ✅ 解决了 `van-switch` 和 `van-image` 组件无法解析的问题

### 2. 头像URL构建重复问题
- ✅ 创建了 `avatar-utils.js` 工具类
- ✅ 修复了URL路径重复拼接的问题
- ✅ 支持多种头像格式处理

## API接口

### 头像相关接口
- `POST /api/v1/avatar/upload` - 上传头像
- `POST /api/v1/avatar/download` - 从URL下载头像
- `DELETE /api/v1/avatar/delete` - 删除头像
- `GET /api/v1/avatar/{fileName}` - 获取头像文件

### 用户相关接口
- `GET /api/v1/user/profile` - 获取用户信息
- `PUT /api/v1/user/profile` - 更新用户信息

## 开发注意事项

### 1. 环境要求
- Node.js >= 16
- npm >= 8
- 后端服务运行在8080端口

### 2. 开发规范
- 使用Vue 3 Composition API
- 遵循Vant组件使用规范
- 统一使用ES6+语法

### 3. 调试技巧
- 使用Vue DevTools调试组件
- 查看浏览器控制台日志
- 使用Network面板检查API请求

## 常见问题

### Q: Vant组件无法正常显示？
A: 确保在 `main.js` 中正确注册了组件，并检查 `vant-components.js` 配置。

### Q: 头像显示异常？
A: 检查 `avatar-utils.js` 中的URL构建逻辑，确保后端服务正常运行。

### Q: API请求失败？
A: 确认后端服务在8080端口运行，检查网络连接和CORS配置。

## 更新日志

### v1.0.0 (2024-01-01)
- ✅ 初始版本发布
- ✅ 完成头像管理功能
- ✅ 完成用户信息管理
- ✅ 完成设置功能
- ✅ 修复Vant组件引入问题
- ✅ 修复头像URL构建问题

### v1.1.0 (2024-01-XX) - 论坛系统开发
- ✅ 完成数据库设计和创建
- ✅ 完成后端实体类开发
  - ✅ 扩展User实体类（论坛权限、禁言功能）
  - ✅ 创建KnowledgeCategory实体类（知识分类）
  - ✅ 创建KnowledgeArticle实体类（知识文章）
  - ✅ 创建KnowledgeContentBlock实体类（内容块）
  - ✅ 创建KnowledgeComment实体类（评论）
  - ✅ 创建CommentImage实体类（评论图片）
  - ✅ 创建KnowledgeRating实体类（用户评分）
- ✅ 创建相关枚举类
  - ✅ PostPermissionLevel（发帖权限级别）
  - ✅ AccessLevel（访问权限级别）
  - ✅ ArticleStatus（文章状态）
  - ✅ AuditStatus（审核状态）
  - ✅ BlockType（内容块类型）
  - ✅ CommentStatus（评论状态）
- ✅ 完成数据访问层开发
  - ✅ KnowledgeCategoryRepository（知识分类数据访问）
  - ✅ KnowledgeArticleRepository（知识文章数据访问）
  - ✅ KnowledgeCommentRepository（评论数据访问）
  - ✅ KnowledgeRatingRepository（用户评分数据访问）
  - ✅ KnowledgeContentBlockRepository（内容块数据访问）
  - ✅ CommentImageRepository（评论图片数据访问）
- ✅ 完成业务逻辑层开发
  - ✅ KnowledgeCategoryService（知识分类业务逻辑）
  - ✅ KnowledgeArticleService（知识文章业务逻辑）
  - ✅ KnowledgeCommentService（评论系统业务逻辑）
  - ✅ KnowledgeRatingService（用户评分业务逻辑）
  - ✅ ForumPermissionService（论坛权限管理业务逻辑）
- ✅ 完成控制器层开发
  - ✅ KnowledgeCategoryController（知识分类REST API）
  - ✅ KnowledgeArticleController（知识文章REST API）
  - ✅ KnowledgeCommentController（评论系统REST API）
  - ✅ KnowledgeRatingController（用户评分REST API）
- ✅ 完成DTO层开发
  - ✅ 请求DTO：CreateCategoryRequest、UpdateCategoryRequest、CreateArticleRequest、CreateCommentRequest、CreateRatingRequest
  - ✅ 响应DTO：CategoryResponse、ArticleResponse、CommentResponse、RatingResponse、PageResponse
- ✅ 完成异常处理完善
  - ✅ ForumException（论坛相关异常）
  - ✅ SwaggerConfig（API文档配置）

## 开发进度

### 第一阶段：核心功能开发 ✅ 已完成
1. **数据库设计与创建** ✅
   - 所有核心表结构（共20个表）
   - 所有外键约束
   - 所有索引优化
   - 基础数据初始化

2. **后端实体类开发** ✅
   - 用户权限扩展
   - 知识库核心实体
   - 论坛评论实体
   - 评分系统实体
   - 相关枚举类定义

3. **数据访问层开发** ✅
   - 知识分类Repository
   - 知识文章Repository
   - 评论系统Repository
   - 评分系统Repository
   - 内容块Repository
   - 评论图片Repository

4. **业务逻辑层开发** ✅
   - 知识分类管理服务 ✅
   - 知识文章管理服务 ✅
   - 评论系统管理服务 ✅
   - 用户评分管理服务 ✅
   - 论坛权限管理服务 ✅

5. **控制器层开发** ✅
   - 知识分类REST API
   - 知识文章REST API
   - 评论系统REST API
   - 用户评分REST API

6. **数据传输层开发** ✅
   - 完整的请求/响应DTO
   - 数据验证注解
   - 分页响应支持

7. **异常处理完善** ✅
   - 论坛专用异常类
   - API文档配置

### 第二阶段：业务逻辑开发 ✅ 已完成
1. **控制器层开发** ✅

### 第三阶段：前端开发 ⏳ 待开始
1. **页面框架开发** - 待开始
2. **核心功能组件** - 待开始
3. **权限控制界面** - 待开始

## 控制器层特性

### REST API接口功能
- **完整的CRUD操作** - 支持增删改查
- **分页查询支持** - 标准化的分页参数
- **权限控制集成** - 使用Spring Security注解
- **数据验证支持** - 请求参数自动验证
- **统一响应格式** - 标准化的HTTP响应

### 主要API接口
1. **知识分类管理**
   - 分类的增删改查
   - 分类树结构查询
   - 分类权限控制
   - 分类排序管理

2. **知识文章管理** ✅
   - 文章的完整生命周期管理（创建、编辑、审核、发布、禁用、恢复、删除）
   - 内容块管理（支持文本和图片混合内容）
   - 审核流程管理（待审核、审核通过、审核拒绝、待发布、已发布）
   - 权限控制和访问验证（用户权限、会员等级、内容访问控制）
   - 文章状态管理（草稿、待审核、已发布、已拒绝、已归档、已禁用）
   - 统计功能（浏览次数、点赞数、评分、文章数量统计）
   - 批量操作支持（批量更新状态、批量审核）

3. **评论系统管理** ✅
   - 多级评论结构（支持无限级回复、评论树构建）
   - 评论状态管理（正常、隐藏、删除、封禁状态）
   - 图片评论支持（多图片、排序、描述）
   - 评论权限控制（用户权限、编辑权限、删除权限）
   - 评论互动功能（点赞、标记有用、统计）
   - 评论管理功能（隐藏、恢复、软删除、批量操作）

4. **评分系统管理** ✅
   - 用户评分管理（创建、更新、删除、查询）
   - 评分统计和分析（平均评分、评分分布、评分数量）
   - 评分分布计算（1-5分统计、高低分分析）
   - 热门内容推荐（高评分文章、最新评分、评分趋势）
   - 评分验证（1-5分范围验证、重复评分检查）
   - 文章统计更新（自动更新文章评分统计）

### API接口规范
- **URL设计** - RESTful风格，语义化命名
- **HTTP方法** - 正确使用GET、POST、PUT、DELETE
- **状态码** - 标准HTTP状态码
- **响应格式** - 统一的JSON响应格式
- **错误处理** - 完善的异常处理机制

## 数据访问层特性

### Repository接口功能
- **基础CRUD操作** - 继承JpaRepository和JpaSpecificationExecutor
- **自定义查询方法** - 支持复杂查询和分页
- **批量操作支持** - 支持批量更新和删除
- **性能优化查询** - 包含索引优化的查询方法
- **业务逻辑查询** - 支持权限控制、状态管理等业务需求

### 主要查询功能
1. **知识分类管理**
   - 多级分类查询
   - 权限控制查询
   - 分类排序管理

2. **知识文章管理**
   - 分页查询支持
   - 状态过滤查询
   - 权限控制查询
   - 搜索和排序

3. **评论系统管理**
   - 多级回复查询
   - 状态管理查询
   - 批量操作支持

4. **评分系统管理**
   - 评分统计查询
   - 评分分布分析
   - 用户评分查询

5. **内容块管理**
   - 顺序管理查询
   - 类型过滤查询
   - 批量操作支持

## 业务逻辑层特性

### Service接口功能
- **完整的业务逻辑封装** - 包含所有核心业务操作
- **事务管理支持** - 使用@Transactional注解
- **权限验证逻辑** - 集成用户权限检查
- **数据验证和业务规则** - 包含完整的业务验证逻辑
- **异常处理机制** - 统一的业务异常处理

### 主要业务功能
1. **知识分类管理**
   - 分类的增删改查
   - 多级分类树结构管理
   - 分类权限控制
   - 分类排序和移动

2. **知识文章管理** ✅
   - 文章的完整生命周期管理（创建、编辑、审核、发布、禁用、恢复、删除）
   - 内容块管理（支持文本和图片混合内容）
   - 审核流程管理（待审核、审核通过、审核拒绝、待发布、已发布）
   - 权限控制和访问验证（用户权限、会员等级、内容访问控制）
   - 文章状态管理（草稿、待审核、已发布、已拒绝、已归档、已禁用）
   - 统计功能（浏览次数、点赞数、评分、文章数量统计）
   - 批量操作支持（批量更新状态、批量审核）

3. **评论系统管理** ✅
   - 多级评论结构（支持无限级回复、评论树构建）
   - 评论状态管理（正常、隐藏、删除、封禁状态）
   - 图片评论支持（多图片、排序、描述）
   - 评论权限控制（用户权限、编辑权限、删除权限）
   - 评论互动功能（点赞、标记有用、统计）
   - 评论管理功能（隐藏、恢复、软删除、批量操作）

4. **评分系统管理** ✅
   - 用户评分管理（创建、更新、删除、查询）
   - 评分统计和分析（平均评分、评分分布、评分数量）
   - 评分分布计算（1-5分统计、高低分分析）
   - 热门内容推荐（高评分文章、最新评分、评分趋势）
   - 评分验证（1-5分范围验证、重复评分检查）
   - 文章统计更新（自动更新文章评分统计）

5. **权限管理系统** ✅
   - 用户发帖权限管理（授予、撤销、更新、过期检查）
   - 用户禁言管理（禁言、解除禁言、警告、统计）
   - 内容访问权限控制（会员等级、内容级别验证）
   - 角色权限管理（角色分配、权限验证、操作权限检查）
   - 权限生命周期管理（权限过期、自动清理、批量操作）
   - 综合权限信息（完整权限信息、权限历史、角色列表）

## 联系方式

如有问题，请联系开发团队。 


#——————————————以下是论坛数据库的设计说明文档
好的，我来创建一个专门的论坛数据库设计文档。

Ran tool
Read file: README.md
现在我来创建一个专门的论坛数据库设计文档：

# 论坛数据库设计文档

## 文档信息
- **文档版本**: v1.0.0
- **创建日期**: 2024-01-XX
- **项目名称**: 戒烟助手论坛系统
- **数据库类型**: MySQL 8.0+
- **状态**: ✅ 所有数据库表、约束、索引、基础数据已创建完成

## 1. 概述

本文档详细描述了论坛系统的数据库设计，包括知识库、论坛、私信、群聊和系统消息等核心功能模块的数据库表结构设计。

**✅ 数据库创建状态**: 所有表结构、约束、索引和基础数据已全部创建完成，可以直接使用。

## 2. 数据库表结构

### 2.1 知识库模块 ✅ 已创建

#### 2.1.1 知识分类表 (knowledge_categories) ✅
```sql
CREATE TABLE knowledge_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    parent_id BIGINT COMMENT '父分类ID，支持多级分类',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    access_level ENUM('free', 'member', 'premium') DEFAULT 'free' COMMENT '访问权限：free-免费，member-会员，premium-高级会员',
    member_only BOOLEAN DEFAULT FALSE COMMENT '是否仅会员可访问',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_parent (parent_id),
    INDEX idx_access (access_level, is_active),
    INDEX idx_sort (sort_order)
);
```

#### 2.1.2 知识文章表 (knowledge_articles) ✅
```sql
CREATE TABLE knowledge_articles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '正文内容',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    source VARCHAR(500) COMMENT '出处/来源',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    dislike_count INT DEFAULT 0 COMMENT '点踩数',
    rating_score DECIMAL(3,2) DEFAULT 0 COMMENT '平均评分',
    rating_count INT DEFAULT 0 COMMENT '评分人数',
    status ENUM('draft', 'pending', 'published', 'rejected', 'archived', 'banned') DEFAULT 'draft' COMMENT '文章状态',
    audit_status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '审核状态',
    audit_comment TEXT COMMENT '审核意见',
    audited_by BIGINT COMMENT '审核员ID',
    audited_at TIMESTAMP COMMENT '审核时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否已删除（软删除标记）',
    deleted_by BIGINT COMMENT '删除操作者ID',
    deleted_at TIMESTAMP COMMENT '删除时间',
    banned_reason VARCHAR(500) COMMENT '禁用原因',
    banned_by BIGINT COMMENT '禁用操作者ID',
    banned_at TIMESTAMP COMMENT '禁用时间',
    publish_time TIMESTAMP COMMENT '发布时间',
    last_edit_time TIMESTAMP COMMENT '最后编辑时间',
    last_edit_by BIGINT COMMENT '最后编辑者ID',
    edit_count INT DEFAULT 0 COMMENT '编辑次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_status (status, is_deleted),
    INDEX idx_category_status (category_id, status, is_deleted),
    INDEX idx_author_status (author_id, status, is_deleted),
    INDEX idx_audit_status (audit_status),
    INDEX idx_publish_time (publish_time),
    INDEX idx_created_at (created_at),
    INDEX idx_updated_at (updated_at),
    INDEX idx_view_count (view_count),
    INDEX idx_rating_score (rating_score),
    INDEX idx_admin_ops (audited_by, banned_by, deleted_by)
);
```

#### 2.1.3 知识内容块表 (knowledge_content_blocks) ✅
```sql
CREATE TABLE knowledge_content_blocks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT NOT NULL COMMENT '文章ID',
    block_type ENUM('text', 'image') NOT NULL COMMENT '内容类型',
    content_order INT NOT NULL COMMENT '内容顺序',
    text_content TEXT COMMENT '文本内容',
    image_url VARCHAR(500) COMMENT '图片URL',
    image_alt VARCHAR(200) COMMENT '图片描述',
    image_width INT COMMENT '图片宽度',
    image_height INT COMMENT '图片高度',
    
    INDEX idx_article_order (article_id, content_order)
);
```

#### 2.1.4 用户评分表 (knowledge_ratings) ✅
```sql
CREATE TABLE knowledge_ratings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT NOT NULL COMMENT '文章ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5) COMMENT '评分1-5',
    comment TEXT COMMENT '评价内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_user_article (user_id, article_id),
    INDEX idx_article_rating (article_id, rating),
    INDEX idx_user_rating (user_id, created_at)
);
```

### 2.2 论坛模块 ✅ 已创建

#### 2.2.1 评论表 (knowledge_comments) ✅
```sql
CREATE TABLE knowledge_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT NOT NULL COMMENT '文章ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    parent_id BIGINT COMMENT '父评论ID，支持多级回复',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    is_helpful BOOLEAN DEFAULT FALSE COMMENT '是否有用',
    status ENUM('active', 'hidden', 'deleted', 'banned') DEFAULT 'active' COMMENT '评论状态',
    hidden_reason VARCHAR(200) COMMENT '隐藏/封禁原因',
    hidden_by BIGINT COMMENT '操作管理员ID',
    hidden_at TIMESTAMP COMMENT '隐藏/封禁时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否已删除（软删除标记）',
    deleted_by BIGINT COMMENT '删除操作者ID',
    deleted_at TIMESTAMP COMMENT '删除时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_status (status, is_deleted),
    INDEX idx_article_status (article_id, status, is_deleted),
    INDEX idx_user_status (user_id, status, is_deleted),
    INDEX idx_parent (parent_id),
    INDEX idx_created_at (created_at)
);
```

#### 2.2.2 评论图片表 (comment_images) ✅
```sql
CREATE TABLE comment_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    image_alt VARCHAR(200) COMMENT '图片描述',
    image_width INT COMMENT '图片宽度',
    image_height INT COMMENT '图片高度',
    sort_order INT DEFAULT 0 COMMENT '图片顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_comment_order (comment_id, sort_order)
);
```

#### 2.2.3 举报表 (reports) ✅
```sql
CREATE TABLE reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id BIGINT NOT NULL COMMENT '举报者ID',
    target_type ENUM('post', 'comment', 'user') NOT NULL COMMENT '举报目标类型',
    target_id BIGINT NOT NULL COMMENT '举报目标ID',
    report_reason ENUM('spam', 'inappropriate', 'harassment', 'copyright', 'other') NOT NULL COMMENT '举报原因',
    report_detail TEXT COMMENT '举报详细说明',
    evidence_urls TEXT COMMENT '证据链接（JSON格式）',
    status ENUM('pending', 'reviewing', 'resolved', 'rejected') DEFAULT 'pending' COMMENT '举报状态',
    admin_id BIGINT COMMENT '处理管理员ID',
    admin_comment TEXT COMMENT '管理员处理意见',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP COMMENT '处理时间',
    
    INDEX idx_target (target_type, target_id),
    INDEX idx_status (status),
    INDEX idx_reporter (reporter_id),
    INDEX idx_admin (admin_id)
);
```

#### 2.2.4 内容封禁表 (content_bans) ✅
```sql
CREATE TABLE content_bans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    target_type ENUM('post', 'comment') NOT NULL COMMENT '封禁目标类型',
    target_id BIGINT NOT NULL COMMENT '封禁目标ID',
    ban_type ENUM('temporary', 'permanent') NOT NULL COMMENT '封禁类型',
    reason VARCHAR(500) NOT NULL COMMENT '封禁原因',
    admin_id BIGINT NOT NULL COMMENT '执行封禁的管理员ID',
    start_time TIMESTAMP NOT NULL COMMENT '封禁开始时间',
    end_time TIMESTAMP COMMENT '封禁结束时间（永久封禁为空）',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否生效中',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_target (target_type, target_id),
    INDEX idx_admin (admin_id),
    INDEX idx_ban_time (start_time, end_time),
    INDEX idx_active (is_active)
);
```

### 2.3 用户权限模块 ✅ 已创建

#### 2.3.1 用户权限扩展 (users表新增字段) ✅
```sql
-- 在现有users表中添加以下字段
ALTER TABLE users ADD COLUMN can_create_posts BOOLEAN DEFAULT FALSE COMMENT '是否可以发帖';
ALTER TABLE users ADD COLUMN post_permission_level ENUM('none', 'limited', 'full') DEFAULT 'none' COMMENT '发帖权限级别';
ALTER TABLE users ADD COLUMN post_permission_granted_by BIGINT COMMENT '发帖权限授予者ID';
ALTER TABLE users ADD COLUMN post_permission_granted_at TIMESTAMP COMMENT '发帖权限授予时间';
ALTER TABLE users ADD COLUMN post_permission_expires_at TIMESTAMP COMMENT '发帖权限过期时间';

-- 用户禁言相关字段
ALTER TABLE users ADD COLUMN forum_banned BOOLEAN DEFAULT FALSE COMMENT '是否被禁言';
ALTER TABLE users ADD COLUMN ban_reason VARCHAR(200) COMMENT '禁言原因';
ALTER TABLE users ADD COLUMN ban_start_time TIMESTAMP COMMENT '禁言开始时间';
ALTER TABLE users ADD COLUMN ban_end_time TIMESTAMP COMMENT '禁言结束时间';
ALTER TABLE users ADD COLUMN ban_count INT DEFAULT 0 COMMENT '累计禁言次数';
ALTER TABLE users ADD COLUMN last_ban_time TIMESTAMP COMMENT '最后一次禁言时间';
ALTER TABLE users ADD COLUMN warning_count INT DEFAULT 0 COMMENT '警告次数';
```

#### 2.3.2 用户禁言记录表 (user_bans) ✅
```sql
CREATE TABLE user_bans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '被禁言用户ID',
    admin_id BIGINT NOT NULL COMMENT '执行禁言的管理员ID',
    ban_type ENUM('temporary', 'permanent', 'warning') DEFAULT 'temporary' COMMENT '禁言类型',
    reason VARCHAR(500) NOT NULL COMMENT '禁言原因',
    start_time TIMESTAMP NOT NULL COMMENT '禁言开始时间',
    end_time TIMESTAMP COMMENT '禁言结束时间（永久禁言为空）',
    affected_areas TEXT COMMENT '受影响区域（如：comment,post,all）',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否生效中',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_ban (user_id, is_active),
    INDEX idx_ban_time (start_time, end_time),
    INDEX idx_admin (admin_id)
);
```

### 2.4 私信模块 ✅ 已创建

#### 2.4.1 私信会话表 (private_messages_conversations) ✅
```sql
CREATE TABLE private_messages_conversations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user1_id BIGINT NOT NULL COMMENT '用户1 ID',
    user2_id BIGINT NOT NULL COMMENT '用户2 ID',
    last_message_id BIGINT COMMENT '最后一条消息ID',
    last_message_time TIMESTAMP COMMENT '最后消息时间',
    unread_count1 INT DEFAULT 0 COMMENT '用户1未读消息数',
    unread_count2 INT DEFAULT 0 COMMENT '用户2未读消息数',
    is_blocked1 BOOLEAN DEFAULT FALSE COMMENT '用户1是否屏蔽用户2',
    is_blocked2 BOOLEAN DEFAULT FALSE COMMENT '用户2是否屏蔽用户1',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user1 (user1_id, updated_at),
    INDEX idx_user2 (user2_id, updated_at),
    INDEX idx_conversation (user1_id, user2_id)
);
```

#### 2.4.2 私信消息表 (private_messages) ✅
```sql
CREATE TABLE private_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id BIGINT NOT NULL COMMENT '会话ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    message_type ENUM('text', 'image', 'file') DEFAULT 'text' COMMENT '消息类型',
    content TEXT COMMENT '文本内容',
    image_url VARCHAR(500) COMMENT '图片URL',
    file_url VARCHAR(500) COMMENT '文件URL',
    file_name VARCHAR(200) COMMENT '文件名',
    file_size BIGINT COMMENT '文件大小(字节)',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_conversation (conversation_id, created_at),
    INDEX idx_sender (sender_id, created_at),
    INDEX idx_receiver (receiver_id, created_at),
    INDEX idx_read (is_read)
);
```

#### 2.4.3 私信黑名单表 (private_message_blacklist) ✅
```sql
CREATE TABLE private_message_blacklist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    blocked_user_id BIGINT NOT NULL COMMENT '被屏蔽用户ID',
    reason VARCHAR(200) COMMENT '屏蔽原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_block (user_id, blocked_user_id),
    INDEX idx_user (user_id),
    INDEX idx_blocked (blocked_user_id)
);
```

### 2.5 群聊模块 ✅ 已创建

#### 2.5.1 群组表 (message_groups) ✅
```sql
CREATE TABLE message_groups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '群组名称',
    description TEXT COMMENT '群组描述',
    avatar VARCHAR(500) COMMENT '群组头像',
    creator_id BIGINT NOT NULL COMMENT '创建者ID',
    group_type ENUM('private', 'public', 'system') DEFAULT 'private' COMMENT '群组类型',
    max_members INT DEFAULT 200 COMMENT '最大成员数',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_creator (creator_id),
    INDEX idx_type (group_type, is_active),
    INDEX idx_created_at (created_at)
);
```

#### 2.5.2 群组成员表 (group_members) ✅
```sql
CREATE TABLE group_members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id BIGINT NOT NULL COMMENT '群组ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role ENUM('owner', 'admin', 'member') DEFAULT 'member' COMMENT '成员角色',
    nickname VARCHAR(100) COMMENT '群内昵称',
    join_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    last_read_time TIMESTAMP COMMENT '最后阅读时间',
    unread_count INT DEFAULT 0 COMMENT '未读消息数',
    is_muted BOOLEAN DEFAULT FALSE COMMENT '是否免打扰',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否活跃',
    
    UNIQUE KEY unique_group_user (group_id, user_id),
    INDEX idx_group (group_id, is_active),
    INDEX idx_user (user_id, is_active),
    INDEX idx_role (group_id, role)
);
```

#### 2.5.3 群组消息表 (group_messages) ✅
```sql
CREATE TABLE group_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id BIGINT NOT NULL COMMENT '群组ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    message_type ENUM('text', 'image', 'file', 'system') DEFAULT 'text' COMMENT '消息类型',
    content TEXT COMMENT '文本内容',
    image_url VARCHAR(500) COMMENT '图片URL',
    file_url VARCHAR(500) COMMENT '文件URL',
    file_name VARCHAR(200) COMMENT '文件名',
    file_size BIGINT COMMENT '文件大小',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_group_time (group_id, created_at),
    INDEX idx_sender (sender_id, created_at),
    INDEX idx_deleted (is_deleted)
);
```

### 2.6 系统消息模块 ✅ 已创建

#### 2.6.1 系统消息表 (system_messages) ✅
```sql
CREATE TABLE system_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT NOT NULL COMMENT '消息内容',
    message_type ENUM('notification', 'announcement', 'warning', 'update') DEFAULT 'notification' COMMENT '消息类型',
    target_type ENUM('all', 'role', 'category', 'specific') DEFAULT 'all' COMMENT '目标类型',
    target_value TEXT COMMENT '目标值（角色、分类、用户ID列表等）',
    priority ENUM('low', 'normal', 'high', 'urgent') DEFAULT 'normal' COMMENT '优先级',
    is_read_required BOOLEAN DEFAULT FALSE COMMENT '是否必须阅读',
    expires_at TIMESTAMP COMMENT '过期时间',
    created_by BIGINT COMMENT '创建者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_type (message_type, created_at),
    INDEX idx_target (target_type),
    INDEX idx_priority (priority),
    INDEX idx_expires (expires_at),
    INDEX idx_created_by (created_by)
);
```

#### 2.6.2 用户系统消息表 (user_system_messages) ✅
```sql
CREATE TABLE user_system_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    system_message_id BIGINT NOT NULL COMMENT '系统消息ID',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    read_time TIMESTAMP COMMENT '阅读时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否已删除',
    
    UNIQUE KEY unique_user_message (user_id, system_message_id),
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_message (system_message_id)
);
```

### 2.7 举报原因表 ✅ 已创建

#### 2.7.1 举报原因表 (report_reasons) ✅
```sql
CREATE TABLE report_reasons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reason_code VARCHAR(50) NOT NULL COMMENT '原因代码',
    reason_name VARCHAR(100) NOT NULL COMMENT '原因名称',
    description TEXT COMMENT '原因描述',
    severity ENUM('low', 'medium', 'high', 'critical') DEFAULT 'medium' COMMENT '严重程度',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    
    UNIQUE KEY unique_reason_code (reason_code)
);
```

## 3. 外键约束 ✅ 已创建

```sql
-- 知识文章外键
ALTER TABLE knowledge_articles 
ADD CONSTRAINT fk_articles_category 
FOREIGN KEY (category_id) REFERENCES knowledge_categories(id) ON DELETE RESTRICT;

ALTER TABLE knowledge_articles 
ADD CONSTRAINT fk_articles_author 
FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE RESTRICT;

-- 评论外键
ALTER TABLE knowledge_comments 
ADD CONSTRAINT fk_comments_article 
FOREIGN KEY (article_id) REFERENCES knowledge_articles(id) ON DELETE CASCADE;

ALTER TABLE knowledge_comments 
ADD CONSTRAINT fk_comments_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT;

-- 评分外键
ALTER TABLE knowledge_ratings 
ADD CONSTRAINT fk_ratings_article 
FOREIGN KEY (article_id) REFERENCES knowledge_articles(id) ON DELETE CASCADE;

ALTER TABLE knowledge_ratings 
ADD CONSTRAINT fk_ratings_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT;

-- 私信外键
ALTER TABLE private_messages 
ADD CONSTRAINT fk_messages_conversation 
FOREIGN KEY (conversation_id) REFERENCES private_messages_conversations(id) ON DELETE CASCADE;

-- 群组外键
ALTER TABLE group_members 
ADD CONSTRAINT fk_members_group 
FOREIGN KEY (group_id) REFERENCES message_groups(id) ON DELETE CASCADE;

ALTER TABLE group_members 
ADD CONSTRAINT fk_members_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 群组消息外键
ALTER TABLE group_messages 
ADD CONSTRAINT fk_group_messages_group 
FOREIGN KEY (group_id) REFERENCES message_groups(id) ON DELETE CASCADE;

-- 系统消息外键
ALTER TABLE user_system_messages 
ADD CONSTRAINT fk_user_messages_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE user_system_messages 
ADD CONSTRAINT fk_user_messages_system 
FOREIGN KEY (system_message_id) REFERENCES system_messages(id) ON DELETE CASCADE;
```

## 4. 索引优化建议 ✅ 已创建

### 4.1 查询优化索引
```sql
-- 文章查询优化
CREATE INDEX idx_articles_search ON knowledge_articles(title, status, created_at);
CREATE INDEX idx_articles_popular ON knowledge_articles(view_count, rating_score, created_at);

-- 评论查询优化
CREATE INDEX idx_comments_thread ON knowledge_comments(article_id, parent_id, created_at);

-- 私信查询优化
CREATE INDEX idx_messages_unread ON private_messages(receiver_id, is_read, created_at);

-- 群组查询优化
CREATE INDEX idx_group_messages_recent ON group_messages(group_id, created_at DESC);
```

### 4.2 复合索引
```sql
-- 用户权限复合索引
CREATE INDEX idx_user_permissions ON users(can_create_posts, post_permission_level, forum_banned);

-- 内容状态复合索引
CREATE INDEX idx_content_status ON knowledge_articles(status, audit_status, is_deleted);
```

## 5. 数据初始化 ✅ 已完成

### 5.1 基础数据插入
```sql
-- 插入默认分类
INSERT INTO knowledge_categories (name, description, access_level) VALUES 
('戒烟知识', '戒烟相关的专业知识', 'free'),
('健康生活', '健康生活方式指导', 'free'),
('会员专区', '高级会员专属内容', 'premium');

-- 插入举报原因
INSERT INTO report_reasons (reason_code, reason_name, description, severity) VALUES 
('spam', '垃圾信息', '发布垃圾广告或无关内容', 'medium'),
('inappropriate', '不当内容', '发布不当或违规内容', 'high'),
('harassment', '骚扰行为', '对他人进行骚扰或攻击', 'high'),
('copyright', '版权侵犯', '侵犯他人知识产权', 'critical'),
('other', '其他', '其他违规行为', 'low');
```

## 6. 性能优化建议

### 6.1 分区策略
- 对于消息表（private_messages, group_messages）建议按时间分区
- 对于日志表建议按月分区

### 6.2 缓存策略
- 热门文章缓存
- 用户权限缓存
- 分类树缓存

### 6.3 读写分离
- 查询操作使用从库
- 写操作使用主库

## 7. 安全考虑

### 7.1 数据加密
- 敏感字段加密存储
- 消息内容可选择性加密

### 7.2 访问控制
- 严格的权限验证
- 操作日志记录

### 7.3 数据备份
- 定期数据备份
- 增量备份策略

## 8. 维护建议

### 8.1 定期维护
- 清理过期数据
- 优化表结构
- 更新统计信息

### 8.2 监控指标
- 表大小监控
- 查询性能监控
- 连接数监控

## 9. 数据库创建完成状态 ✅

### 9.1 已完成的数据库对象
- ✅ 所有核心表结构（共20个表）
- ✅ 所有外键约束
- ✅ 所有索引优化
- ✅ 基础数据初始化
- ✅ 举报原因数据

### 9.2 可以直接使用的功能
- 知识库分类管理
- 文章发布和管理
- 评论系统
- 用户权限控制
- 举报和封禁系统
- 私信功能
- 群聊功能
- 系统消息

### 9.3 下一步开发建议
1. 开始后端实体类开发
2. 实现业务逻辑服务
3. 开发REST API接口
4. 前端页面和组件开发

---

**文档版本**: v1.0.0  
**最后更新**: 2024-01-XX  
**维护人员**: 开发团队  
**数据库状态**: ✅ 完全就绪，可开始应用开发