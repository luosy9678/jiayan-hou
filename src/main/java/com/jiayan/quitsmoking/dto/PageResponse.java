package com.jiayan.quitsmoking.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页响应DTO
 */
@Data
public class PageResponse<T> {
    
    /**
     * 数据列表
     */
    private List<T> content;
    
    /**
     * 当前页码（从0开始）
     */
    private int pageNumber;
    
    /**
     * 每页大小
     */
    private int pageSize;
    
    /**
     * 总元素数
     */
    private long totalElements;
    
    /**
     * 总页数
     */
    private int totalPages;
    
    /**
     * 是否有上一页
     */
    private boolean hasPrevious;
    
    /**
     * 是否有下一页
     */
    private boolean hasNext;
    
    /**
     * 是否为第一页
     */
    private boolean isFirst;
    
    /**
     * 是否为最后一页
     */
    private boolean isLast;
    
    /**
     * 从Spring Data Page对象创建分页响应
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(page.getContent());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setHasPrevious(page.hasPrevious());
        response.setHasNext(page.hasNext());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        return response;
    }
    
    /**
     * 创建空的分页响应
     */
    public static <T> PageResponse<T> empty() {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(List.of());
        response.setPageNumber(0);
        response.setPageSize(0);
        response.setTotalElements(0);
        response.setTotalPages(0);
        response.setHasPrevious(false);
        response.setHasNext(false);
        response.setFirst(true);
        response.setLast(true);
        return response;
    }
} 