package com.magic.crm.service.impl;

import com.magic.crm.dto.CustomerDTO;
import com.magic.crm.dto.CustomerPoolDTO;
import com.magic.crm.dto.CustomerSearchDTO;
import com.magic.crm.entity.Customer;
import com.magic.crm.entity.User;
import com.magic.crm.repository.CustomerRepository;
import com.magic.crm.repository.UserRepository;
import com.magic.crm.service.CustomerService;
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
 * 客户服务实现类
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public Customer createCustomer(CustomerDTO customerDTO) {
        // 检查客户是否已存在
        if (StringUtils.hasText(customerDTO.getUscc())) {
            Optional<Customer> existingCustomer = customerRepository.findByNameAndUsccAndRegion(
                customerDTO.getName(), customerDTO.getUscc(), customerDTO.getRegion());
            if (existingCustomer.isPresent()) {
                throw new RuntimeException("客户已存在：名称=" + customerDTO.getName() + 
                    ", 统一社会信用代码=" + customerDTO.getUscc() + ", 地区=" + customerDTO.getRegion());
            }
        }
        
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        
        // 生成客户编号
        customer.setCode(generateCustomerCode());
        
        // 设置创建信息
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setDeleted(false);
        customer.setIsKeyCustomer(false);
        customer.setIsBlacklist(false);
        customer.setIsPublicPool(false);
        customer.setStatus("ACTIVE");
        
        return customerRepository.save(customer);
    }
    
    @Override
    public Customer updateCustomer(UUID id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("客户不存在，ID: " + id));
        
        // 检查客户是否已存在（排除自己）
        if (StringUtils.hasText(customerDTO.getUscc())) {
            Optional<Customer> existingCustomer = customerRepository.findByNameAndUsccAndRegion(
                customerDTO.getName(), customerDTO.getUscc(), customerDTO.getRegion());
            if (existingCustomer.isPresent() && !existingCustomer.get().getId().equals(id)) {
                throw new RuntimeException("客户已存在：名称=" + customerDTO.getName() + 
                    ", 统一社会信用代码=" + customerDTO.getUscc() + ", 地区=" + customerDTO.getRegion());
            }
        }
        
        BeanUtils.copyProperties(customerDTO, customer, "id", "code", "createdAt", "createdBy");
        customer.setUpdatedAt(LocalDateTime.now());
        
        return customerRepository.save(customer);
    }
    
    @Override
    public Customer getCustomerById(UUID id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("客户不存在，ID: " + id));
    }
    
    @Override
    public Customer getCustomerByCode(String code) {
        return customerRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("客户不存在，编号: " + code));
    }
    
    @Override
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("客户不存在，ID: " + id));
        
        customer.setDeleted(true);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }
    
    @Override
    public void deleteCustomers(List<UUID> ids) {
        List<Customer> customers = customerRepository.findAllById(ids);
        for (Customer customer : customers) {
            customer.setDeleted(true);
            customer.setUpdatedAt(LocalDateTime.now());
        }
        customerRepository.saveAll(customers);
    }
    
    @Override
    public Page<Customer> searchCustomers(CustomerSearchDTO searchDTO, Pageable pageable) {
        Specification<Customer> spec = (root, query, cb) -> {
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
            
            if (StringUtils.hasText(searchDTO.getCustomerLevel())) {
                predicates.add(cb.equal(root.get("customerLevel"), searchDTO.getCustomerLevel()));
            }
            
            if (StringUtils.hasText(searchDTO.getCustomerType())) {
                predicates.add(cb.equal(root.get("customerType"), searchDTO.getCustomerType()));
            }
            
            if (StringUtils.hasText(searchDTO.getSource())) {
                predicates.add(cb.equal(root.get("source"), searchDTO.getSource()));
            }
            
            if (searchDTO.getOwnerId() != null) {
                predicates.add(cb.equal(root.get("ownerId"), searchDTO.getOwnerId()));
            }
            
            if (searchDTO.getIsKeyCustomer() != null) {
                predicates.add(cb.equal(root.get("isKeyCustomer"), searchDTO.getIsKeyCustomer()));
            }
            
            if (searchDTO.getIsBlacklist() != null) {
                predicates.add(cb.equal(root.get("isBlacklist"), searchDTO.getIsBlacklist()));
            }
            
            if (searchDTO.getIsPublicPool() != null) {
                predicates.add(cb.equal(root.get("isPublicPool"), searchDTO.getIsPublicPool()));
            }
            
            if (StringUtils.hasText(searchDTO.getStatus())) {
                predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
            }
            
            // 时间范围查询
            if (searchDTO.getCreatedAtStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), searchDTO.getCreatedAtStart()));
            }
            
            if (searchDTO.getCreatedAtEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), searchDTO.getCreatedAtEnd()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return customerRepository.findAll(spec, pageable);
    }
    
    @Override
    public Page<Customer> getPublicPoolCustomers(Pageable pageable) {
        return customerRepository.findByIsPublicPoolTrue(pageable);
    }
    
    @Override
    public Page<Customer> getPrivateCustomers(UUID ownerId, Pageable pageable) {
        return customerRepository.findByIsPublicPoolFalseAndOwnerId(ownerId, pageable);
    }
    
    @Override
    public void moveToPublicPool(CustomerPoolDTO poolDTO) {
        if (poolDTO.getCustomerIds() == null || poolDTO.getCustomerIds().isEmpty()) {
            throw new RuntimeException("客户ID列表不能为空");
        }
        
        List<Customer> customers = customerRepository.findAllById(poolDTO.getCustomerIds());
        for (Customer customer : customers) {
            customer.setIsPublicPool(true);
            customer.setOwnerId(null);
            customer.setPoolEntryTime(LocalDateTime.now());
            customer.setPoolEntryReason(poolDTO.getReason());
            customer.setUpdatedAt(LocalDateTime.now());
        }
        customerRepository.saveAll(customers);
    }
    
    @Override
    public void batchMoveToPublicPool(List<UUID> customerIds, String reason) {
        List<Customer> customers = customerRepository.findAllById(customerIds);
        for (Customer customer : customers) {
            customer.setIsPublicPool(true);
            customer.setOwnerId(null);
            customer.setUpdatedAt(LocalDateTime.now());
        }
        customerRepository.saveAll(customers);
    }
    
    @Override
    public void claimFromPublicPool(UUID customerId, UUID ownerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("客户不存在，ID: " + customerId));
        
        if (!customer.getIsPublicPool()) {
            throw new RuntimeException("客户不在公海池中，ID: " + customerId);
        }
        
        customer.setIsPublicPool(false);
        customer.setOwnerId(ownerId);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }
    
    @Override
    public void batchClaimFromPublicPool(List<UUID> customerIds, UUID ownerId) {
        List<Customer> customers = customerRepository.findAllById(customerIds);
        for (Customer customer : customers) {
            if (customer.getIsPublicPool()) {
                customer.setIsPublicPool(false);
                customer.setOwnerId(ownerId);
                customer.setUpdatedAt(LocalDateTime.now());
            }
        }
        customerRepository.saveAll(customers);
    }
    
    @Override
    public void assignCustomer(UUID customerId, UUID newOwnerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("客户不存在，ID: " + customerId));
        
        customer.setOwnerId(newOwnerId);
        customer.setIsPublicPool(false);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }
    
    @Override
    public void batchAssignCustomers(List<UUID> customerIds, UUID newOwnerId) {
        List<Customer> customers = customerRepository.findAllById(customerIds);
        for (Customer customer : customers) {
            customer.setOwnerId(newOwnerId);
            customer.setIsPublicPool(false);
            customer.setUpdatedAt(LocalDateTime.now());
        }
        customerRepository.saveAll(customers);
    }
    
    @Override
    public void transferCustomer(UUID customerId, UUID fromOwnerId, UUID toOwnerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("客户不存在，ID: " + customerId));
        
        if (!customer.getOwnerId().equals(fromOwnerId)) {
            throw new RuntimeException("客户不属于指定用户，ID: " + customerId);
        }
        
        customer.setOwnerId(toOwnerId);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }
    
    @Override
    public void shareCustomer(UUID customerId, List<UUID> collaboratorIds) {
        // 这里可以实现客户共享逻辑，比如创建共享记录
        // 暂时简单实现，实际项目中需要创建共享表
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("客户不存在，ID: " + customerId));
        
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }
    
    @Override
    public void mergeCustomers(UUID primaryCustomerId, List<UUID> mergeCustomerIds) {
        Customer primaryCustomer = customerRepository.findById(primaryCustomerId)
            .orElseThrow(() -> new RuntimeException("主客户不存在，ID: " + primaryCustomerId));
        
        List<Customer> mergeCustomers = customerRepository.findAllById(mergeCustomerIds);
        
        // 合并逻辑：将合并客户的相关数据转移到主客户
        for (Customer mergeCustomer : mergeCustomers) {
            if (!mergeCustomer.getId().equals(primaryCustomerId)) {
                // 这里可以实现具体的合并逻辑
                // 比如合并联系人、跟进记录等
                mergeCustomer.setDeleted(true);
                mergeCustomer.setUpdatedAt(LocalDateTime.now());
            }
        }
        
        customerRepository.saveAll(mergeCustomers);
        primaryCustomer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(primaryCustomer);
    }
    
    @Override
    public List<Customer> checkDuplicates(String name, String uscc, String region) {
        List<Customer> duplicates = new ArrayList<>();
        
        if (StringUtils.hasText(name)) {
            Optional<Customer> byName = customerRepository.findByName(name);
            if (byName.isPresent()) {
                duplicates.add(byName.get());
            }
        }
        
        if (StringUtils.hasText(uscc)) {
            Optional<Customer> byUscc = customerRepository.findByUscc(uscc);
            if (byUscc.isPresent()) {
                duplicates.add(byUscc.get());
            }
        }
        
        if (StringUtils.hasText(name) && StringUtils.hasText(uscc) && StringUtils.hasText(region)) {
            Optional<Customer> byNameAndUsccAndRegion = customerRepository.findByNameAndUsccAndRegion(name, uscc, region);
            if (byNameAndUsccAndRegion.isPresent()) {
                duplicates.add(byNameAndUsccAndRegion.get());
            }
        }
        
        return duplicates.stream().distinct().collect(Collectors.toList());
    }
    
    @Override
    public List<Customer> getCustomersToMoveToPool(int daysWithoutFollow, int daysWithoutOrder) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(Math.max(daysWithoutFollow, daysWithoutOrder));
        return customerRepository.findCustomersToMoveToPool(cutoffTime);
    }
    
    @Override
    public void autoMoveToPublicPool() {
        List<Customer> customersToMove = getCustomersToMoveToPool(30, 90); // 30天未跟进或90天未成单
        for (Customer customer : customersToMove) {
            customer.setIsPublicPool(true);
            customer.setOwnerId(null);
            customer.setUpdatedAt(LocalDateTime.now());
        }
        customerRepository.saveAll(customersToMove);
    }
    
    @Override
    public String generateCustomerCode() {
        String prefix = "CUST";
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + timestamp.substring(timestamp.length() - 8);
    }
    
    @Override
    public List<Customer> importCustomers(List<CustomerDTO> customerDTOs) {
        List<Customer> customers = new ArrayList<>();
        for (CustomerDTO dto : customerDTOs) {
            try {
                Customer customer = createCustomer(dto);
                customers.add(customer);
            } catch (Exception e) {
                // 记录导入失败的数据
                System.err.println("导入客户失败: " + dto.getName() + ", 错误: " + e.getMessage());
            }
        }
        return customers;
    }
    
    @Override
    public List<Customer> exportCustomers(CustomerSearchDTO searchDTO) {
        // 使用分页查询所有数据
        Pageable pageable = Pageable.unpaged();
        Page<Customer> page = searchCustomers(searchDTO, pageable);
        return page.getContent();
    }
    
    @Override
    public CustomerStatistics getCustomerStatistics(UUID ownerId) {
        CustomerStatistics stats = new CustomerStatistics();
        
        if (ownerId != null) {
            // 私海统计
            Page<Customer> privateCustomers = getPrivateCustomers(ownerId, Pageable.unpaged());
            stats.setPrivateCount((long) privateCustomers.getContent().size());
            
            // 重点客户统计
            long keyCustomerCount = privateCustomers.getContent().stream()
                .mapToLong(c -> c.getIsKeyCustomer() ? 1 : 0)
                .sum();
            stats.setKeyCustomerCount(keyCustomerCount);
            
            // 黑名单统计
            long blacklistCount = privateCustomers.getContent().stream()
                .mapToLong(c -> c.getIsBlacklist() ? 1 : 0)
                .sum();
            stats.setBlacklistCount(blacklistCount);
        }
        
        // 公海统计
        Page<Customer> publicCustomers = getPublicPoolCustomers(Pageable.unpaged());
        stats.setPublicPoolCount((long) publicCustomers.getContent().size());
        
        // 总统计
        stats.setTotalCount(stats.getPrivateCount() + stats.getPublicPoolCount());
        
        return stats;
    }
    
    @Override
    public List<Customer> getSubsidiaries(UUID parentCustomerId) {
        return customerRepository.findByParentCustomerId(parentCustomerId);
    }
    
    @Override
    public void setAsKeyCustomer(UUID customerId, boolean isKey) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("客户不存在，ID: " + customerId));
        
        customer.setIsKeyCustomer(isKey);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }
    
    @Override
    public void setAsBlacklist(UUID customerId, boolean isBlacklist) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("客户不存在，ID: " + customerId));
        
        customer.setIsBlacklist(isBlacklist);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }
    
    @Override
    public void updateLastFollowTime(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("客户不存在，ID: " + customerId));
        
        customer.setLastFollowTime(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }
    
    @Override
    public void updateLastOrderTime(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("客户不存在，ID: " + customerId));
        
        customer.setLastOrderTime(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }
}
