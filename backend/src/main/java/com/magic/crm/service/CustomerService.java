package com.magic.crm.service;

import com.magic.crm.dto.CustomerDTO;
import com.magic.crm.dto.CustomerPoolDTO;
import com.magic.crm.dto.CustomerSearchDTO;
import com.magic.crm.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * 客户服务接口
 */
public interface CustomerService {
    
    /**
     * 创建客户
     */
    Customer createCustomer(CustomerDTO customerDTO);
    
    /**
     * 更新客户
     */
    Customer updateCustomer(UUID id, CustomerDTO customerDTO);
    
    /**
     * 获取客户详情
     */
    Customer getCustomerById(UUID id);
    
    /**
     * 根据编号获取客户
     */
    Customer getCustomerByCode(String code);
    
    /**
     * 删除客户
     */
    void deleteCustomer(UUID id);
    
    /**
     * 批量删除客户
     */
    void deleteCustomers(List<UUID> ids);
    
    /**
     * 搜索客户
     */
    Page<Customer> searchCustomers(CustomerSearchDTO searchDTO, Pageable pageable);
    
    /**
     * 获取公海池客户
     */
    Page<Customer> getPublicPoolCustomers(Pageable pageable);
    
    /**
     * 获取私海客户
     */
    Page<Customer> getPrivateCustomers(UUID ownerId, Pageable pageable);
    
    /**
     * 将客户移入公海池
     */
    void moveToPublicPool(CustomerPoolDTO poolDTO);
    
    /**
     * 批量将客户移入公海池
     */
    void batchMoveToPublicPool(List<UUID> customerIds, String reason);
    
    /**
     * 认领公海客户
     */
    void claimFromPublicPool(UUID customerId, UUID ownerId);
    
    /**
     * 批量认领公海客户
     */
    void batchClaimFromPublicPool(List<UUID> customerIds, UUID ownerId);
    
    /**
     * 分配客户
     */
    void assignCustomer(UUID customerId, UUID newOwnerId);
    
    /**
     * 批量分配客户
     */
    void batchAssignCustomers(List<UUID> customerIds, UUID newOwnerId);
    
    /**
     * 转移客户
     */
    void transferCustomer(UUID customerId, UUID fromOwnerId, UUID toOwnerId);
    
    /**
     * 共享客户
     */
    void shareCustomer(UUID customerId, List<UUID> collaboratorIds);
    
    /**
     * 合并客户
     */
    void mergeCustomers(UUID primaryCustomerId, List<UUID> mergeCustomerIds);
    
    /**
     * 检查客户重复
     */
    List<Customer> checkDuplicates(String name, String uscc, String region);
    
    /**
     * 获取需要进入公海的客户
     */
    List<Customer> getCustomersToMoveToPool(int daysWithoutFollow, int daysWithoutOrder);
    
    /**
     * 自动将客户移入公海池
     */
    void autoMoveToPublicPool();
    
    /**
     * 生成客户编号
     */
    String generateCustomerCode();
    
    /**
     * 导入客户
     */
    List<Customer> importCustomers(List<CustomerDTO> customerDTOs);
    
    /**
     * 导出客户
     */
    List<Customer> exportCustomers(CustomerSearchDTO searchDTO);
    
    /**
     * 获取客户统计信息
     */
    CustomerStatistics getCustomerStatistics(UUID ownerId);
    
    /**
     * 获取客户的子公司
     */
    List<Customer> getSubsidiaries(UUID parentCustomerId);
    
    /**
     * 设置为重点客户
     */
    void setAsKeyCustomer(UUID customerId, boolean isKey);
    
    /**
     * 设置为黑名单
     */
    void setAsBlacklist(UUID customerId, boolean isBlacklist);
    
    /**
     * 更新最后跟进时间
     */
    void updateLastFollowTime(UUID customerId);
    
    /**
     * 更新最后成单时间
     */
    void updateLastOrderTime(UUID customerId);
    
    /**
     * 客户统计信息内部类
     */
    class CustomerStatistics {
        private Long totalCount;
        private Long publicPoolCount;
        private Long privateCount;
        private Long keyCustomerCount;
        private Long blacklistCount;
        private Long todayNewCount;
        private Long weekNewCount;
        private Long monthNewCount;
        
        // Getters and Setters
        public Long getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }
        
        public Long getPublicPoolCount() {
            return publicPoolCount;
        }
        
        public void setPublicPoolCount(Long publicPoolCount) {
            this.publicPoolCount = publicPoolCount;
        }
        
        public Long getPrivateCount() {
            return privateCount;
        }
        
        public void setPrivateCount(Long privateCount) {
            this.privateCount = privateCount;
        }
        
        public Long getKeyCustomerCount() {
            return keyCustomerCount;
        }
        
        public void setKeyCustomerCount(Long keyCustomerCount) {
            this.keyCustomerCount = keyCustomerCount;
        }
        
        public Long getBlacklistCount() {
            return blacklistCount;
        }
        
        public void setBlacklistCount(Long blacklistCount) {
            this.blacklistCount = blacklistCount;
        }
        
        public Long getTodayNewCount() {
            return todayNewCount;
        }
        
        public void setTodayNewCount(Long todayNewCount) {
            this.todayNewCount = todayNewCount;
        }
        
        public Long getWeekNewCount() {
            return weekNewCount;
        }
        
        public void setWeekNewCount(Long weekNewCount) {
            this.weekNewCount = weekNewCount;
        }
        
        public Long getMonthNewCount() {
            return monthNewCount;
        }
        
        public void setMonthNewCount(Long monthNewCount) {
            this.monthNewCount = monthNewCount;
        }
    }
}