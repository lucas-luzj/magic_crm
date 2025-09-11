package com.magic.crm.service;

import com.magic.crm.dto.SupplierDTO;
import com.magic.crm.dto.SupplierSearchDTO;
import com.magic.crm.entity.Supplier;
import com.magic.crm.entity.SupplierContact;
import com.magic.crm.entity.SupplierProduct;
import com.magic.crm.entity.SupplierEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * 供应商服务接口
 */
public interface SupplierService {
    
    /**
     * 创建供应商
     */
    Supplier createSupplier(SupplierDTO supplierDTO);
    
    /**
     * 更新供应商
     */
    Supplier updateSupplier(UUID id, SupplierDTO supplierDTO);
    
    /**
     * 获取供应商详情
     */
    Supplier getSupplierById(UUID id);
    
    /**
     * 根据编码获取供应商
     */
    Supplier getSupplierByCode(String code);
    
    /**
     * 删除供应商
     */
    void deleteSupplier(UUID id);
    
    /**
     * 批量删除供应商
     */
    void deleteSuppliers(List<UUID> ids);
    
    /**
     * 搜索供应商
     */
    Page<Supplier> searchSuppliers(SupplierSearchDTO searchDTO, Pageable pageable);
    
    /**
     * 获取所有供应商
     */
    Page<Supplier> getAllSuppliers(Pageable pageable);
    
    /**
     * 根据行业获取供应商
     */
    List<Supplier> getSuppliersByIndustry(String industry);
    
    /**
     * 根据地区获取供应商
     */
    List<Supplier> getSuppliersByRegion(String region);
    
    /**
     * 根据等级获取供应商
     */
    List<Supplier> getSuppliersByLevel(String level);
    
    /**
     * 根据类型获取供应商
     */
    List<Supplier> getSuppliersByType(String type);
    
    /**
     * 根据状态获取供应商
     */
    List<Supplier> getSuppliersByStatus(String status);
    
    /**
     * 获取黑名单供应商
     */
    List<Supplier> getBlacklistSuppliers();
    
    /**
     * 获取重点供应商
     */
    List<Supplier> getKeySuppliers();
    
    /**
     * 获取即将到期的合同
     */
    List<Supplier> getExpiringContracts(int days);
    
    /**
     * 获取已过期的合同
     */
    List<Supplier> getExpiredContracts();
    
    /**
     * 获取高评级供应商
     */
    List<Supplier> getHighRatedSuppliers(Integer minRating);
    
    /**
     * 获取需要评价的供应商
     */
    List<Supplier> getSuppliersNeedingEvaluation();
    
    /**
     * 添加联系人
     */
    SupplierContact addContact(UUID supplierId, SupplierContact contact);
    
    /**
     * 更新联系人
     */
    SupplierContact updateContact(UUID contactId, SupplierContact contact);
    
    /**
     * 删除联系人
     */
    void deleteContact(UUID contactId);
    
    /**
     * 获取供应商联系人
     */
    List<SupplierContact> getSupplierContacts(UUID supplierId);
    
    /**
     * 添加供应商产品
     */
    SupplierProduct addSupplierProduct(UUID supplierId, SupplierProduct supplierProduct);
    
    /**
     * 更新供应商产品
     */
    SupplierProduct updateSupplierProduct(UUID supplierProductId, SupplierProduct supplierProduct);
    
    /**
     * 删除供应商产品
     */
    void deleteSupplierProduct(UUID supplierProductId);
    
    /**
     * 获取供应商产品
     */
    List<SupplierProduct> getSupplierProducts(UUID supplierId);
    
    /**
     * 添加供应商评价
     */
    SupplierEvaluation addEvaluation(UUID supplierId, SupplierEvaluation evaluation);
    
    /**
     * 获取供应商评价
     */
    List<SupplierEvaluation> getSupplierEvaluations(UUID supplierId);
    
    /**
     * 更新供应商评级
     */
    void updateSupplierRatings(UUID supplierId, Integer qualityRating, Integer serviceRating, 
                              Integer priceRating, Integer overallRating);
    
    /**
     * 设置为黑名单
     */
    void setBlacklist(UUID supplierId, Boolean isBlacklist);
    
    /**
     * 设置为重点供应商
     */
    void setKeySupplier(UUID supplierId, Boolean isKeySupplier);
    
    /**
     * 生成供应商编码
     */
    String generateSupplierCode();
    
    /**
     * 导入供应商
     */
    List<Supplier> importSuppliers(List<SupplierDTO> supplierDTOs);
    
    /**
     * 导出供应商
     */
    List<Supplier> exportSuppliers(SupplierSearchDTO searchDTO);
    
    /**
     * 获取供应商统计信息
     */
    SupplierStatistics getSupplierStatistics();
    
    /**
     * 检查供应商重复
     */
    List<Supplier> checkDuplicates(String name, String uscc, String code);
    
    /**
     * 供应商统计信息内部类
     */
    class SupplierStatistics {
        private Long totalCount;
        private Long activeCount;
        private Long inactiveCount;
        private Long suspendedCount;
        private Long blacklistCount;
        private Long keySupplierCount;
        private Long expiringContractsCount;
        private Long expiredContractsCount;
        private Long highRatedCount;
        private Long needEvaluationCount;
        private Double averageRating;
        
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
        
        public Long getSuspendedCount() {
            return suspendedCount;
        }
        
        public void setSuspendedCount(Long suspendedCount) {
            this.suspendedCount = suspendedCount;
        }
        
        public Long getBlacklistCount() {
            return blacklistCount;
        }
        
        public void setBlacklistCount(Long blacklistCount) {
            this.blacklistCount = blacklistCount;
        }
        
        public Long getKeySupplierCount() {
            return keySupplierCount;
        }
        
        public void setKeySupplierCount(Long keySupplierCount) {
            this.keySupplierCount = keySupplierCount;
        }
        
        public Long getExpiringContractsCount() {
            return expiringContractsCount;
        }
        
        public void setExpiringContractsCount(Long expiringContractsCount) {
            this.expiringContractsCount = expiringContractsCount;
        }
        
        public Long getExpiredContractsCount() {
            return expiredContractsCount;
        }
        
        public void setExpiredContractsCount(Long expiredContractsCount) {
            this.expiredContractsCount = expiredContractsCount;
        }
        
        public Long getHighRatedCount() {
            return highRatedCount;
        }
        
        public void setHighRatedCount(Long highRatedCount) {
            this.highRatedCount = highRatedCount;
        }
        
        public Long getNeedEvaluationCount() {
            return needEvaluationCount;
        }
        
        public void setNeedEvaluationCount(Long needEvaluationCount) {
            this.needEvaluationCount = needEvaluationCount;
        }
        
        public Double getAverageRating() {
            return averageRating;
        }
        
        public void setAverageRating(Double averageRating) {
            this.averageRating = averageRating;
        }
    }
}
