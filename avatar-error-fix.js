// 头像错误修复工具
(function() {
    'use strict';
    
    console.log('头像错误修复工具已加载');
    
    // 拦截所有图片加载错误
    const originalCreateElement = document.createElement;
    document.createElement = function(tagName) {
        const element = originalCreateElement.call(document, tagName);
        
        if (tagName.toLowerCase() === 'img') {
            // 监听图片加载错误
            element.addEventListener('error', function(e) {
                if (this.src && this.src.includes('default-avatar.png')) {
                    console.log('拦截到默认头像404错误:', this.src);
                    e.preventDefault();
                    e.stopPropagation();
                    
                    // 替换为占位符
                    this.style.display = 'none';
                    
                    // 创建占位符
                    const placeholder = document.createElement('div');
                    placeholder.className = 'avatar-placeholder';
                    placeholder.style.cssText = `
                        width: ${this.width || 40}px;
                        height: ${this.height || 40}px;
                        border-radius: 50%;
                        background-color: #E5E7EB;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: ${Math.max(12, (this.width || 40) * 0.5)}px;
                        color: #9CA3AF;
                        border: 1px solid #ccc;
                    `;
                    placeholder.textContent = '👤';
                    
                    // 插入占位符
                    if (this.parentNode) {
                        this.parentNode.insertBefore(placeholder, this.nextSibling);
                    }
                    
                    return false;
                }
            });
        }
        
        return element;
    };
    
    // 修复现有的图片元素
    function fixExistingImages() {
        const images = document.querySelectorAll('img');
        images.forEach(img => {
            if (img.src && img.src.includes('default-avatar.png')) {
                console.log('修复现有图片:', img.src);
                img.style.display = 'none';
                
                // 创建占位符
                const placeholder = document.createElement('div');
                placeholder.className = 'avatar-placeholder';
                placeholder.style.cssText = `
                    width: ${img.width || 40}px;
                    height: ${img.height || 40}px;
                    border-radius: 50%;
                    background-color: #E5E7EB;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: ${Math.max(12, (img.width || 40) * 0.5)}px;
                    color: #9CA3AF;
                    border: 1px solid #ccc;
                `;
                placeholder.textContent = '👤';
                
                // 插入占位符
                if (img.parentNode) {
                    img.parentNode.insertBefore(placeholder, img.nextSibling);
                }
            }
        });
    }
    
    // 监听DOM变化
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.type === 'childList') {
                mutation.addedNodes.forEach(function(node) {
                    if (node.nodeType === 1) { // Element node
                        if (node.tagName === 'IMG') {
                            if (node.src && node.src.includes('default-avatar.png')) {
                                console.log('修复动态添加的图片:', node.src);
                                node.style.display = 'none';
                                
                                const placeholder = document.createElement('div');
                                placeholder.className = 'avatar-placeholder';
                                placeholder.style.cssText = `
                                    width: ${node.width || 40}px;
                                    height: ${node.height || 40}px;
                                    border-radius: 50%;
                                    background-color: #E5E7EB;
                                    display: flex;
                                    align-items: center;
                                    justify-content: center;
                                    font-size: ${Math.max(12, (node.width || 40) * 0.5)}px;
                                    color: #9CA3AF;
                                    border: 1px solid #ccc;
                                `;
                                placeholder.textContent = '👤';
                                
                                if (node.parentNode) {
                                    node.parentNode.insertBefore(placeholder, node.nextSibling);
                                }
                            }
                        } else {
                            // 检查子元素
                            const images = node.querySelectorAll ? node.querySelectorAll('img') : [];
                            images.forEach(img => {
                                if (img.src && img.src.includes('default-avatar.png')) {
                                    console.log('修复子元素中的图片:', img.src);
                                    img.style.display = 'none';
                                    
                                    const placeholder = document.createElement('div');
                                    placeholder.className = 'avatar-placeholder';
                                    placeholder.style.cssText = `
                                        width: ${img.width || 40}px;
                                        height: ${img.height || 40}px;
                                        border-radius: 50%;
                                        background-color: #E5E7EB;
                                        display: flex;
                                        align-items: center;
                                        justify-content: center;
                                        font-size: ${Math.max(12, (img.width || 40) * 0.5)}px;
                                        color: #9CA3AF;
                                        border: 1px solid #ccc;
                                    `;
                                    placeholder.textContent = '👤';
                                    
                                    if (img.parentNode) {
                                        img.parentNode.insertBefore(placeholder, img.nextSibling);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    });
    
    // 开始观察
    if (document.body) {
        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    } else {
        // 如果document.body还不存在，等待DOM加载完成
        document.addEventListener('DOMContentLoaded', function() {
            if (document.body) {
                observer.observe(document.body, {
                    childList: true,
                    subtree: true
                });
            }
        });
    }
    
    // 页面加载完成后修复现有图片
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', fixExistingImages);
    } else {
        fixExistingImages();
    }
    
    console.log('头像错误修复工具初始化完成');
})(); 