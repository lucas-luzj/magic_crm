package com.magic.crm.service.impl;

import com.magic.crm.dto.SupplierDTO;
import com.magic.crm.dto.SupplierSearchDTO;
import com.magic.crm.entity.Supplier;
import com.magic.crm.entity.SupplierContact;
import com.magic.crm.entity.SupplierProduct;
import com.magic.crm.entity.SupplierEvaluation;
import com.magic.crm.repository.SupplierRepository;
import com.magic.crm.service.SupplierService;
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
 * 供应商服务实现类
 */
@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Override
    public Supplier createSupplier(SupplierDTO supplierDTO) {
        // 检查供应商编码是否已存在
        if (StringUtils.hasText(supplierDTO.getCode()) && supplierRepository.existsByCode(supplierDTO.getCode())) {
            throw new RuntimeException("供应商编码已存在: " + supplierDTO.getCode());
        }
        
        // 检查供应商名称是否已存在
        if (supplierRepository.existsByName(supplierDTO.getName())) {
            throw new RuntimeException("供应商名称已存在: " + supplierDTO.getName());
        }
        
        // 检查统一社会信用代码是否已存在
        if (StringUtils.hasText(supplierDTO.getUscc()) && supplierRepository.existsByUscc(supplierDTO.getUscc())) {
            throw new RuntimeException("统一社会信用代码已存在: " + supplierDTO.getUscc());
        }
        
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierDTO, supplier);
        
        // 生成供应商编码
        if (!StringUtils.hasText(supplier.getCode())) {
            supplier.setCode(generateSupplierCode());
        }
        
        // 设置创建信息
        supplier.setCreatedAt(LocalDateTime.now());
        supplier.setUpdatedAt(LocalDateTime.now());
        supplier.setDeleted(false);
        supplier.setStatus("ACTIVE");
        supplier.setIsBlacklist(false);
        supplier.setIsKeySupplier(false);
        
        return supplierRepository.save(supplier);
    }
    
    @Override
    public Supplier updateSupplier(UUID id, SupplierDTO supplierDTO) {
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("供应商不存在，ID: " + id));
        
        // 检查供应商编码是否已存在（排除自己）
        if (StringUtils.hasText(supplierDTO.getCode()) && 
            !supplierDTO.getCode().equals(supplier.getCode()) && 
            supplierRepository.existsByCode(supplierDTO.getCode())) {
            throw new RuntimeException("供应商编码已存在: " + supplierDTO.getCode());
        }
        
        // 检查供应商名称是否已存在（排除自己）
        if (StringUtils.hasText(supplierDTO.getName()) && 
            !supplierDTO.getName().equals(supplier.getName()) && 
            supplierRepository.existsByName(supplierDTO.getName())) {
            throw new RuntimeException("供应商名称已存在: " + supplierDTO.getName());
        }
        
        // 检查统一社会信用代码是否已存在（排除自己）
        if (StringUtils.hasText(supplierDTO.getUscc()) && 
            !supplierDTO.getUscc().equals(supplier.getUscc()) && 
            supplierRepository.existsByUscc(supplierDTO.getUscc())) {
            throw new RuntimeException("统一社会信用代码已存在: " + supplierDTO.getUscc());
        }
        
        BeanUtils.copyProperties(supplierDTO, supplier, "id", "createdAt", "createdBy");
        supplier.setUpdatedAt(LocalDateTime.now());
        
        return supplierRepository.save(supplier);
    }
    
    @Override
    public Supplier getSupplierById(UUID id) {
        return supplierRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("供应商不存在，ID: " + id));
    }
    
    @Override
    public Supplier getSupplierByCode(String code) {
        return supplierRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("供应商不存在，编码: " + code));
    }
    
    @Override
    public void deleteSupplier(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("供应商不存在，ID: " + id));
        
        supplier.setDeleted(true);
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepository.save(supplier);
    }
    
    @Override
    public void deleteSuppliers(List<UUID> ids) {
        List<Supplier> suppliers = supplierRepository.findAllById(ids);
        for (Supplier supplier : suppliers) {
            supplier.setDeleted(true);
            supplier.setUpdatedAt(LocalDateTime.now());
        }
        supplierRepository.saveAll(suppliers);
    }
    
    @Override
    public Page<Supplier> searchSuppliers(SupplierSearchDTO searchDTO, Pageable pageable) {
        Specification<Supplier> spec = (root, query, cb) -> {
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
            
            if (StringUtils.hasText(searchDTO.getShortName())) {
                predicates.add(cb.like(cb.lower(root.get("shortName")), 
                    "%" + searchDTO.getShortName().toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(searchDTO.getUscc())) {
                predicates.add(cb.like(cb.lower(root.get("uscc")), 
                    "%" + searchDTO.getUscc().toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(searchDTO.getIndustry())) {
                predicates.add(cb.equal(root.get("industry"), searchDTO.getIndustry()));
            }
            
            if (StringUtils.hasText(searchDTO.getRegion())) {
                predicates.add(cb.equal(root.get("region"), searchDTO.getRegion()));
            }
            
            if (StringUtils.hasText(searchDTO.getSupplierLevel())) {
                predicates.add(cb.equal(root.get("supplierLevel"), searchDTO.getSupplierLevel()));
            }
            
            if (StringUtils.hasText(searchDTO.getSupplierType())) {
                predicates.add(cb.equal(root.get("supplierType"), searchDTO.getSupplierType()));
            }
            
            if (StringUtils.hasText(searchDTO.getSource())) {
                predicates.add(cb.equal(root.get("source"), searchDTO.getSource()));
            }
            
            if (StringUtils.hasText(searchDTO.getStatus())) {
                predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
            }
            
            if (searchDTO.getIsBlacklist() != null) {
                predicates.add(cb.equal(root.get("isBlacklist"), searchDTO.getIsBlacklist()));
            }
            
            if (searchDTO.getIsKeySupplier() != null) {
                predicates.add(cb.equal(root.get("isKeySupplier"), searchDTO.getIsKeySupplier()));
            }
            
            // 关键词搜索
            if (StringUtils.hasText(searchDTO.getKeyword())) {
                String keyword = searchDTO.getKeyword().toLowerCase();
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), "%" + keyword + "%");
                Predicate codePredicate = cb.like(cb.lower(root.get("code")), "%" + keyword + "%");
                Predicate shortNamePredicate = cb.like(cb.lower(root.get("shortName")), "%" + keyword + "%");
                predicates.add(cb.or(namePredicate, codePredicate, shortNamePredicate));
            }
            
            // 时间范围查询
            if (searchDTO.getCreatedStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), searchDTO.getCreatedStartTime()));
            }
            
            if (searchDTO.getCreatedEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), searchDTO.getCreatedEndTime()));
            }
            
            if (searchDTO.getContractStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("contractStartDate"), searchDTO.getContractStartDate()));
            }
            
            if (searchDTO.getContractEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("contractEndDate"), searchDTO.getContractEndDate()));
            }
            
            // 评级范围查询
            if (searchDTO.getMinRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("overallRating"), searchDTO.getMinRating()));
            }
            
            if (searchDTO.getMaxRating() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("overallRating"), searchDTO.getMaxRating()));
            }
            
            if (searchDTO.getCreatedBy() != null) {
                predicates.add(cb.equal(root.get("createdBy"), searchDTO.getCreatedBy()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return supplierRepository.findAll(spec, pageable);
    }
    
    @Override
    public Page<Supplier> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable);
    }
    
    @Override
    public List<Supplier> getSuppliersByIndustry(String industry) {
        return supplierRepository.findByIndustry(industry);
    }
    
    @Override
    public List<Supplier> getSuppliersByRegion(String region) {
        return supplierRepository.findByRegion(region);
    }
    
    @Override
    public List<Supplier> getSuppliersByLevel(String level) {
        return supplierRepository.findBySupplierLevel(level);
    }
    
    @Override
    public List<Supplier> getSuppliersByType(String type) {
        return supplierRepository.findBySupplierType(type);
    }
    
    @Override
    public List<Supplier> getSuppliersByStatus(String status) {
        return supplierRepository.findByStatus(status);
    }
    
    @Override
    public List<Supplier> getBlacklistSuppliers() {
        return supplierRepository.findByIsBlacklistTrue();
    }
    
    @Override
    public List<Supplier> getKeySuppliers() {
        return supplierRepository.findByIsKeySupplierTrue();
    }
    
    @Override
    public List<Supplier> getExpiringContracts(int days) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(days);
        return supplierRepository.findExpiringContracts(startDate, endDate);
    }
    
    @Override
    public List<Supplier> getExpiredContracts() {
        return supplierRepository.findExpiredContracts(LocalDateTime.now());
    }
    
    @Override
    public List<Supplier> getHighRatedSuppliers(Integer minRating) {
        return supplierRepository.findHighRatedSuppliers(minRating != null ? minRating : 4);
    }
    
    @Override
    public List<Supplier> getSuppliersNeedingEvaluation() {
        LocalDateTime beforeDate = LocalDateTime.now().minusMonths(6); // 6个月前
        return supplierRepository.findSuppliersNeedingEvaluation(beforeDate);
    }
    
    @Override
    public SupplierContact addContact(UUID supplierId, SupplierContact contact) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new RuntimeException("供应商不存在，ID: " + supplierId));
        
        contact.setSupplierId(supplierId);
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());
        contact.setDeleted(false);
        
        // 这里需要SupplierContactRepository来保存联系人
        // 暂时返回null，实际项目中需要实现
        return null;
    }
    
    @Override
    public SupplierContact updateContact(UUID contactId, SupplierContact contact) {
        // 这里需要SupplierContactRepository来更新联系人
        // 暂时返回null，实际项目中需要实现
        return null;
    }
    
    @Override
    public void deleteContact(UUID contactId) {
        // 这里需要SupplierContactRepository来删除联系人
        // 实际项目中需要实现
    }
    
    @Override
    public List<SupplierContact> getSupplierContacts(UUID supplierId) {
        // 这里需要SupplierContactRepository来查询联系人
        // 暂时返回空列表，实际项目中需要实现
        return new ArrayList<>();
    }
    
    @Override
    public SupplierProduct addSupplierProduct(UUID supplierId, SupplierProduct supplierProduct) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new RuntimeException("供应商不存在，ID: " + supplierId));
        
        supplierProduct.setSupplierId(supplierId);
        supplierProduct.setCreatedAt(LocalDateTime.now());
        supplierProduct.setUpdatedAt(LocalDateTime.now());
        supplierProduct.setDeleted(false);
        
        // 这里需要SupplierProductRepository来保存供应商产品
        // 暂时返回null，实际项目中需要实现
        return null;
    }
    
    @Override
    public SupplierProduct updateSupplierProduct(UUID supplierProductId, SupplierProduct supplierProduct) {
        // 这里需要SupplierProductRepository来更新供应商产品
        // 暂时返回null，实际项目中需要实现
        return null;
    }
    
    @Override
    public void deleteSupplierProduct(UUID supplierProductId) {
        // 这里需要SupplierProductRepository来删除供应商产品
        // 实际项目中需要实现
    }
    
    @Override
    public List<SupplierProduct> getSupplierProducts(UUID supplierId) {
        // 这里需要SupplierProductRepository来查询供应商产品
        // 暂时返回空列表，实际项目中需要实现
        return new ArrayList<>();
    }
    
    @Override
    public SupplierEvaluation addEvaluation(UUID supplierId, SupplierEvaluation evaluation) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new RuntimeException("供应商不存在，ID: " + supplierId));
        
        evaluation.setSupplierId(supplierId);
        evaluation.setCreatedAt(LocalDateTime.now());
        evaluation.setUpdatedAt(LocalDateTime.now());
        evaluation.setDeleted(false);
        
        // 这里需要SupplierEvaluationRepository来保存评价
        // 暂时返回null，实际项目中需要实现
        return null;
    }
    
    @Override
    public List<SupplierEvaluation> getSupplierEvaluations(UUID supplierId) {
        // 这里需要SupplierEvaluationRepository来查询评价
        // 暂时返回空列表，实际项目中需要实现
        return new ArrayList<>();
    }
    
    @Override
    public void updateSupplierRatings(UUID supplierId, Integer qualityRating, Integer serviceRating, 
                                     Integer priceRating, Integer overallRating) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new RuntimeException("供应商不存在，ID: " + supplierId));
        
        supplier.setQualityRating(qualityRating);
        supplier.setServiceRating(serviceRating);
        supplier.setPriceRating(priceRating);
        supplier.setOverallRating(overallRating);
        supplier.setLastEvaluationDate(LocalDateTime.now());
        supplier.setUpdatedAt(LocalDateTime.now());
        
        supplierRepository.save(supplier);
    }
    
    @Override
    public void setBlacklist(UUID supplierId, Boolean isBlacklist) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new RuntimeException("供应商不存在，ID: " + supplierId));
        
        supplier.setIsBlacklist(isBlacklist);
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepository.save(supplier);
    }
    
    @Override
    public void setKeySupplier(UUID supplierId, Boolean isKeySupplier) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new RuntimeException("供应商不存在，ID: " + supplierId));
        
        supplier.setIsKeySupplier(isKeySupplier);
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepository.save(supplier);
    }
    
    @Override
    public String generateSupplierCode() {
        String prefix = "SUPP";
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + timestamp.substring(timestamp.length() - 8);
    }
    
    @Override
    public List<Supplier> importSuppliers(List<SupplierDTO> supplierDTOs) {
        List<Supplier> suppliers = new ArrayList<>();
        for (SupplierDTO dto : supplierDTOs) {
            try {
                Supplier supplier = createSupplier(dto);
                suppliers.add(supplier);
            } catch (Exception e) {
                // 记录导入失败的数据
                System.err.println("导入供应商失败: " + dto.getName() + ", 错误: " + e.getMessage());
            }
        }
        return suppliers;
    }
    
    @Override
    public List<Supplier> exportSuppliers(SupplierSearchDTO searchDTO) {
        // 使用分页查询所有数据
        Pageable pageable = Pageable.unpaged();
        Page<Supplier> page = searchSuppliers(searchDTO, pageable);
        return page.getContent();
    }
    
    @Override
    public SupplierStatistics getSupplierStatistics() {
        SupplierStatistics stats = new SupplierStatistics();
        
        // 总数量
        stats.setTotalCount(supplierRepository.countActiveSuppliers());
        
        // 按状态统计
        List<Supplier> activeSuppliers = supplierRepository.findByStatus("ACTIVE");
        stats.setActiveCount((long) activeSuppliers.size());
        
        List<Supplier> inactiveSuppliers = supplierRepository.findByStatus("INACTIVE");
        stats.setInactiveCount((long) inactiveSuppliers.size());
        
        List<Supplier> suspendedSuppliers = supplierRepository.findByStatus("SUSPENDED");
        stats.setSuspendedCount((long) suspendedSuppliers.size());
        
        // 黑名单和重点供应商
        List<Supplier> blacklistSuppliers = supplierRepository.findByIsBlacklistTrue();
        stats.setBlacklistCount((long) blacklistSuppliers.size());
        
        List<Supplier> keySuppliers = supplierRepository.findByIsKeySupplierTrue();
        stats.setKeySupplierCount((long) keySuppliers.size());
        
        // 合同相关
        List<Supplier> expiringContracts = supplierRepository.findExpiringContracts(
            LocalDateTime.now(), LocalDateTime.now().plusDays(30));
        stats.setExpiringContractsCount((long) expiringContracts.size());
        
        List<Supplier> expiredContracts = supplierRepository.findExpiredContracts(LocalDateTime.now());
        stats.setExpiredContractsCount((long) expiredContracts.size());
        
        // 高评级供应商
        List<Supplier> highRatedSuppliers = supplierRepository.findHighRatedSuppliers(4);
        stats.setHighRatedCount((long) highRatedSuppliers.size());
        
        // 需要评价的供应商
        List<Supplier> needEvaluationSuppliers = supplierRepository.findSuppliersNeedingEvaluation(
            LocalDateTime.now().minusMonths(6));
        stats.setNeedEvaluationCount((long) needEvaluationSuppliers.size());
        
        // 计算平均评级
        if (stats.getTotalCount() > 0) {
            double totalRating = activeSuppliers.stream()
                .filter(s -> s.getOverallRating() != null)
                .mapToInt(Supplier::getOverallRating)
                .sum();
            long ratedCount = activeSuppliers.stream()
                .filter(s -> s.getOverallRating() != null)
                .count();
            if (ratedCount > 0) {
                stats.setAverageRating(totalRating / ratedCount);
            }
        }
        
        return stats;
    }
    
    @Override
    public List<Supplier> checkDuplicates(String name, String uscc, String code) {
        List<Supplier> duplicates = new ArrayList<>();
        
        if (StringUtils.hasText(name)) {
            Optional<Supplier> byName = supplierRepository.findByName(name);
            if (byName.isPresent()) {
                duplicates.add(byName.get());
            }
        }
        
        if (StringUtils.hasText(uscc)) {
            Optional<Supplier> byUscc = supplierRepository.findByUscc(uscc);
            if (byUscc.isPresent()) {
                duplicates.add(byUscc.get());
            }
        }
        
        if (StringUtils.hasText(code)) {
            Optional<Supplier> byCode = supplierRepository.findByCode(code);
            if (byCode.isPresent()) {
                duplicates.add(byCode.get());
            }
        }
        
        return duplicates.stream().distinct().collect(Collectors.toList());
    }
}
