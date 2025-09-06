package com.jiayan.quitsmoking.controller;

import com.jiayan.quitsmoking.common.ApiResponse;
import com.jiayan.quitsmoking.dto.CreateSmokingRecordRequest;
import com.jiayan.quitsmoking.dto.CreateTrainingRecordRequest;
import com.jiayan.quitsmoking.dto.SmokingRecordResponse;
import com.jiayan.quitsmoking.dto.TrainingRecordResponse;
import com.jiayan.quitsmoking.service.SmokingRecordService;
import com.jiayan.quitsmoking.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;

@Slf4j
@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class SmokingRecordController {

    private final SmokingRecordService smokingRecordService;
    private final JwtUtil jwtUtil;

    /**
     * 记录吸烟
     */
    @PostMapping("/smoking")
    public ResponseEntity<ApiResponse<SmokingRecordResponse>> createSmokingRecord(
            @Valid @RequestBody CreateSmokingRecordRequest request,
            HttpServletRequest httpRequest) {

        log.info("接收到记录吸烟请求: {}", request);

        try {
            // 从JWT Token中获取用户ID
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                log.error("未找到有效的Authorization头");
                return ResponseEntity.badRequest().body(ApiResponse.error(1001, "未提供有效的认证Token"));
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Long userId = Long.valueOf(userIdStr);

            SmokingRecordResponse response = smokingRecordService.createSmokingRecord(userId, request);

            return ResponseEntity.ok(ApiResponse.success("记录成功", response));
        } catch (Exception e) {
            log.error("记录吸烟时发生异常", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(1001, "Token无效或已过期"));
        }
    }

    /**
     * 记录训练
     */
    @PostMapping("/training")
    public ResponseEntity<ApiResponse<TrainingRecordResponse>> createTrainingRecord(
            @Valid @RequestBody CreateTrainingRecordRequest request,
            HttpServletRequest httpRequest) {

        log.info("接收到记录训练请求: {}", request);

        try {
            // 从JWT Token中获取用户ID
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                log.error("未找到有效的Authorization头");
                return ResponseEntity.badRequest().body(ApiResponse.error(1001, "未提供有效的认证Token"));
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            Long userId = Long.valueOf(userIdStr);

            TrainingRecordResponse response = smokingRecordService.createTrainingRecord(userId, request);

            return ResponseEntity.ok(ApiResponse.success("记录成功", response));
        } catch (Exception e) {
            log.error("记录训练时发生异常", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(1001, "Token无效或已过期"));
        }
    }

    /**
     * 获取记录列表（支持分页和日期过滤）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getRecords(
            @RequestParam(defaultValue = "smoking") String type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            HttpServletRequest httpRequest) {

        log.info("获取记录列表请求 - 类型: {}, 日期: {}, 限制: {}, 偏移: {}", type, date, limit, offset);

        // 从JWT Token中获取用户ID
        String token = extractTokenFromHeader(httpRequest);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.valueOf(userIdStr);

        if ("smoking".equals(type)) {
            if (date != null) {
                // 获取指定日期的吸烟记录
                List<SmokingRecordResponse> records = smokingRecordService.getUserSmokingRecordsByDate(userId, date);
                return ResponseEntity.ok(ApiResponse.success("获取成功", 
                    new RecordListResponse(records.size(), records)));
            } else {
                // 分页获取吸烟记录
                Pageable pageable = PageRequest.of(offset / limit, limit);
                Page<SmokingRecordResponse> recordPage = smokingRecordService.getUserSmokingRecords(userId, pageable);
                return ResponseEntity.ok(ApiResponse.success("获取成功", 
                    new RecordListResponse(recordPage.getTotalElements(), recordPage.getContent())));
            }
        } else if ("training".equals(type)) {
            if (date != null) {
                // 获取指定日期的训练记录
                List<TrainingRecordResponse> records = smokingRecordService.getUserTrainingRecordsByDate(userId, date);
                return ResponseEntity.ok(ApiResponse.success("获取成功", 
                    new TrainingRecordListResponse(records.size(), records)));
            } else {
                // 分页获取训练记录
                Pageable pageable = PageRequest.of(offset / limit, limit);
                Page<TrainingRecordResponse> recordPage = smokingRecordService.getUserTrainingRecords(userId, pageable);
                return ResponseEntity.ok(ApiResponse.success("获取成功", 
                    new TrainingRecordListResponse(recordPage.getTotalElements(), recordPage.getContent())));
            }
        }

        return ResponseEntity.badRequest().body(ApiResponse.error(400, "不支持的记录类型，支持: smoking, training"));
    }

    /**
     * 获取今日吸烟统计
     */
    @GetMapping("/smoking/today-count")
    public ResponseEntity<ApiResponse<Long>> getTodaySmokingCount(HttpServletRequest httpRequest) {
        log.info("获取今日吸烟统计请求");

        try {
            // 从JWT Token中获取用户ID
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供认证令牌"));
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            if (userIdStr == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "认证令牌无效"));
            }
            
            Long userId = Long.valueOf(userIdStr);

            Long count = smokingRecordService.getTodaySmokingCount(userId);

            return ResponseEntity.ok(ApiResponse.success("获取成功", count));
            
        } catch (NumberFormatException e) {
            log.error("用户ID格式错误", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "用户ID格式错误"));
        } catch (Exception e) {
            log.error("获取今日吸烟统计异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "获取统计失败，请稍后重试"));
        }
    }

    /**
     * 获取今日训练统计
     */
    @GetMapping("/training/today-count")
    public ResponseEntity<ApiResponse<Long>> getTodayTrainingCount(HttpServletRequest httpRequest) {
        log.info("获取今日训练统计请求");

        try {
            // 从JWT Token中获取用户ID
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供认证令牌"));
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            if (userIdStr == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "认证令牌无效"));
            }
            
            Long userId = Long.valueOf(userIdStr);

            Long count = smokingRecordService.getTodayTrainingCount(userId);

            return ResponseEntity.ok(ApiResponse.success("获取成功", count));
            
        } catch (NumberFormatException e) {
            log.error("用户ID格式错误", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "用户ID格式错误"));
        } catch (Exception e) {
            log.error("获取今日训练统计异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "获取统计失败，请稍后重试"));
        }
    }

    /**
     * 获取最后一次吸烟记录
     */
    @GetMapping("/smoking/last")
    public ResponseEntity<ApiResponse<SmokingRecordResponse>> getLastSmokingRecord(HttpServletRequest httpRequest) {
        log.info("获取最后一次吸烟记录请求");

        try {
            // 从JWT Token中获取用户ID
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供认证令牌"));
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            if (userIdStr == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "认证令牌无效"));
            }
            
            Long userId = Long.valueOf(userIdStr);

            SmokingRecordResponse response = smokingRecordService.getLastSmokingRecord(userId);
            
            if (response == null) {
                // 没有找到记录，返回空数据而不是null
                return ResponseEntity.ok(ApiResponse.success("暂无吸烟记录", null));
            }

            return ResponseEntity.ok(ApiResponse.success("获取成功", response));
            
        } catch (NumberFormatException e) {
            log.error("用户ID格式错误", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "用户ID格式错误"));
        } catch (Exception e) {
            log.error("获取最后一次吸烟记录异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "获取记录失败，请稍后重试"));
        }
    }

    /**
     * 获取最后一次训练记录
     */
    @GetMapping("/training/last")
    public ResponseEntity<ApiResponse<TrainingRecordResponse>> getLastTrainingRecord(HttpServletRequest httpRequest) {
        log.info("获取最后一次训练记录请求");

        try {
            // 从JWT Token中获取用户ID
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供认证令牌"));
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            if (userIdStr == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "认证令牌无效"));
            }
            
            Long userId = Long.valueOf(userIdStr);

            TrainingRecordResponse response = smokingRecordService.getLastTrainingRecord(userId);
            
            if (response == null) {
                // 没有找到记录，返回空数据而不是null
                return ResponseEntity.ok(ApiResponse.success("暂无训练记录", null));
            }

            return ResponseEntity.ok(ApiResponse.success("获取成功", response));
            
        } catch (NumberFormatException e) {
            log.error("用户ID格式错误", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "用户ID格式错误"));
        } catch (Exception e) {
            log.error("获取最后一次训练记录异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "获取记录失败，请稍后重试"));
        }
    }

    /**
     * 删除吸烟记录
     */
    @DeleteMapping("/smoking/{recordId}")
    public ResponseEntity<ApiResponse<Void>> deleteSmokingRecord(
            @PathVariable Long recordId,
            HttpServletRequest httpRequest) {

        log.info("删除吸烟记录请求，记录ID: {}", recordId);

        try {
            // 从JWT Token中获取用户ID
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供认证令牌"));
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            if (userIdStr == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "认证令牌无效"));
            }
            
            Long userId = Long.valueOf(recordId);
            Long userTokenId = Long.valueOf(userIdStr);

            smokingRecordService.deleteSmokingRecord(userTokenId, userId);

            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
            
        } catch (NumberFormatException e) {
            log.error("用户ID或记录ID格式错误", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "ID格式错误"));
        } catch (Exception e) {
            log.error("删除吸烟记录异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "删除记录失败，请稍后重试"));
        }
    }

    /**
     * 删除训练记录
     */
    @DeleteMapping("/training/{recordId}")
    public ResponseEntity<ApiResponse<Void>> deleteTrainingRecord(
            @PathVariable Long recordId,
            HttpServletRequest httpRequest) {

        log.info("删除训练记录请求，记录ID: {}", recordId);

        try {
            // 从JWT Token中获取用户ID
            String token = extractTokenFromHeader(httpRequest);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供认证令牌"));
            }
            
            String userIdStr = jwtUtil.getUserIdFromToken(token);
            if (userIdStr == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "认证令牌无效"));
            }
            
            Long userId = Long.valueOf(recordId);
            Long userTokenId = Long.valueOf(userIdStr);

            smokingRecordService.deleteTrainingRecord(userTokenId, userId);

            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
            
        } catch (NumberFormatException e) {
            log.error("用户ID或记录ID格式错误", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "ID格式错误"));
        } catch (Exception e) {
            log.error("删除训练记录异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "删除记录失败，请稍后重试"));
        }
    }

    /**
     * 从请求头中提取JWT Token
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * 吸烟记录列表响应类
     */
    public static class RecordListResponse {
        public final long total;
        public final List<SmokingRecordResponse> records;

        public RecordListResponse(long total, List<SmokingRecordResponse> records) {
            this.total = total;
            this.records = records;
        }
    }

    /**
     * 训练记录列表响应类
     */
    public static class TrainingRecordListResponse {
        public final long total;
        public final List<TrainingRecordResponse> records;

        public TrainingRecordListResponse(long total, List<TrainingRecordResponse> records) {
            this.total = total;
            this.records = records;
        }
    }
} 