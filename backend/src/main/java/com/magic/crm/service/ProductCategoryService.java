package com.magic.crm.service;

import com.magic.crm.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * 产品分类服务接口
 */
public interface ProductCategoryService {
    
    /**
     * 创建分类
     */
    ProductCategory createCategory(ProductCategory category);
    
    /**
     * 更新分类
     */
    ProductCategory updateCategory(UUID id, ProductCategory category);
    
    /**
     * 获取分类详情
     */
    ProductCategory getCategoryById(UUID id);
    
    /**
     * 根据编码获取分类
     */
    ProductCategory getCategoryByCode(String code);
    
    /**
     * 删除分类
     */
    void deleteCategory(UUID id);
    
    /**
     * 批量删除分类
     */
    void deleteCategories(List<UUID> ids);
    
    /**
     * 获取所有分类
     */
    Page<ProductCategory> getAllCategories(Pageable pageable);
    
    /**
     * 获取分类树
     */
    List<ProductCategory> getCategoryTree();
    
    /**
     * 获取根分类
     */
    List<ProductCategory> getRootCategories();
    
    /**
     * 获取子分类
     */
    List<ProductCategory> getChildCategories(UUID parentId);
    
    /**
     * 搜索分类
     */
    Page<ProductCategory> searchCategories(String keyword, Pageable pageable);
    
    /**
     * 生成分类编码
     */
    String generateCategoryCode();
    
    /**
     * 检查分类是否可以删除（是否有子分类或产品）
     */
    boolean canDeleteCategory(UUID categoryId);
    
    /**
     * 移动分类
     */
    void moveCategory(UUID categoryId, UUID newParentId);
    
    /**
     * 更新分类路径
     */
    void updateCategoryPath(UUID categoryId);
    
    /**
     * 获取分类统计信息
     */
    CategoryStatistics getCategoryStatistics();
    
    /**
     * 分类统计信息内部类
     */
    class CategoryStatistics {
        private Long totalCount;
        private Long rootCount;
        private Long maxLevel;
        private Long productCount;
        
        // Getters and Setters
        public Long getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }
        
        public Long getRootCount() {
            return rootCount;
        }
        
        public void setRootCount(Long rootCount) {
            this.rootCount = rootCount;
        }
        
        public Long getMaxLevel() {
            return maxLevel;
        }
        
        public void setMaxLevel(Long maxLevel) {
            this.maxLevel = maxLevel;
        }
        
        public Long getProductCount() {
            return productCount;
        }
        
        public void setProductCount(Long productCount) {
            this.productCount = productCount;
        }
    }
}
