package com.denwon.crm.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 分页响应格式
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    private Integer code;
    private Boolean success;
    private String message;
    private PageData<T> data;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageData<T> {
        private Long totalCount;
        private Integer pageIndex;
        private Integer pageCount;
        private List<T> records;
    }
    
    public static <T> PageResponse<T> success(Long totalCount, Integer pageIndex, 
                                               Integer pageCount, List<T> records) {
        return PageResponse.<T>builder()
                .code(200)
                .success(true)
                .message("查询成功")
                .data(PageData.<T>builder()
                        .totalCount(totalCount)
                        .pageIndex(pageIndex)
                        .pageCount(pageCount)
                        .records(records)
                        .build())
                .build();
    }
}