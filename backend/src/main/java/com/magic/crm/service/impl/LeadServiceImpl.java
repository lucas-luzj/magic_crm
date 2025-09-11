package com.magic.crm.service.impl;

import com.magic.crm.dto.LeadDTO;
import com.magic.crm.dto.LeadSearchDTO;
import com.magic.crm.entity.Lead;
import com.magic.crm.entity.LeadFollowUp;
import com.magic.crm.repository.LeadRepository;
import com.magic.crm.service.LeadService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 线索服务实现类
 */
@Service
@Transactional
public class LeadServiceImpl implements LeadService {
    
    @Autowired
    private LeadRepository leadRepository;
    
    @Override
    public Lead createLead(LeadDTO leadDTO) {
        // 检查线索编号是否已存在
        if (StringUtils.hasText(leadDTO.getCode()) && leadRepository.existsByCode(leadDTO.getCode())) {
            throw new RuntimeException("线索编号已存在: " + leadDTO.getCode());
        }
        
        // 检查线索名称是否已存在
        if (leadRepository.existsByName(leadDTO.getName())) {
            throw new RuntimeException("线索名称已存在: " + leadDTO.getName());
        }
        
        Lead lead = new Lead();
        BeanUtils.copyProperties(leadDTO, lead);
        
        // 生成线索编号
        if (!StringUtils.hasText(lead.getCode())) {
            lead.setCode(generateLeadCode());
        }
        
        // 设置创建信息
        lead.setCreatedAt(LocalDateTime.now());
        lead.setUpdatedAt(LocalDateTime.now());
        lead.setDeleted(false);
        lead.setStatus("NEW");
        lead.setPriority("MEDIUM");
        lead.setScore(0);
        
        return leadRepository.save(lead);
    }
    
