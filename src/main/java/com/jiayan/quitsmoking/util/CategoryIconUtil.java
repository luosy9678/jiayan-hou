package com.jiayan.quitsmoking.util;

/**
 * 分类图标工具类
 */
public class CategoryIconUtil {
    
    /**
     * 构建图标URL
     */
    public static String buildIconUrl(String iconName) {
        if (iconName == null || iconName.trim().isEmpty()) {
            return null;
        }
        return "/api/v1/knowledge/categories/icons/" + iconName;
    }
    
    /**
     * 获取默认图标名称
     */
    public static String getDefaultIconName() {
        return "default-category.png";
    }
}