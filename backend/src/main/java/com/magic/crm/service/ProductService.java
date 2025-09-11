package com.magic.crm.service;

import com.magic.crm.dto.ProductDTO;
import com.magic.crm.dto.ProductSearchDTO;
import com.magic.crm.entity.Product;
import com.magic.crm.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * 产品服务接口
 */
public interface ProductService {
    
    /**
     * 创建产品
     */
    Product createProduct(ProductDTO productDTO);
    
    /**
     * 更新产品
     */
    Product updateProduct(UUID id, ProductDTO productDTO);
    
    /**
     * 获取产品详情
     */
    Product getProductById(UUID id);
    
    /**
     * 根据编码获取产品
     */
    Product getProductByCode(String code);
    
    /**
     * 删除产品
     */
    void deleteProduct(UUID id);
    
    /**
     * 批量删除产品
     */
    void deleteProducts(List<UUID> ids);
    
    /**
     * 搜索产品
     */
    Page<Product> searchProducts(ProductSearchDTO searchDTO, Pageable pageable);
    
    /**
     * 获取所有产品
     */
    Page<Product> getAllProducts(Pageable pageable);
    
    /**
     * 根据分类获取产品
     */
    Page<Product> getProductsByCategory(UUID categoryId, Pageable pageable);
    
    /**
     * 根据品牌获取产品
     */
    List<Product> getProductsByBrand(String brand);
    
    /**
     * 根据状态获取产品
     */
    List<Product> getProductsByStatus(String status);
    
    /**
     * 获取备件产品
     */
    List<Product> getSparePartProducts();
    
    /**
     * 获取套餐产品
     */
    List<Product> getBundleProducts();
    
    /**
     * 获取可定制产品
     */
    List<Product> getCustomizableProducts();
    
    /**
     * 获取低库存产品
     */
    List<Product> getLowStockProducts();
    
    /**
     * 获取缺货产品
     */
    List<Product> getOutOfStockProducts();
    
    /**
     * 生成产品编码
     */
    String generateProductCode();
    
    /**
     * 导入产品
     */
    List<Product> importProducts(List<ProductDTO> productDTOs);
    
    /**
     * 导出产品
     */
    List<Product> exportProducts(ProductSearchDTO searchDTO);
    
    /**
     * 获取产品统计信息
     */
    ProductStatistics getProductStatistics();
    
    /**
     * 检查产品重复
     */
    List<Product> checkDuplicates(String name, String code, String brand, String model);
    
    /**
     * 更新产品状态
     */
    void updateProductStatus(UUID productId, String status);
    
    /**
     * 批量更新产品状态
     */
    void batchUpdateProductStatus(List<UUID> productIds, String status);
    
    /**
     * 产品统计信息内部类
     */
    class ProductStatistics {
        private Long totalCount;
        private Long activeCount;
        private Long inactiveCount;
        private Long discontinuedCount;
        private Long sparePartCount;
        private Long bundleCount;
        private Long customizableCount;
        private Long lowStockCount;
        private Long outOfStockCount;
        
        // Getters and Setters
        public Long getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }
        
        public Long getActiveCount() {
            return activeCount;
        }
        
        public void setActiveCount(Long activeCount) {
            this.activeCount = activeCount;
        }
        
        public Long getInactiveCount() {
            return inactiveCount;
        }
        
        public void setInactiveCount(Long inactiveCount) {
            this.inactiveCount = inactiveCount;
        }
        
        public Long getDiscontinuedCount() {
            return discontinuedCount;
        }
        
        public void setDiscontinuedCount(Long discontinuedCount) {
            this.discontinuedCount = discontinuedCount;
        }
        
        public Long getSparePartCount() {
            return sparePartCount;
        }
        
        public void setSparePartCount(Long sparePartCount) {
            this.sparePartCount = sparePartCount;
        }
        
        public Long getBundleCount() {
            return bundleCount;
        }
        
        public void setBundleCount(Long bundleCount) {
            this.bundleCount = bundleCount;
        }
        
        public Long getCustomizableCount() {
            return customizableCount;
        }
        
        public void setCustomizableCount(Long customizableCount) {
            this.customizableCount = customizableCount;
        }
        
        public Long getLowStockCount() {
            return lowStockCount;
        }
        
        public void setLowStockCount(Long lowStockCount) {
            this.lowStockCount = lowStockCount;
        }
        
        public Long getOutOfStockCount() {
            return outOfStockCount;
        }
        
        public void setOutOfStockCount(Long outOfStockCount) {
            this.outOfStockCount = outOfStockCount;
        }
    }
}