    @Override
    public Lead updateLead(UUID id, LeadDTO leadDTO) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("线索不存在，ID: " + id));
        
        // 检查线索编号是否已存在（排除自己）
        if (StringUtils.hasText(leadDTO.getCode()) && 
            !leadDTO.getCode().equals(lead.getCode()) && 
            leadRepository.existsByCode(leadDTO.getCode())) {
            throw new RuntimeException("线索编号已存在: " + leadDTO.getCode());
        }
        
        // 检查线索名称是否已存在（排除自己）
        if (StringUtils.hasText(leadDTO.getName()) && 
            !leadDTO.getName().equals(lead.getName()) && 
            leadRepository.existsByName(leadDTO.getName())) {
            throw new RuntimeException("线索名称已存在: " + leadDTO.getName());
        }
        
        BeanUtils.copyProperties(leadDTO, lead, "id", "createdAt", "createdBy");
        lead.setUpdatedAt(LocalDateTime.now());
        
        return leadRepository.save(lead);
    }
    
    @Override
    public Lead getLeadById(UUID id) {
        return leadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("线索不存在，ID: " + id));
    }
    
    @Override
    public Lead getLeadByCode(String code) {
        return leadRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("线索不存在，编号: " + code));
    }
    
    @Override
    public void deleteLead(UUID id) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("线索不存在，ID: " + id));
        
        lead.setDeleted(true);
        lead.setUpdatedAt(LocalDateTime.now());
        leadRepository.save(lead);
    }
    
    @Override
    public void deleteLeads(List<UUID> ids) {
        List<Lead> leads = leadRepository.findAllById(ids);
        for (Lead lead : leads) {
            lead.setDeleted(true);
            lead.setUpdatedAt(LocalDateTime.now());
        }
        leadRepository.saveAll(leads);
    }
    
    @Override
    public Page<Lead> searchLeads(LeadSearchDTO searchDTO, Pageable pageable) {
        Specification<Lead> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 基本条件
            predicates.add(cb.equal(root.get("deleted"), false));
            
            if (StringUtils.hasText(searchDTO.getName())) {
                predicates.add(cb.like(cb.lower(root.get("name")), 
                    "%" + searchDTO.getName().toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(searchDTO.getCode())) {
                predicates.add(cb.like(cb.lower(root.get("code")), 
                    "%" + searchDTO.getCode().toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(searchDTO.getCompanyName())) {
                predicates.add(cb.like(cb.lower(root.get("companyName")), 
                    "%" + searchDTO.getCompanyName().toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(searchDTO.getContactName())) {
                predicates.add(cb.like(cb.lower(root.get("contactName")), 
                    "%" + searchDTO.getContactName().toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(searchDTO.getContactPhone())) {
                predicates.add(cb.like(root.get("contactPhone"), 
                    "%" + searchDTO.getContactPhone() + "%"));
            }
            
            if (StringUtils.hasText(searchDTO.getContactEmail())) {
                predicates.add(cb.like(cb.lower(root.get("contactEmail")), 
                    "%" + searchDTO.getContactEmail().toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(searchDTO.getIndustry())) {
                predicates.add(cb.equal(root.get("industry"), searchDTO.getIndustry()));
            }
            
            if (StringUtils.hasText(searchDTO.getRegion())) {
                predicates.add(cb.equal(root.get("region"), searchDTO.getRegion()));
            }
            
            if (StringUtils.hasText(searchDTO.getSource())) {
                predicates.add(cb.equal(root.get("source"), searchDTO.getSource()));
            }
            
            if (StringUtils.hasText(searchDTO.getStatus())) {
                predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
            }
            
            if (StringUtils.hasText(searchDTO.getPriority())) {
                predicates.add(cb.equal(root.get("priority"), searchDTO.getPriority()));
            }
            
            if (searchDTO.getOwnerId() != null) {
                predicates.add(cb.equal(root.get("ownerId"), searchDTO.getOwnerId()));
            }
            
            // 关键词搜索
            if (StringUtils.hasText(searchDTO.getKeyword())) {
                String keyword = searchDTO.getKeyword().toLowerCase();
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), "%" + keyword + "%");
                Predicate companyPredicate = cb.like(cb.lower(root.get("companyName")), "%" + keyword + "%");
                Predicate contactPredicate = cb.like(cb.lower(root.get("contactName")), "%" + keyword + "%");
                Predicate phonePredicate = cb.like(root.get("contactPhone"), "%" + keyword + "%");
                Predicate emailPredicate = cb.like(cb.lower(root.get("contactEmail")), "%" + keyword + "%");
                predicates.add(cb.or(namePredicate, companyPredicate, contactPredicate, phonePredicate, emailPredicate));
            }
            
            // 时间范围查询
            if (searchDTO.getCreatedStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), searchDTO.getCreatedStartTime()));
            }
            
            if (searchDTO.getCreatedEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), searchDTO.getCreatedEndTime()));
            }
            
            if (searchDTO.getLastContactStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastContactTime"), searchDTO.getLastContactStartTime()));
            }
            
            if (searchDTO.getLastContactEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastContactTime"), searchDTO.getLastContactEndTime()));
            }
            
            if (searchDTO.getEstimatedCloseStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("estimatedCloseDate"), searchDTO.getEstimatedCloseStartDate()));
            }
            
            if (searchDTO.getEstimatedCloseEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("estimatedCloseDate"), searchDTO.getEstimatedCloseEndDate()));
            }
            
            // 评分范围查询
            if (searchDTO.getMinScore() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("score"), searchDTO.getMinScore()));
            }
            
            if (searchDTO.getMaxScore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("score"), searchDTO.getMaxScore()));
            }
            
            if (searchDTO.getCreatedBy() != null) {
                predicates.add(cb.equal(root.get("createdBy"), searchDTO.getCreatedBy()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return leadRepository.findAll(spec, pageable);
    }
    
    @Override
    public Page<Lead> getAllLeads(Pageable pageable) {
        return leadRepository.findAll(pageable);
    }
    
    @Override
    public Page<Lead> getLeadsByOwner(UUID ownerId, Pageable pageable) {
        return leadRepository.findByOwnerId(ownerId, pageable);
    }
    
    @Override
    public List<Lead> getLeadsByStatus(String status) {
        return leadRepository.findByStatus(status);
    }
    
    @Override
    public List<Lead> getLeadsByPriority(String priority) {
        return leadRepository.findByPriority(priority);
    }
    
    @Override
    public List<Lead> getLeadsBySource(String source) {
        return leadRepository.findBySource(source);
    }
    
    @Override
    public List<Lead> getLeadsToFollow() {
        return leadRepository.findLeadsToFollow(LocalDateTime.now());
    }
    
    @Override
    public List<Lead> getOverdueLeads() {
        return leadRepository.findOverdueLeads(LocalDateTime.now());
    }
    
    @Override
    public List<Lead> getHighScoreLeads(Integer minScore) {
        return leadRepository.findHighScoreLeads(minScore != null ? minScore : 80);
    }
    
    @Override
    public List<Lead> getLeadsClosingSoon(int days) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(days);
        return leadRepository.findLeadsClosingSoon(startDate, endDate);
    }
    
    @Override
    public void assignLead(UUID leadId, UUID ownerId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("线索不存在，ID: " + leadId));
        
        lead.setOwnerId(ownerId);
        lead.setAssignedAt(LocalDateTime.now());
        lead.setUpdatedAt(LocalDateTime.now());
        leadRepository.save(lead);
    }
    
    @Override
    public void batchAssignLeads(List<UUID> leadIds, UUID ownerId) {
        List<Lead> leads = leadRepository.findAllById(leadIds);
        for (Lead lead : leads) {
            lead.setOwnerId(ownerId);
            lead.setAssignedAt(LocalDateTime.now());
            lead.setUpdatedAt(LocalDateTime.now());
        }
        leadRepository.saveAll(leads);
    }
    
    @Override
    public Lead convertToCustomer(UUID leadId, UUID customerId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("线索不存在，ID: " + leadId));
        
        lead.setStatus("CONVERTED");
        lead.setConvertedCustomerId(customerId);
        lead.setConvertedAt(LocalDateTime.now());
        lead.setUpdatedAt(LocalDateTime.now());
        
        return leadRepository.save(lead);
    }
    
    @Override
    public void updateLeadStatus(UUID leadId, String status) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("线索不存在，ID: " + leadId));
        
        lead.setStatus(status);
        lead.setUpdatedAt(LocalDateTime.now());
        leadRepository.save(lead);
    }
    
    @Override
    public void updateLeadScore(UUID leadId, Integer score) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("线索不存在，ID: " + leadId));
        
        lead.setScore(score);
        lead.setUpdatedAt(LocalDateTime.now());
        leadRepository.save(lead);
    }
    
    @Override
    public void updateLastContactTime(UUID leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("线索不存在，ID: " + leadId));
        
        lead.setLastContactTime(LocalDateTime.now());
        lead.setUpdatedAt(LocalDateTime.now());
        leadRepository.save(lead);
    }
    
    @Override
    public LeadFollowUp addFollowUp(UUID leadId, LeadFollowUp followUp) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("线索不存在，ID: " + leadId));
        
        followUp.setLeadId(leadId);
        followUp.setCreatedAt(LocalDateTime.now());
        followUp.setUpdatedAt(LocalDateTime.now());
        followUp.setDeleted(false);
        
        // 更新线索的最后联系时间
        lead.setLastContactTime(followUp.getFollowTime());
        lead.setUpdatedAt(LocalDateTime.now());
        
        // 如果有评分变化，更新线索评分
        if (followUp.getScoreChange() != null) {
            lead.setScore(lead.getScore() + followUp.getScoreChange());
        }
        
        // 如果有状态变化，更新线索状态
        if (StringUtils.hasText(followUp.getStatusChange())) {
            lead.setStatus(followUp.getStatusChange());
        }
        
        // 设置下次跟进时间
        if (followUp.getNextFollowTime() != null) {
            lead.setNextFollowTime(followUp.getNextFollowTime());
        }
        
        leadRepository.save(lead);
        
        // 这里需要保存跟进记录，但需要LeadFollowUpRepository
        // 暂时返回null，实际项目中需要实现
        return null;
    }
    
    @Override
    public List<LeadFollowUp> getLeadFollowUps(UUID leadId) {
        // 这里需要LeadFollowUpRepository来查询跟进记录
        // 暂时返回空列表，实际项目中需要实现
        return new ArrayList<>();
    }
    
    @Override
    public String generateLeadCode() {
        String prefix = "LEAD";
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + timestamp.substring(timestamp.length() - 8);
    }
    
    @Override
    public List<Lead> importLeads(List<LeadDTO> leadDTOs) {
        List<Lead> leads = new ArrayList<>();
        for (LeadDTO dto : leadDTOs) {
            try {
                Lead lead = createLead(dto);
                leads.add(lead);
            } catch (Exception e) {
                // 记录导入失败的数据
                System.err.println("导入线索失败: " + dto.getName() + ", 错误: " + e.getMessage());
            }
        }
        return leads;
    }
    
    @Override
    public List<Lead> exportLeads(LeadSearchDTO searchDTO) {
        // 使用分页查询所有数据
        Pageable pageable = Pageable.unpaged();
        Page<Lead> page = searchLeads(searchDTO, pageable);
        return page.getContent();
    }
    
    @Override
    public LeadStatistics getLeadStatistics(UUID ownerId) {
        LeadStatistics stats = new LeadStatistics();
        
        // 总数量
        if (ownerId != null) {
            stats.setTotalCount(leadRepository.countByOwnerId(ownerId));
        } else {
            stats.setTotalCount(leadRepository.countActiveLeads());
        }
        
        // 按状态统计
        List<Lead> newLeads = leadRepository.findByStatus("NEW");
        stats.setNewCount((long) newLeads.size());
        
        List<Lead> contactedLeads = leadRepository.findByStatus("CONTACTED");
        stats.setContactedCount((long) contactedLeads.size());
        
        List<Lead> qualifiedLeads = leadRepository.findByStatus("QUALIFIED");
        stats.setQualifiedCount((long) qualifiedLeads.size());
        
        List<Lead> unqualifiedLeads = leadRepository.findByStatus("UNQUALIFIED");
        stats.setUnqualifiedCount((long) unqualifiedLeads.size());
        
        List<Lead> convertedLeads = leadRepository.findByStatus("CONVERTED");
        stats.setConvertedCount((long) convertedLeads.size());
        
        // 高优先级线索
        List<Lead> highPriorityLeads = leadRepository.findByPriority("HIGH");
        stats.setHighPriorityCount((long) highPriorityLeads.size());
        
        // 超期线索
        List<Lead> overdueLeads = leadRepository.findOverdueLeads(LocalDateTime.now());
        stats.setOverdueCount((long) overdueLeads.size());
        
        // 需要跟进的线索
        List<Lead> toFollowLeads = leadRepository.findLeadsToFollow(LocalDateTime.now());
        stats.setToFollowCount((long) toFollowLeads.size());
        
        // 转换率
        if (stats.getTotalCount() > 0) {
            double conversionRate = (double) stats.getConvertedCount() / stats.getTotalCount() * 100;
            stats.setConversionRate(conversionRate);
        }
        
        return stats;
    }
    
    @Override
    public List<Lead> checkDuplicates(String name, String companyName, String contactPhone, String contactEmail) {
        List<Lead> duplicates = new ArrayList<>();
        
        if (StringUtils.hasText(name)) {
            Optional<Lead> byName = leadRepository.findByName(name);
            if (byName.isPresent()) {
                duplicates.add(byName.get());
            }
        }
        
        if (StringUtils.hasText(companyName)) {
            List<Lead> byCompany = leadRepository.findByCompanyName(companyName);
            duplicates.addAll(byCompany);
        }
        
        if (StringUtils.hasText(contactPhone)) {
            List<Lead> byPhone = leadRepository.findByContactPhone(contactPhone);
            duplicates.addAll(byPhone);
        }
        
        if (StringUtils.hasText(contactEmail)) {
            List<Lead> byEmail = leadRepository.findByContactEmail(contactEmail);
            duplicates.addAll(byEmail);
        }
        
        return duplicates.stream().distinct().collect(Collectors.toList());
    }
}
