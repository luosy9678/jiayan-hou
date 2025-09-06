package com.jiayan.quitsmoking.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

/**
 * 背景图服务接口
 */
public interface BackgroundService {

    /**
     * 上传背景图文件
     */
    String uploadBackground(Long userId, MultipartFile file) throws Exception;

    /**
     * 从URL下载并保存背景图
     */
    String downloadBackground(Long userId, URL url) throws Exception;

    /**
     * 获取背景图文件
     */
    Resource getBackgroundFile(String fileName) throws Exception;

    /**
     * 删除背景图
     */
    void deleteBackground(Long userId) throws Exception;
} 