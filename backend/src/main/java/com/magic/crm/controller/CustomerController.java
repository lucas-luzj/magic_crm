package com.magic.crm.controller;

import com.magic.crm.dto.CustomerDTO;
import com.magic.crm.dto.CustomerPoolDTO;
import com.magic.crm.dto.CustomerSearchDTO;
import com.magic.crm.entity.Customer;
import com.magic.crm.service.CustomerService;
import com.magic.crm.service.CustomerService.CustomerStatistics;
import com.magic.crm.util.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * 客户管理控制器
 */
@RestController
@RequestMapping("/api/customers")
@Api(tags = "客户管理")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @PostMapping
    @ApiOperation("创建客户")
    @PreAuthorize("hasAuthority('customer:create')")
    public ApiResponse<Customer> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        Customer customer = customerService.createCustomer(customerDTO);
        return ApiResponse.success(customer, "客户创建成功");
    }
    
    @PutMapping("/{id}")
    @ApiOperation("更新客户")
    @PreAuthorize("hasAuthority('customer:update')")
    public ApiResponse<Customer> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerDTO customerDTO) {
        Customer customer = customerService.updateCustomer(id, customerDTO);
        return ApiResponse.success(customer, "客户更新成功");
    }
    
    @GetMapping("/{id}")
    @ApiOperation("获取客户详情")
    @PreAuthorize("hasAuthority('customer:view')")
    public ApiResponse<Customer> getCustomer(@PathVariable UUID id) {
        Customer customer = customerService.getCustomerById(id);
        return ApiResponse.success(customer);
    }
    
    @GetMapping("/code/{code}")
    @ApiOperation("根据编号获取客户")
    @PreAuthorize("hasAuthority('customer:view')")
    public ApiResponse<Customer> getCustomerByCode(@PathVariable String code) {
        Customer customer = customerService.getCustomerByCode(code);
        return ApiResponse.success(customer);
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation("删除客户")
    @PreAuthorize("hasAuthority('customer:delete')")
    public ApiResponse<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ApiResponse.success(null, "客户删除成功");
    }
    
    @DeleteMapping("/batch")
    @ApiOperation("批量删除客户")
    @PreAuthorize("hasAuthority('customer:delete')")
    public ApiResponse<Void> deleteCustomers(@RequestBody List<UUID> ids) {
        customerService.deleteCustomers(ids);
        return ApiResponse.success(null, "客户批量删除成功");
    }
    
    @PostMapping("/search")
    @ApiOperation("搜索客户")
    @PreAuthorize("hasAuthority('customer:view')")
    public ApiResponse<Page<Customer>> searchCustomers(
            @RequestBody CustomerSearchDTO searchDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Customer> customers = customerService.searchCustomers(searchDTO, pageable);
        return ApiResponse.success(customers);
    }
    
    @GetMapping("/public-pool")
    @ApiOperation("获取公海池客户")
    @PreAuthorize("hasAuthority('customer:view')")
    public ApiResponse<Page<Customer>> getPublicPoolCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "poolEntryTime"));
        Page<Customer> customers = customerService.getPublicPoolCustomers(pageable);
        return ApiResponse.success(customers);
    }
    
    @GetMapping("/private")
    @ApiOperation("获取私海客户")
    @PreAuthorize("hasAuthority('customer:view')")
    public ApiResponse<Page<Customer>> getPrivateCustomers(
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Customer> customers = customerService.getPrivateCustomers(ownerId, pageable);
        return ApiResponse.success(customers);
    }
    
    @PostMapping("/move-to-pool")
    @ApiOperation("将客户移入公海池")
    @PreAuthorize("hasAuthority('customer:pool:move')")
    public ApiResponse<Void> moveToPublicPool(@Valid @RequestBody CustomerPoolDTO poolDTO) {
        customerService.moveToPublicPool(poolDTO);
        return ApiResponse.success(null, "客户已移入公海池");
    }
    
    @PostMapping("/batch-move-to-pool")
    @ApiOperation("批量将客户移入公海池")
    @PreAuthorize("hasAuthority('customer:pool:move')")
    public ApiResponse<Void> batchMoveToPublicPool(
            @RequestBody List<UUID> customerIds,
            @RequestParam String reason) {
        customerService.batchMoveToPublicPool(customerIds, reason);
        return ApiResponse.success(null, "客户已批量移入公海池");
    }
    
    @PostMapping("/claim/{customerId}")
    @ApiOperation("认领公海客户")
    @PreAuthorize("hasAuthority('customer:pool:claim')")
    public ApiResponse<Void> claimFromPublicPool(
            @PathVariable UUID customerId,
            @RequestParam UUID ownerId) {
        customerService.claimFromPublicPool(customerId, ownerId);
        return ApiResponse.success(null, "客户认领成功");
    }
    
    @PostMapping("/batch-claim")
    @ApiOperation("批量认领公海客户")
    @PreAuthorize("hasAuthority('customer:pool:claim')")
    public ApiResponse<Void> batchClaimFromPublicPool(
            @RequestBody List<UUID> customerIds,
            @RequestParam UUID ownerId) {
        customerService.batchClaimFromPublicPool(customerIds, ownerId);
        return ApiResponse.success(null, "客户批量认领成功");
    }
    
    @PostMapping("/assign/{customerId}")
    @ApiOperation("分配客户")
    @PreAuthorize("hasAuthority('customer:assign')")
    public ApiResponse<Void> assignCustomer(
            @PathVariable UUID customerId,
            @RequestParam UUID newOwnerId) {
        customerService.assignCustomer(customerId, newOwnerId);
        return ApiResponse.success(null, "客户分配成功");
    }
    
    @PostMapping("/batch-assign")
    @ApiOperation("批量分配客户")
    @PreAuthorize("hasAuthority('customer:assign')")
    public ApiResponse<Void> batchAssignCustomers(
            @RequestBody List<UUID> customerIds,
            @RequestParam UUID newOwnerId) {
        customerService.batchAssignCustomers(customerIds, newOwnerId);
        return ApiResponse.success(null, "客户批量分配成功");
    }
    
    @PostMapping("/transfer/{customerId}")
    @ApiOperation("转移客户")
    @PreAuthorize("hasAuthority('customer:transfer')")
    public ApiResponse<Void> transferCustomer(
            @PathVariable UUID customerId,
            @RequestParam UUID fromOwnerId,
            @RequestParam UUID toOwnerId) {
        customerService.transferCustomer(customerId, fromOwnerId, toOwnerId);
        return ApiResponse.success(null, "客户转移成功");
    }
    
    @PostMapping("/share/{customerId}")
    @ApiOperation("共享客户")
    @PreAuthorize("hasAuthority('customer:share')")
    public ApiResponse<Void> shareCustomer(
            @PathVariable UUID customerId,
            @RequestBody List<UUID> collaboratorIds) {
        customerService.shareCustomer(customerId, collaboratorIds);
        return ApiResponse.success(null, "客户共享成功");
    }
    
    @PostMapping("/merge")
    @ApiOperation("合并客户")
    @PreAuthorize("hasAuthority('customer:merge')")
    public ApiResponse<Void> mergeCustomers(
            @RequestParam UUID primaryCustomerId,
            @RequestBody List<UUID> mergeCustomerIds) {
        customerService.mergeCustomers(primaryCustomerId, mergeCustomerIds);
        return ApiResponse.success(null, "客户合并成功");
    }
    
    @GetMapping("/check-duplicates")
    @ApiOperation("检查客户重复")
    @PreAuthorize("hasAuthority('customer:view')")
    public ApiResponse<List<Customer>> checkDuplicates(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String uscc,
            @RequestParam(required = false) String region) {
        List<Customer> duplicates = customerService.checkDuplicates(name, uscc, region);
        return ApiResponse.success(duplicates);
    }
    
    @PostMapping("/auto-move-to-pool")
    @ApiOperation("自动将客户移入公海池")
    @PreAuthorize("hasAuthority('customer:pool:auto')")
    public ApiResponse<Void> autoMoveToPublicPool() {
        customerService.autoMoveToPublicPool();
        return ApiResponse.success(null, "自动移入公海池执行成功");
    }
    
    @GetMapping("/generate-code")
    @ApiOperation("生成客户编号")
    @PreAuthorize("hasAuthority('customer:create')")
    public ApiResponse<String> generateCustomerCode() {
        String code = customerService.generateCustomerCode();
        return ApiResponse.success(code);
    }
    
    @PostMapping("/import")
    @ApiOperation("导入客户")
    @PreAuthorize("hasAuthority('customer:import')")
    public ApiResponse<List<Customer>> importCustomers(@RequestBody List<CustomerDTO> customerDTOs) {
        List<Customer> customers = customerService.importCustomers(customerDTOs);
        return ApiResponse.success(customers, "客户导入成功");
    }
    
    @PostMapping("/export")
    @ApiOperation("导出客户")
    @PreAuthorize("hasAuthority('customer:export')")
    public ApiResponse<List<Customer>> exportCustomers(@RequestBody CustomerSearchDTO searchDTO) {
        List<Customer> customers = customerService.exportCustomers(searchDTO);
        return ApiResponse.success(customers);
    }
    
    @GetMapping("/statistics")
    @ApiOperation("获取客户统计信息")
    @PreAuthorize("hasAuthority('customer:view')")
    public ApiResponse<CustomerStatistics> getCustomerStatistics(
            @RequestParam(required = false) UUID ownerId) {
        CustomerStatistics statistics = customerService.getCustomerStatistics(ownerId);
        return ApiResponse.success(statistics);
    }
    
    @GetMapping("/{parentId}/subsidiaries")
    @ApiOperation("获取客户的子公司")
    @PreAuthorize("hasAuthority('customer:view')")
    public ApiResponse<List<Customer>> getSubsidiaries(@PathVariable UUID parentId) {
        List<Customer> subsidiaries = customerService.getSubsidiaries(parentId);
        return ApiResponse.success(subsidiaries);
    }
    
    @PutMapping("/{customerId}/key-customer")
    @ApiOperation("设置为重点客户")
    @PreAuthorize("hasAuthority('customer:update')")
    public ApiResponse<Void> setAsKeyCustomer(
            @PathVariable UUID customerId,
            @RequestParam boolean isKey) {
        customerService.setAsKeyCustomer(customerId, isKey);
        return ApiResponse.success(null, isKey ? "已设置为重点客户" : "已取消重点客户");
    }
    
    @PutMapping("/{customerId}/blacklist")
    @ApiOperation("设置为黑名单")
    @PreAuthorize("hasAuthority('customer:update')")
    public ApiResponse<Void> setAsBlacklist(
            @PathVariable UUID customerId,
            @RequestParam boolean isBlacklist) {
        customerService.setAsBlacklist(customerId, isBlacklist);
        return ApiResponse.success(null, isBlacklist ? "已加入黑名单" : "已移出黑名单");
    }
}