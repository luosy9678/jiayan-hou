-- 修复控制器脚本记录
-- 执行时间：2025-08-27
-- 目的：记录需要修复的控制器问题

-- 问题1: KnowledgeArticleController
-- - 缺少 JwtUtil 导入和依赖注入
-- - 仍然调用 resolveAuthorId() 方法
-- - 仍然有 createSystemUser() 方法
-- - 需要添加 getCurrentUserId() 方法

-- 问题2: KnowledgeCommentController  
-- - 仍然有 getDefaultUserId() 方法
-- - 仍然有 createSystemUser() 方法
-- - 仍然注入 PasswordEncoder
-- - getCurrentUserId() 方法仍有降级逻辑

-- 问题3: KnowledgeRatingController
-- - 已经有正确的 getCurrentUserId() 方法
-- - 但仍然有 createSystemUser() 方法，需要移除

-- 修复步骤：
-- 1. 修改 KnowledgeArticleController
-- 2. 修改 KnowledgeCommentController  
-- 3. 修改 KnowledgeRatingController
-- 4. 重新编译测试 