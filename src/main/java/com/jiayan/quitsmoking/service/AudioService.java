package com.jiayan.quitsmoking.service;

import com.jiayan.quitsmoking.dto.AudioRequest;
import com.jiayan.quitsmoking.dto.AudioResponse;
import com.jiayan.quitsmoking.entity.Audio;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * 音频服务接口
 */
public interface AudioService {
    
    /**
     * 创建音频
     */
    AudioResponse createAudio(Long userId, AudioRequest request);
    
    /**
     * 更新音频
     */
    AudioResponse updateAudio(Long userId, Long audioId, AudioRequest request);
    
    /**
     * 获取音频详情
     */
    AudioResponse getAudio(Long audioId);
    
    /**
     * 获取用户的音频列表
     */
    List<AudioResponse> getUserAudios(Long userId);
    
    /**
     * 获取公有音频列表
     */
    List<AudioResponse> getPublicAudios();
    
    /**
     * 根据音色获取公有音频
     */
    List<AudioResponse> getPublicAudiosByVoiceType(String voiceType);
    
    /**
     * 删除音频
     */
    void deleteAudio(Long userId, Long audioId);
    
    /**
     * 启用/禁用音频
     */
    AudioResponse toggleAudioStatus(Long userId, Long audioId);
    
    /**
     * 检查音频是否存在且属于用户
     */
    boolean isAudioOwnedByUser(Long audioId, Long userId);
    
    /**
     * 下载音频文件
     */
    Resource downloadAudio(Long audioId, Long userId);
    
    /**
     * 检查用户是否有权限下载音频
     */
    boolean canUserDownloadAudio(Long audioId, Long userId);
} 