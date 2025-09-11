package com.magic.crm.controller;

import com.magic.crm.dto.SupplierDTO;
import com.magic.crm.dto.SupplierSearchDTO;
import com.magic.crm.entity.Supplier;
import com.magic.crm.entity.SupplierContact;
import com.magic.crm.entity.SupplierProduct;
import com.magic.crm.entity.SupplierEvaluation;
import com.magic.crm.service.SupplierService;
import com.magic.crm.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * 供应商管理控制器
 */
@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "供应商管理")
public class SupplierController {
    
    @Autowired
    private SupplierService supplierService;
    
    @PostMapping
    @Operation(summary = "创建供应商")
    @PreAuthorize("hasAuthority('supplier:create')")
    public ApiResponse<Supplier> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        Supplier supplier = supplierService.createSupplier(supplierDTO);
        return ApiResponse.success(supplier, "供应商创建成功");
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新供应商")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ApiResponse<Supplier> updateSupplier(
            @PathVariable UUID id,
            @Valid @RequestBody SupplierDTO supplierDTO) {
        Supplier supplier = supplierService.updateSupplier(id, supplierDTO);
        return ApiResponse.success(supplier, "供应商更新成功");
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取供应商详情")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<Supplier> getSupplier(@PathVariable UUID id) {
        Supplier supplier = supplierService.getSupplierById(id);
        return ApiResponse.success(supplier);
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "根据编码获取供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<Supplier> getSupplierByCode(@PathVariable String code) {
        Supplier supplier = supplierService.getSupplierByCode(code);
        return ApiResponse.success(supplier);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除供应商")
    @PreAuthorize("hasAuthority('supplier:delete')")
    public ApiResponse<Void> deleteSupplier(@PathVariable UUID id) {
        supplierService.deleteSupplier(id);
        return ApiResponse.success(null, "供应商删除成功");
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除供应商")
    @PreAuthorize("hasAuthority('supplier:delete')")
    public ApiResponse<Void> deleteSuppliers(@RequestBody List<UUID> ids) {
        supplierService.deleteSuppliers(ids);
        return ApiResponse.success(null, "供应商批量删除成功");
    }
    
    @PostMapping("/search")
    @Operation(summary = "搜索供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<Page<Supplier>> searchSuppliers(
            @RequestBody SupplierSearchDTO searchDTO,
            Pageable pageable) {
        Page<Supplier> suppliers = supplierService.searchSuppliers(searchDTO, pageable);
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping
    @Operation(summary = "获取所有供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<Page<Supplier>> getAllSuppliers(Pageable pageable) {
        Page<Supplier> suppliers = supplierService.getAllSuppliers(pageable);
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/industry/{industry}")
    @Operation(summary = "根据行业获取供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> getSuppliersByIndustry(@PathVariable String industry) {
        List<Supplier> suppliers = supplierService.getSuppliersByIndustry(industry);
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/region/{region}")
    @Operation(summary = "根据地区获取供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> getSuppliersByRegion(@PathVariable String region) {
        List<Supplier> suppliers = supplierService.getSuppliersByRegion(region);
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/level/{level}")
    @Operation(summary = "根据等级获取供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> getSuppliersByLevel(@PathVariable String level) {
        List<Supplier> suppliers = supplierService.getSuppliersByLevel(level);
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "根据类型获取供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> getSuppliersByType(@PathVariable String type) {
        List<Supplier> suppliers = supplierService.getSuppliersByType(type);
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态获取供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> getSuppliersByStatus(@PathVariable String status) {
        List<Supplier> suppliers = supplierService.getSuppliersByStatus(status);
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/blacklist")
    @Operation(summary = "获取黑名单供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> getBlacklistSuppliers() {
        List<Supplier> suppliers = supplierService.getBlacklistSuppliers();
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/key-suppliers")
    @Operation(summary = "获取重点供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> getKeySuppliers() {
        List<Supplier> suppliers = supplierService.getKeySuppliers();
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/expiring-contracts")
    @Operation(summary = "获取即将到期的合同")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> getExpiringContracts(
            @RequestParam(defaultValue = "30") int days) {
        List<Supplier> suppliers = supplierService.getExpiringContracts(days);
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/expired-contracts")
    @Operation(summary = "获取已过期的合同")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> getExpiredContracts() {
        List<Supplier> suppliers = supplierService.getExpiredContracts();
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/high-rated")
    @Operation(summary = "获取高评级供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> getHighRatedSuppliers(
            @RequestParam(defaultValue = "4") Integer minRating) {
        List<Supplier> suppliers = supplierService.getHighRatedSuppliers(minRating);
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/need-evaluation")
    @Operation(summary = "获取需要评价的供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> getSuppliersNeedingEvaluation() {
        List<Supplier> suppliers = supplierService.getSuppliersNeedingEvaluation();
        return ApiResponse.success(suppliers);
    }
    
    @PostMapping("/{id}/contacts")
    @Operation(summary = "添加联系人")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ApiResponse<SupplierContact> addContact(
            @PathVariable UUID id,
            @RequestBody SupplierContact contact) {
        SupplierContact result = supplierService.addContact(id, contact);
        return ApiResponse.success(result, "联系人添加成功");
    }
    
    @PutMapping("/contacts/{contactId}")
    @Operation(summary = "更新联系人")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ApiResponse<SupplierContact> updateContact(
            @PathVariable UUID contactId,
            @RequestBody SupplierContact contact) {
        SupplierContact result = supplierService.updateContact(contactId, contact);
        return ApiResponse.success(result, "联系人更新成功");
    }
    
    @DeleteMapping("/contacts/{contactId}")
    @Operation(summary = "删除联系人")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ApiResponse<Void> deleteContact(@PathVariable UUID contactId) {
        supplierService.deleteContact(contactId);
        return ApiResponse.success(null, "联系人删除成功");
    }
    
    @GetMapping("/{id}/contacts")
    @Operation(summary = "获取供应商联系人")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<SupplierContact>> getSupplierContacts(@PathVariable UUID id) {
        List<SupplierContact> contacts = supplierService.getSupplierContacts(id);
        return ApiResponse.success(contacts);
    }
    
    @PostMapping("/{id}/products")
    @Operation(summary = "添加供应商产品")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ApiResponse<SupplierProduct> addSupplierProduct(
            @PathVariable UUID id,
            @RequestBody SupplierProduct supplierProduct) {
        SupplierProduct result = supplierService.addSupplierProduct(id, supplierProduct);
        return ApiResponse.success(result, "供应商产品添加成功");
    }
    
    @PutMapping("/products/{productId}")
    @Operation(summary = "更新供应商产品")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ApiResponse<SupplierProduct> updateSupplierProduct(
            @PathVariable UUID productId,
            @RequestBody SupplierProduct supplierProduct) {
        SupplierProduct result = supplierService.updateSupplierProduct(productId, supplierProduct);
        return ApiResponse.success(result, "供应商产品更新成功");
    }
    
    @DeleteMapping("/products/{productId}")
    @Operation(summary = "删除供应商产品")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ApiResponse<Void> deleteSupplierProduct(@PathVariable UUID productId) {
        supplierService.deleteSupplierProduct(productId);
        return ApiResponse.success(null, "供应商产品删除成功");
    }
    
    @GetMapping("/{id}/products")
    @Operation(summary = "获取供应商产品")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<SupplierProduct>> getSupplierProducts(@PathVariable UUID id) {
        List<SupplierProduct> products = supplierService.getSupplierProducts(id);
        return ApiResponse.success(products);
    }
    
    @PostMapping("/{id}/evaluations")
    @Operation(summary = "添加供应商评价")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ApiResponse<SupplierEvaluation> addEvaluation(
            @PathVariable UUID id,
            @RequestBody SupplierEvaluation evaluation) {
        SupplierEvaluation result = supplierService.addEvaluation(id, evaluation);
        return ApiResponse.success(result, "供应商评价添加成功");
    }
    
    @GetMapping("/{id}/evaluations")
    @Operation(summary = "获取供应商评价")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<SupplierEvaluation>> getSupplierEvaluations(@PathVariable UUID id) {
        List<SupplierEvaluation> evaluations = supplierService.getSupplierEvaluations(id);
        return ApiResponse.success(evaluations);
    }
    
    @PutMapping("/{id}/ratings")
    @Operation(summary = "更新供应商评级")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ApiResponse<Void> updateSupplierRatings(
            @PathVariable UUID id,
            @RequestParam Integer qualityRating,
            @RequestParam Integer serviceRating,
            @RequestParam Integer priceRating,
            @RequestParam Integer overallRating) {
        supplierService.updateSupplierRatings(id, qualityRating, serviceRating, priceRating, overallRating);
        return ApiResponse.success(null, "供应商评级更新成功");
    }
    
    @PutMapping("/{id}/blacklist")
    @Operation(summary = "设置黑名单")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ApiResponse<Void> setBlacklist(
            @PathVariable UUID id,
            @RequestParam Boolean isBlacklist) {
        supplierService.setBlacklist(id, isBlacklist);
        return ApiResponse.success(null, "黑名单设置成功");
    }
    
    @PutMapping("/{id}/key-supplier")
    @Operation(summary = "设置重点供应商")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ApiResponse<Void> setKeySupplier(
            @PathVariable UUID id,
            @RequestParam Boolean isKeySupplier) {
        supplierService.setKeySupplier(id, isKeySupplier);
        return ApiResponse.success(null, "重点供应商设置成功");
    }
    
    @GetMapping("/generate-code")
    @Operation(summary = "生成供应商编码")
    @PreAuthorize("hasAuthority('supplier:create')")
    public ApiResponse<String> generateSupplierCode() {
        String code = supplierService.generateSupplierCode();
        return ApiResponse.success(code);
    }
    
    @PostMapping("/import")
    @Operation(summary = "导入供应商")
    @PreAuthorize("hasAuthority('supplier:create')")
    public ApiResponse<List<Supplier>> importSuppliers(@RequestBody List<SupplierDTO> supplierDTOs) {
        List<Supplier> suppliers = supplierService.importSuppliers(supplierDTOs);
        return ApiResponse.success(suppliers, "供应商导入成功");
    }
    
    @PostMapping("/export")
    @Operation(summary = "导出供应商")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> exportSuppliers(@RequestBody SupplierSearchDTO searchDTO) {
        List<Supplier> suppliers = supplierService.exportSuppliers(searchDTO);
        return ApiResponse.success(suppliers);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取供应商统计信息")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<SupplierService.SupplierStatistics> getSupplierStatistics() {
        SupplierService.SupplierStatistics statistics = supplierService.getSupplierStatistics();
        return ApiResponse.success(statistics);
    }
    
    @PostMapping("/check-duplicates")
    @Operation(summary = "检查供应商重复")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<Supplier>> checkDuplicates(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String uscc,
            @RequestParam(required = false) String code) {
        List<Supplier> duplicates = supplierService.checkDuplicates(name, uscc, code);
        return ApiResponse.success(duplicates);
    }
}
