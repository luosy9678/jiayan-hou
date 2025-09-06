package com.jiayan.quitsmoking.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

/**
 * 头像服务接口
 */
public interface AvatarService {

    /**
     * 上传头像文件
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像文件名
     */
    String uploadAvatar(Long userId, MultipartFile file) throws Exception;

    /**
     * 从URL下载并保存头像
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 头像文件名
     */
    String downloadAvatar(Long userId, URL avatarUrl) throws Exception;

    /**
     * 获取头像文件
     * @param fileName 文件名
     * @return 文件资源
     */
    Resource getAvatarFile(String fileName) throws Exception;

    /**
     * 删除用户头像
     * @param userId 用户ID
     */
    void deleteAvatar(Long userId) throws Exception;

    /**
     * 生成头像文件名
     * @param userId 用户ID
     * @param originalFileName 原始文件名
     * @return 新的文件名
     */
    String generateAvatarFileName(Long userId, String originalFileName);
} 