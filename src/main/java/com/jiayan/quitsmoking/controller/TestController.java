package com.jiayan.quitsmoking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试控制器 - 提供测试页面访问
 */
@Controller
@RequestMapping("/test")
public class TestController {
    
    /**
     * 简单API测试页面
     */
    @GetMapping("/simple")
    @ResponseBody
    public String getSimpleTestPage() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>简单API测试</title>
                <meta charset="UTF-8">
            </head>
            <body>
                <h1>简单API测试</h1>
                <button onclick="testAPI()">测试文章分类管理API</button>
                <div id="result"></div>

                <script>
                    async function testAPI() {
                        const resultDiv = document.getElementById('result');
                        resultDiv.innerHTML = '正在测试...';
                        
                        try {
                            const response = await fetch('http://localhost:8081/api/v1/knowledge/categories?page=0&size=5');
                            const data = await response.json();
                            
                            resultDiv.innerHTML = `
                                <h3>测试结果</h3>
                                <p>状态码: ${response.status}</p>
                                <p>响应数据:</p>
                                <pre>${JSON.stringify(data, null, 2)}</pre>
                            `;
                        } catch (error) {
                            resultDiv.innerHTML = `
                                <h3>错误</h3>
                                <p>${error.message}</p>
                            `;
                        }
                    }
                </script>
            </body>
            </html>
            """;
    }
    
    /**
     * 文章分类管理API测试页面
     */
    @GetMapping("/category-api")
    @ResponseBody
    public String getCategoryApiTestPage() {
        return """
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>文章分类管理API测试</title>
                <style>
                    body {
                        font-family: 'Microsoft YaHei', Arial, sans-serif;
                        max-width: 1200px;
                        margin: 0 auto;
                        padding: 20px;
                        background: #f5f5f5;
                    }
                    .container {
                        background: white;
                        padding: 20px;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        margin-bottom: 20px;
                    }
                    .test-section {
                        margin-bottom: 30px;
                        padding: 20px;
                        border: 1px solid #ddd;
                        border-radius: 5px;
                    }
                    .test-result {
                        margin-top: 10px;
                        padding: 10px;
                        border-radius: 5px;
                        font-family: monospace;
                        white-space: pre-wrap;
                        max-height: 300px;
                        overflow-y: auto;
                    }
                    .success {
                        background: #d4edda;
                        border: 1px solid #c3e6cb;
                        color: #155724;
                    }
                    .error {
                        background: #f8d7da;
                        border: 1px solid #f5c6cb;
                        color: #721c24;
                    }
                    .info {
                        background: #d1ecf1;
                        border: 1px solid #bee5eb;
                        color: #0c5460;
                    }
                    button {
                        background: #007bff;
                        color: white;
                        border: none;
                        padding: 10px 20px;
                        border-radius: 5px;
                        cursor: pointer;
                        margin: 5px;
                    }
                    button:hover {
                        background: #0056b3;
                    }
                    input, select {
                        padding: 8px;
                        margin: 5px;
                        border: 1px solid #ddd;
                        border-radius: 4px;
                    }
                    .form-group {
                        margin: 10px 0;
                    }
                    label {
                        display: inline-block;
                        width: 120px;
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <h1>文章分类管理API测试</h1>
                <p>测试端口：8081 | API基础URL：http://localhost:8081/api/v1</p>
                
                <div class="container">
                    <h2>1. 获取分类列表（基础测试）</h2>
                    <div class="test-section">
                        <button onclick="testGetCategories()">测试获取分类列表</button>
                        <div id="getCategoriesResult" class="test-result"></div>
                    </div>
                </div>

                <div class="container">
                    <h2>2. 获取分类列表（带筛选参数）</h2>
                    <div class="test-section">
                        <div class="form-group">
                            <label>分类名称：</label>
                            <input type="text" id="filterName" placeholder="输入分类名称关键词">
                        </div>
                        <div class="form-group">
                            <label>状态：</label>
                            <select id="filterStatus">
                                <option value="">全部</option>
                                <option value="true">启用</option>
                                <option value="false">禁用</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>访问权限：</label>
                            <select id="filterAccessLevel">
                                <option value="">全部</option>
                                <option value="free">免费</option>
                                <option value="member">会员</option>
                                <option value="premium">高级会员</option>
                            </select>
                        </div>
                        <button onclick="testGetCategoriesWithFilters()">测试筛选分类</button>
                        <div id="filterCategoriesResult" class="test-result"></div>
                    </div>
                </div>

                <div class="container">
                    <h2>3. 获取单个分类详情</h2>
                    <div class="test-section">
                        <div class="form-group">
                            <label>分类ID：</label>
                            <input type="number" id="categoryId" value="1" min="1">
                        </div>
                        <button onclick="testGetCategoryById()">测试获取分类详情</button>
                        <div id="getCategoryResult" class="test-result"></div>
                    </div>
                </div>

                <script>
                    const API_BASE = 'http://localhost:8081/api/v1';
                    
                    // 测试获取分类列表
                    async function testGetCategories() {
                        const resultDiv = document.getElementById('getCategoriesResult');
                        resultDiv.className = 'test-result info';
                        resultDiv.textContent = '正在测试...';
                        
                        try {
                            const response = await fetch(`${API_BASE}/knowledge/categories?page=0&size=10`);
                            const data = await response.json();
                            
                            if (response.ok && data.code === 200) {
                                resultDiv.className = 'test-result success';
                                resultDiv.textContent = `✅ 成功获取分类列表\\n状态码: ${response.status}\\n数据: ${JSON.stringify(data, null, 2)}`;
                            } else {
                                resultDiv.className = 'test-result error';
                                resultDiv.textContent = `❌ 获取分类列表失败\\n状态码: ${response.status}\\n错误信息: ${JSON.stringify(data, null, 2)}`;
                            }
                        } catch (error) {
                            resultDiv.className = 'test-result error';
                            resultDiv.textContent = `❌ 请求异常: ${error.message}`;
                        }
                    }
                    
                    // 测试带筛选的分类列表
                    async function testGetCategoriesWithFilters() {
                        const resultDiv = document.getElementById('filterCategoriesResult');
                        const name = document.getElementById('filterName').value;
                        const status = document.getElementById('filterStatus').value;
                        const accessLevel = document.getElementById('filterAccessLevel').value;
                        
                        resultDiv.className = 'test-result info';
                        resultDiv.textContent = '正在测试筛选功能...';
                        
                        try {
                            let url = `${API_BASE}/knowledge/categories?page=0&size=10`;
                            if (name) url += `&name=${encodeURIComponent(name)}`;
                            if (status !== '') url += `&isActive=${status}`;
                            if (accessLevel) url += `&accessLevel=${accessLevel}`;
                            
                            const response = await fetch(url);
                            const data = await response.json();
                            
                            if (response.ok && data.code === 200) {
                                resultDiv.className = 'test-result success';
                                resultDiv.textContent = `✅ 成功获取筛选结果\\n状态码: ${response.status}\\n筛选参数: name=${name}, isActive=${status}, accessLevel=${accessLevel}\\n数据: ${JSON.stringify(data, null, 2)}`;
                            } else {
                                resultDiv.className = 'test-result error';
                                resultDiv.textContent = `❌ 筛选分类失败\\n状态码: ${response.status}\\n错误信息: ${JSON.stringify(data, null, 2)}`;
                            }
                        } catch (error) {
                            resultDiv.className = 'test-result error';
                            resultDiv.textContent = `❌ 请求异常: ${error.message}`;
                        }
                    }
                    
                    // 测试获取单个分类
                    async function testGetCategoryById() {
                        const resultDiv = document.getElementById('getCategoryResult');
                        const categoryId = document.getElementById('categoryId').value;
                        
                        resultDiv.className = 'test-result info';
                        resultDiv.textContent = '正在获取分类详情...';
                        
                        try {
                            const response = await fetch(`${API_BASE}/knowledge/categories/${categoryId}`);
                            const data = await response.json();
                            
                            if (response.ok && data.code === 200) {
                                resultDiv.className = 'test-result success';
                                resultDiv.textContent = `✅ 成功获取分类详情\\n状态码: ${response.status}\\n分类ID: ${categoryId}\\n数据: ${JSON.stringify(data, null, 2)}`;
                            } else {
                                resultDiv.className = 'test-result error';
                                resultDiv.textContent = `❌ 获取分类详情失败\\n状态码: ${response.status}\\n分类ID: ${categoryId}\\n错误信息: ${JSON.stringify(data, null, 2)}`;
                            }
                        } catch (error) {
                            resultDiv.className = 'test-result error';
                            resultDiv.textContent = `❌ 请求异常: ${error.message}`;
                        }
                    }
                    
                    // 页面加载完成后自动测试基础功能
                    window.onload = function() {
                        console.log('页面加载完成，准备测试文章分类管理API...');
                        console.log('API基础URL:', API_BASE);
                    };
                </script>
            </body>
            </html>
            """;
    }
    
    /**
     * 分类排序逻辑测试页面
     */
    @GetMapping("/category-sorting")
    @ResponseBody
    public String getCategorySortingTestPage() {
        return """
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>测试分类排序逻辑</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        max-width: 1200px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f5f5f5;
                    }
                    .container {
                        background: white;
                        padding: 20px;
                        border-radius: 8px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    .test-section {
                        margin: 20px 0;
                        padding: 15px;
                        border: 1px solid #ddd;
                        border-radius: 5px;
                    }
                    .test-button {
                        background: #007bff;
                        color: white;
                        border: none;
                        padding: 10px 20px;
                        border-radius: 5px;
                        cursor: pointer;
                        margin: 5px;
                    }
                    .test-button:hover {
                        background: #0056b3;
                    }
                    .result {
                        margin-top: 15px;
                        padding: 10px;
                        background: #f8f9fa;
                        border-radius: 5px;
                        white-space: pre-wrap;
                        font-family: monospace;
                    }
                    .category-item {
                        margin: 5px 0;
                        padding: 5px;
                        border-left: 3px solid #007bff;
                        background: #f8f9fa;
                    }
                    .parent-category {
                        font-weight: bold;
                        color: #007bff;
                    }
                    .child-category {
                        margin-left: 20px;
                        color: #666;
                    }
                    .sort-info {
                        color: #28a745;
                        font-size: 0.9em;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>测试分类排序逻辑</h1>
                    <p>测试新的分类排序逻辑：小排序值的顶级分类 + 其子类 → 大排序值的顶级分类 + 其子类</p>
                    
                    <div class="test-section">
                        <h3>测试新的排序逻辑</h3>
                        <button class="test-button" onclick="testNewSorting()">测试新排序逻辑</button>
                        <button class="test-button" onclick="testOldSorting()">测试原有排序逻辑</button>
                        <button class="test-button" onclick="clearResults()">清空结果</button>
                        <div id="sortingResult" class="result"></div>
                    </div>
                    
                    <div class="test-section">
                        <h3>排序逻辑说明</h3>
                        <p><strong>新的排序逻辑：</strong></p>
                        <ul>
                            <li>首先按顶级分类的排序值从小到大排列</li>
                            <li>每个顶级分类下，子类按排序值从小到大排列</li>
                            <li>整体结构：小排序值父类+子类 → 大排序值父类+子类</li>
                        </ul>
                        <p><strong>示例：</strong></p>
                        <ul>
                            <li>父类A (排序值: 1) + 子类A1 (排序值: 1) + 子类A2 (排序值: 2)</li>
                            <li>父类B (排序值: 5) + 子类B1 (排序值: 1) + 子类B2 (排序值: 3)</li>
                            <li>父类C (排序值: 10) + 子类C1 (排序值: 1)</li>
                        </ul>
                    </div>
                </div>

                <script>
                    const API_BASE = 'http://localhost:8081/api/v1';
                    
                    async function testNewSorting() {
                        const resultDiv = document.getElementById('sortingResult');
                        resultDiv.innerHTML = '正在测试新的排序逻辑...';
                        
                        try {
                            // 使用新的排序逻辑（无筛选条件）
                            const response = await fetch(`${API_BASE}/knowledge/categories?page=0&size=100`);
                            const data = await response.json();
                            
                            if (data.content && Array.isArray(data.content)) {
                                displaySortedCategories(data.content, '新排序逻辑');
                            } else {
                                resultDiv.innerHTML = '获取分类失败: ' + JSON.stringify(data, null, 2);
                            }
                        } catch (error) {
                            resultDiv.innerHTML = '测试失败: ' + error.message;
                        }
                    }
                    
                    async function testOldSorting() {
                        const resultDiv = document.getElementById('sortingResult');
                        resultDiv.innerHTML = '正在测试原有的排序逻辑...';
                        
                        try {
                            // 使用原有的排序逻辑（添加一个筛选条件）
                            const response = await fetch(`${API_BASE}/knowledge/categories?page=0&size=100&isActive=true`);
                            const data = await response.json();
                            
                            if (data.content && Array.isArray(data.content)) {
                                displaySortedCategories(data.content, '原有排序逻辑');
                            } else {
                                resultDiv.innerHTML = '获取分类失败: ' + JSON.stringify(data, null, 2);
                            }
                        } catch (error) {
                            resultDiv.innerHTML = '测试失败: ' + error.message;
                        }
                    }
                    
                    function displaySortedCategories(categories, title) {
                        const resultDiv = document.getElementById('sortingResult');
                        
                        // 按父分类分组
                        const groupedCategories = {};
                        const rootCategories = [];
                        
                        categories.forEach(category => {
                            if (category.parentId === null) {
                                rootCategories.push(category);
                                groupedCategories[category.id] = {
                                    parent: category,
                                    children: []
                                };
                            } else {
                                if (groupedCategories[category.parentId]) {
                                    groupedCategories[category.parentId].children.push(category);
                                }
                            }
                        });
                        
                        // 按父分类排序值排序
                        rootCategories.sort((a, b) => a.sortOrder - b.sortOrder);
                        
                        let html = `<h4>${title} - 共 ${categories.length} 个分类</h4>`;
                        html += '<div class="sort-info">排序规则：父类排序值从小到大，子类排序值从小到大</div><br>';
                        
                        rootCategories.forEach(rootCategory => {
                            const group = groupedCategories[rootCategory.id];
                            html += `<div class="category-item parent-category">`;
                            html += `📁 ${rootCategory.name} (ID: ${rootCategory.id}, 排序: ${rootCategory.sortOrder})`;
                            html += `</div>`;
                            
                            // 子类按排序值排序
                            group.children.sort((a, b) => a.sortOrder - b.sortOrder);
                            
                            group.children.forEach(child => {
                                html += `<div class="category-item child-category">`;
                                html += `  └─ ${child.name} (ID: ${child.id}, 排序: ${child.sortOrder})`;
                                html += `</div>`;
                            });
                        });
                        
                        resultDiv.innerHTML = html;
                    }
                    
                    function clearResults() {
                        document.getElementById('sortingResult').innerHTML = '';
                    }
                    
                    // 页面加载完成后显示说明
                    document.addEventListener('DOMContentLoaded', function() {
                        console.log('分类排序测试页面已加载');
                    });
                </script>
            </body>
            </html>
            """;
    }
} 