package com.magic.crm.service;

import com.magic.crm.dto.LeadDTO;
import com.magic.crm.dto.LeadSearchDTO;
import com.magic.crm.entity.Lead;
import com.magic.crm.entity.LeadFollowUp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * 线索服务接口
 */
public interface LeadService {
    
    /**
     * 创建线索
     */
    Lead createLead(LeadDTO leadDTO);
    
    /**
     * 更新线索
     */
    Lead updateLead(UUID id, LeadDTO leadDTO);
    
    /**
     * 获取线索详情
     */
    Lead getLeadById(UUID id);
    
    /**
     * 根据编号获取线索
     */
    Lead getLeadByCode(String code);
    
    /**
     * 删除线索
     */
    void deleteLead(UUID id);
    
    /**
     * 批量删除线索
     */
    void deleteLeads(List<UUID> ids);
    
    /**
     * 搜索线索
     */
    Page<Lead> searchLeads(LeadSearchDTO searchDTO, Pageable pageable);
    
    /**
     * 获取所有线索
     */
    Page<Lead> getAllLeads(Pageable pageable);
    
    /**
     * 根据所属销售获取线索
     */
    Page<Lead> getLeadsByOwner(UUID ownerId, Pageable pageable);
    
    /**
     * 根据状态获取线索
     */
    List<Lead> getLeadsByStatus(String status);
    
    /**
     * 根据优先级获取线索
     */
    List<Lead> getLeadsByPriority(String priority);
    
    /**
     * 根据来源获取线索
     */
    List<Lead> getLeadsBySource(String source);
    
    /**
     * 获取需要跟进的线索
     */
    List<Lead> getLeadsToFollow();
    
    /**
     * 获取超期未跟进的线索
     */
    List<Lead> getOverdueLeads();
    
    /**
     * 获取高评分线索
     */
    List<Lead> getHighScoreLeads(Integer minScore);
    
    /**
     * 获取即将到期的线索
     */
    List<Lead> getLeadsClosingSoon(int days);
    
    /**
     * 分配线索
     */
    void assignLead(UUID leadId, UUID ownerId);
    
    /**
     * 批量分配线索
     */
    void batchAssignLeads(List<UUID> leadIds, UUID ownerId);
    
    /**
     * 转换线索为客户
     */
    Lead convertToCustomer(UUID leadId, UUID customerId);
    
    /**
     * 更新线索状态
     */
    void updateLeadStatus(UUID leadId, String status);
    
    /**
     * 更新线索评分
     */
    void updateLeadScore(UUID leadId, Integer score);
    
    /**
     * 更新最后联系时间
     */
    void updateLastContactTime(UUID leadId);
    
    /**
     * 添加跟进记录
     */
    LeadFollowUp addFollowUp(UUID leadId, LeadFollowUp followUp);
    
    /**
     * 获取线索跟进记录
     */
    List<LeadFollowUp> getLeadFollowUps(UUID leadId);
    
    /**
     * 生成线索编号
     */
    String generateLeadCode();
    
    /**
     * 导入线索
     */
    List<Lead> importLeads(List<LeadDTO> leadDTOs);
    
    /**
     * 导出线索
     */
    List<Lead> exportLeads(LeadSearchDTO searchDTO);
    
    /**
     * 获取线索统计信息
     */
    LeadStatistics getLeadStatistics(UUID ownerId);
    
    /**
     * 检查线索重复
     */
    List<Lead> checkDuplicates(String name, String companyName, String contactPhone, String contactEmail);
    
    /**
     * 线索统计信息内部类
     */
    class LeadStatistics {
        private Long totalCount;
        private Long newCount;
        private Long contactedCount;
        private Long qualifiedCount;
        private Long unqualifiedCount;
        private Long convertedCount;
        private Long highPriorityCount;
        private Long overdueCount;
        private Long toFollowCount;
        private Double conversionRate;
        private Long totalValue;
        
        // Getters and Setters
        public Long getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }
        
        public Long getNewCount() {
            return newCount;
        }
        
        public void setNewCount(Long newCount) {
            this.newCount = newCount;
        }
        
        public Long getContactedCount() {
            return contactedCount;
        }
        
        public void setContactedCount(Long contactedCount) {
            this.contactedCount = contactedCount;
        }
        
        public Long getQualifiedCount() {
            return qualifiedCount;
        }
        
        public void setQualifiedCount(Long qualifiedCount) {
            this.qualifiedCount = qualifiedCount;
        }
        
        public Long getUnqualifiedCount() {
            return unqualifiedCount;
        }
        
        public void setUnqualifiedCount(Long unqualifiedCount) {
            this.unqualifiedCount = unqualifiedCount;
        }
        
        public Long getConvertedCount() {
            return convertedCount;
        }
        
        public void setConvertedCount(Long convertedCount) {
            this.convertedCount = convertedCount;
        }
        
        public Long getHighPriorityCount() {
            return highPriorityCount;
        }
        
        public void setHighPriorityCount(Long highPriorityCount) {
            this.highPriorityCount = highPriorityCount;
        }
        
        public Long getOverdueCount() {
            return overdueCount;
        }
        
        public void setOverdueCount(Long overdueCount) {
            this.overdueCount = overdueCount;
        }
        
        public Long getToFollowCount() {
            return toFollowCount;
        }
        
        public void setToFollowCount(Long toFollowCount) {
            this.toFollowCount = toFollowCount;
        }
        
        public Double getConversionRate() {
            return conversionRate;
        }
        
        public void setConversionRate(Double conversionRate) {
            this.conversionRate = conversionRate;
        }
        
        public Long getTotalValue() {
            return totalValue;
        }
        
        public void setTotalValue(Long totalValue) {
            this.totalValue = totalValue;
        }
    }
}
