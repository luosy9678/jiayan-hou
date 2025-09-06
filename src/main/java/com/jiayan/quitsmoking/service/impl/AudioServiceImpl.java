package com.jiayan.quitsmoking.service.impl;

import com.jiayan.quitsmoking.dto.AudioRequest;
import com.jiayan.quitsmoking.dto.AudioResponse;
import com.jiayan.quitsmoking.entity.Audio;
import com.jiayan.quitsmoking.enums.ErrorCode;
import com.jiayan.quitsmoking.enums.QuitMode;
import com.jiayan.quitsmoking.exception.BusinessException;
import com.jiayan.quitsmoking.repository.AudioRepository;
import com.jiayan.quitsmoking.repository.UserRepository;
import com.jiayan.quitsmoking.service.AudioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 音频服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AudioServiceImpl implements AudioService {
    
    private final AudioRepository audioRepository;
    private final UserRepository userRepository;
    
    // 音频文件存储路径
    private static final String AUDIO_UPLOAD_DIR = "uploads/audios/";
    
    @Override
    @Transactional
    public AudioResponse createAudio(Long userId, AudioRequest request) {
        log.info("创建音频，用户ID: {}, 文件名: {}", userId != null ? userId : "NULL(系统音频)", request.getFileName());
        log.info("=== AudioRequest参数验证 ===");
        log.info("isPublic: {}", request.getIsPublic());
        log.info("description: {}", request.getDescription());
        log.info("fileName: {}", request.getFileName());
        log.info("voiceType: {}", request.getVoiceType());
        log.info("quitMode: {}", request.getQuitMode());
        log.info("isPremiumOnly: {}", request.getIsPremiumOnly());
        log.info("===============================");
        
        // 检查文件名是否已存在
        if (audioRepository.findByFileName(request.getFileName()) != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST.getCode(), "音频文件名已存在");
        }
        
        Audio audio = new Audio();
        audio.setIsPublic(request.getIsPublic());
        audio.setDescription(request.getDescription());
        audio.setFileName(request.getFileName());
        audio.setVoiceType(request.getVoiceType());
        
        // 设置新字段
        QuitMode quitMode = request.getQuitMode();
        Boolean isPremiumOnly = request.getIsPremiumOnly();
        
        log.info("设置Audio实体字段 - 请求中的quitMode: {}, isPremiumOnly: {}", quitMode, isPremiumOnly);
        
        audio.setQuitMode(quitMode);
        audio.setIsPremiumOnly(isPremiumOnly);
        audio.setUserId(userId);
        audio.setIsDisabled(false);
        
        log.info("Audio实体设置完成 - quitMode: {}, isPremiumOnly: {}, userId: {}", 
                audio.getQuitMode(), audio.getIsPremiumOnly(), audio.getUserId() != null ? audio.getUserId() : "NULL(系统音频)");
        
        Audio savedAudio = audioRepository.save(audio);
        log.info("音频创建成功，ID: {}", savedAudio.getId());
        log.info("保存后的音频实体 - quitMode: {}, isPremiumOnly: {}, userId: {}", 
                savedAudio.getQuitMode(), savedAudio.getIsPremiumOnly(), savedAudio.getUserId() != null ? savedAudio.getUserId() : "NULL(系统音频)");
        
        return AudioResponse.fromEntity(savedAudio);
    }
    
    @Override
    @Transactional
    public AudioResponse updateAudio(Long userId, Long audioId, AudioRequest request) {
        log.info("更新音频，用户ID: {}, 音频ID: {}", userId, audioId);
        
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "音频不存在"));
        
        if (!audio.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "无权限修改此音频");
        }
        
        // 检查文件名是否被其他音频使用
        Audio existingAudio = audioRepository.findByFileName(request.getFileName());
        if (existingAudio != null && !existingAudio.getId().equals(audioId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST.getCode(), "音频文件名已存在");
        }
        
        audio.setIsPublic(request.getIsPublic());
        audio.setDescription(request.getDescription());
        audio.setFileName(request.getFileName());
        audio.setVoiceType(request.getVoiceType());
        audio.setQuitMode(request.getQuitMode());
        audio.setIsPremiumOnly(request.getIsPremiumOnly());
        
        Audio updatedAudio = audioRepository.save(audio);
        log.info("音频更新成功，ID: {}", updatedAudio.getId());
        
        return AudioResponse.fromEntity(updatedAudio);
    }
    
    @Override
    public AudioResponse getAudio(Long audioId) {
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "音频不存在"));
        
        return AudioResponse.fromEntity(audio);
    }
    
    @Override
    public List<AudioResponse> getUserAudios(Long userId) {
        List<Audio> audios = audioRepository.findByUserIdAndIsDisabledFalseOrderByCreatedAtDesc(userId);
        return audios.stream()
                .map(AudioResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AudioResponse> getPublicAudios() {
        List<Audio> audios = audioRepository.findByIsPublicTrueAndIsDisabledFalseOrderByCreatedAtDesc();
        return audios.stream()
                .map(AudioResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AudioResponse> getPublicAudiosByVoiceType(String voiceType) {
        // 修复：使用正确的方法名
        List<Audio> audios = audioRepository.findByVoiceTypeAndIsPublicTrueAndIsDisabledFalseOrderByCreatedAtDesc(voiceType);
        return audios.stream()
                .map(AudioResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteAudio(Long userId, Long audioId) {
        log.info("删除音频，用户ID: {}, 音频ID: {}", userId, audioId);
        
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "音频不存在"));
        
        if (!audio.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "无权限删除此音频");
        }
        
        audioRepository.delete(audio);
        log.info("音频删除成功，ID: {}", audioId);
    }
    
    @Override
    @Transactional
    public AudioResponse toggleAudioStatus(Long userId, Long audioId) {
        log.info("切换音频状态，用户ID: {}, 音频ID: {}", userId, audioId);
        
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "音频不存在"));
        
        if (!audio.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "无权限修改此音频");
        }
        
        audio.setIsDisabled(!audio.getIsDisabled());
        Audio updatedAudio = audioRepository.save(audio);
        
        log.info("音频状态切换成功，ID: {}, 新状态: {}", audioId, updatedAudio.getIsDisabled());
        return AudioResponse.fromEntity(updatedAudio);
    }
    
    @Override
    public boolean isAudioOwnedByUser(Long audioId, Long userId) {
        Audio audio = audioRepository.findById(audioId).orElse(null);
        return audio != null && audio.getUserId().equals(userId);
    }
    
    @Override
    public Resource downloadAudio(Long audioId, Long userId) {
        log.info("下载音频，音频ID: {}, 用户ID: {}", audioId, userId);
        
        // 获取音频信息
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "音频不存在"));
        
        // 检查权限
        if (!canUserDownloadAudio(audioId, userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "无权限下载此音频");
        }
        
        try {
            // 构建文件路径
            Path filePath = Paths.get(AUDIO_UPLOAD_DIR).resolve(audio.getFileName());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                log.info("音频文件准备下载，文件名: {}, 大小: {} bytes", 
                    audio.getFileName(), resource.contentLength());
                return resource;
            } else {
                throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "音频文件不存在或无法读取");
            }
        } catch (MalformedURLException e) {
            log.error("音频文件路径错误", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "音频文件路径错误");
        } catch (IOException e) {
            log.error("读取音频文件失败", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "读取音频文件失败");
        }
    }
    
    @Override
    public boolean canUserDownloadAudio(Long audioId, Long userId) {
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "音频不存在"));
        
        // 检查音频是否被禁用
        if (audio.getIsDisabled()) {
            return false;
        }
        
        // 公有音频：所有用户都可以下载
        if (audio.getIsPublic()) {
            // 检查会员限制
            if (audio.getIsPremiumOnly()) {
                // TODO: 这里需要检查用户是否为会员
                // 暂时返回true，后续需要实现会员检查逻辑
                return true;
            }
            return true;
        }
        
        // 私有音频：只有音频所有者可以下载
        return audio.getUserId().equals(userId);
    }
} 