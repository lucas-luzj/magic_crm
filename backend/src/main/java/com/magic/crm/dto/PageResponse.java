package com.magic.crm.dto;

import org.springframework.data.domain.Page;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用分页响应DTO
 * 支持多种Entity到DTO的转换方式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private long totalCount;
    private int pageIndex;
    private int pageSize;
    private int pageCount;
    private List<T> records;
    private boolean first;
    private boolean last;
    private int numberOfElements;
    private boolean empty;
    private Sort sort;

    /**
     * 方案1: 使用Function转换器 (最推荐)
     * 优点: 类型安全，性能好，灵活
     */
    public static <E, D> PageResponse<D> of(Page<E> page, Function<E, D> converter) {
        PageResponse<D> response = new PageResponse<>();
        response.totalCount = page.getTotalElements();
        response.pageIndex = page.getNumber() + 1;
        response.pageSize = page.getSize();
        response.pageCount = page.getTotalPages();
        response.first = page.isFirst();
        response.last = page.isLast();
        response.numberOfElements = page.getNumberOfElements();
        response.empty = page.isEmpty();
        response.sort = Sort.of(page.getSort());
        response.records = page.getContent().stream()
                .map(converter)
                .collect(Collectors.toList());
        return response;
    }

    /**
     * 方案2: 直接传入已转换的数据 (简单场景)
     */
    public PageResponse(Page<?> page, List<T> convertedRecords) {
        this.totalCount = page.getTotalElements();
        this.pageIndex = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.pageCount = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.numberOfElements = page.getNumberOfElements();
        this.empty = page.isEmpty();
        this.sort = Sort.of(page.getSort());
        this.records = convertedRecords;
    }

    /**
     * 方案3: 不转换，直接返回原始数据 (Entity直接返回)
     */
    public PageResponse(Page<T> page) {
        this.totalCount = page.getTotalElements();
        this.pageIndex = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.pageCount = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.numberOfElements = page.getNumberOfElements();
        this.empty = page.isEmpty();
        this.sort = Sort.of(page.getSort());
        this.records = page.getContent();
    }

    /**
     * 排序信息
     */
    public static class Sort {
        private boolean sorted;
        private boolean unsorted;
        private List<Order> orders;
        
        public Sort() {}
        
        public Sort(boolean sorted, boolean unsorted, List<Order> orders) {
            this.sorted = sorted;
            this.unsorted = unsorted;
            this.orders = orders;
        }
        
        public static Sort of(org.springframework.data.domain.Sort sort) {
            if (sort == null) {
                return new Sort(false, true, null);
            }
            
            List<Order> orders = sort.stream()
                    .map(order -> new Order(order.getProperty(), order.getDirection().name()))
                    .toList();
            
            return new Sort(sort.isSorted(), sort.isUnsorted(), orders);
        }
        
        // Getters and Setters
        public boolean isSorted() {
            return sorted;
        }
        
        public void setSorted(boolean sorted) {
            this.sorted = sorted;
        }
        
        public boolean isUnsorted() {
            return unsorted;
        }
        
        public void setUnsorted(boolean unsorted) {
            this.unsorted = unsorted;
        }
        
        public List<Order> getOrders() {
            return orders;
        }
        
        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }
    }

    /**
     * 排序字段
     */
    public static class Order {
        private String property;
        private String direction;
        
        public Order() {}
        
        public Order(String property, String direction) {
            this.property = property;
            this.direction = direction;
        }
        
        // Getters and Setters
        public String getProperty() {
            return property;
        }
        
        public void setProperty(String property) {
            this.property = property;
        }
        
        public String getDirection() {
            return direction;
        }
        
        public void setDirection(String direction) {
            this.direction = direction;
        }
    }
}
