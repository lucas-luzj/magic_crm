package com.magic.crm.controller;

import com.magic.crm.dto.LeadDTO;
import com.magic.crm.dto.LeadSearchDTO;
import com.magic.crm.entity.Lead;
import com.magic.crm.entity.LeadFollowUp;
import com.magic.crm.service.LeadService;
import com.magic.crm.common.ApiResponse;
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
 * 线索管理控制器
 */
@RestController
@RequestMapping("/api/leads")
@Tag(name = "线索管理")
public class LeadController {
    
    @Autowired
    private LeadService leadService;
    
    @PostMapping
    @Operation(summary("创建线索")
    @PreAuthorize("hasAuthority('lead:create')")
    public ApiResponse<Lead> createLead(@Valid @RequestBody LeadDTO leadDTO) {
        Lead lead = leadService.createLead(leadDTO);
        return ApiResponse.success(lead, "线索创建成功");
    }
    
    @PutMapping("/{id}")
    @Operation(summary("更新线索")
    @PreAuthorize("hasAuthority('lead:update')")
    public ApiResponse<Lead> updateLead(
            @PathVariable UUID id,
            @Valid @RequestBody LeadDTO leadDTO) {
        Lead lead = leadService.updateLead(id, leadDTO);
        return ApiResponse.success(lead, "线索更新成功");
    }
    
    @GetMapping("/{id}")
    @Operation(summary("获取线索详情")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<Lead> getLead(@PathVariable UUID id) {
        Lead lead = leadService.getLeadById(id);
        return ApiResponse.success(lead);
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary("根据编号获取线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<Lead> getLeadByCode(@PathVariable String code) {
        Lead lead = leadService.getLeadByCode(code);
        return ApiResponse.success(lead);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary("删除线索")
    @PreAuthorize("hasAuthority('lead:delete')")
    public ApiResponse<Void> deleteLead(@PathVariable UUID id) {
        leadService.deleteLead(id);
        return ApiResponse.success(null, "线索删除成功");
    }
    
    @DeleteMapping("/batch")
    @Operation(summary("批量删除线索")
    @PreAuthorize("hasAuthority('lead:delete')")
    public ApiResponse<Void> deleteLeads(@RequestBody List<UUID> ids) {
        leadService.deleteLeads(ids);
        return ApiResponse.success(null, "线索批量删除成功");
    }
    
    @PostMapping("/search")
    @Operation(summary("搜索线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<Page<Lead>> searchLeads(
            @RequestBody LeadSearchDTO searchDTO,
            Pageable pageable) {
        Page<Lead> leads = leadService.searchLeads(searchDTO, pageable);
        return ApiResponse.success(leads);
    }
    
    @GetMapping
    @Operation(summary("获取所有线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<Page<Lead>> getAllLeads(Pageable pageable) {
        Page<Lead> leads = leadService.getAllLeads(pageable);
        return ApiResponse.success(leads);
    }
    
    @GetMapping("/owner/{ownerId}")
    @Operation(summary("根据所属销售获取线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<Page<Lead>> getLeadsByOwner(
            @PathVariable UUID ownerId,
            Pageable pageable) {
        Page<Lead> leads = leadService.getLeadsByOwner(ownerId, pageable);
        return ApiResponse.success(leads);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary("根据状态获取线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<List<Lead>> getLeadsByStatus(@PathVariable String status) {
        List<Lead> leads = leadService.getLeadsByStatus(status);
        return ApiResponse.success(leads);
    }
    
    @GetMapping("/priority/{priority}")
    @Operation(summary("根据优先级获取线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<List<Lead>> getLeadsByPriority(@PathVariable String priority) {
        List<Lead> leads = leadService.getLeadsByPriority(priority);
        return ApiResponse.success(leads);
    }
    
    @GetMapping("/source/{source}")
    @Operation(summary("根据来源获取线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<List<Lead>> getLeadsBySource(@PathVariable String source) {
        List<Lead> leads = leadService.getLeadsBySource(source);
        return ApiResponse.success(leads);
    }
    
    @GetMapping("/to-follow")
    @Operation(summary("获取需要跟进的线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<List<Lead>> getLeadsToFollow() {
        List<Lead> leads = leadService.getLeadsToFollow();
        return ApiResponse.success(leads);
    }
    
    @GetMapping("/overdue")
    @Operation(summary("获取超期未跟进的线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<List<Lead>> getOverdueLeads() {
        List<Lead> leads = leadService.getOverdueLeads();
        return ApiResponse.success(leads);
    }
    
    @GetMapping("/high-score")
    @Operation(summary("获取高评分线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<List<Lead>> getHighScoreLeads(
            @RequestParam(defaultValue = "80") Integer minScore) {
        List<Lead> leads = leadService.getHighScoreLeads(minScore);
        return ApiResponse.success(leads);
    }
    
    @GetMapping("/closing-soon")
    @Operation(summary("获取即将到期的线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<List<Lead>> getLeadsClosingSoon(
            @RequestParam(defaultValue = "7") int days) {
        List<Lead> leads = leadService.getLeadsClosingSoon(days);
        return ApiResponse.success(leads);
    }
    
    @PutMapping("/{id}/assign")
    @Operation(summary("分配线索")
    @PreAuthorize("hasAuthority('lead:assign')")
    public ApiResponse<Void> assignLead(
            @PathVariable UUID id,
            @RequestParam UUID ownerId) {
        leadService.assignLead(id, ownerId);
        return ApiResponse.success(null, "线索分配成功");
    }
    
    @PutMapping("/batch/assign")
    @Operation(summary("批量分配线索")
    @PreAuthorize("hasAuthority('lead:assign')")
    public ApiResponse<Void> batchAssignLeads(
            @RequestBody List<UUID> leadIds,
            @RequestParam UUID ownerId) {
        leadService.batchAssignLeads(leadIds, ownerId);
        return ApiResponse.success(null, "线索批量分配成功");
    }
    
    @PutMapping("/{id}/convert")
    @Operation(summary("转换线索为客户")
    @PreAuthorize("hasAuthority('lead:convert')")
    public ApiResponse<Lead> convertToCustomer(
            @PathVariable UUID id,
            @RequestParam UUID customerId) {
        Lead lead = leadService.convertToCustomer(id, customerId);
        return ApiResponse.success(lead, "线索转换成功");
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary("更新线索状态")
    @PreAuthorize("hasAuthority('lead:update')")
    public ApiResponse<Void> updateLeadStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        leadService.updateLeadStatus(id, status);
        return ApiResponse.success(null, "线索状态更新成功");
    }
    
    @PutMapping("/{id}/score")
    @Operation(summary("更新线索评分")
    @PreAuthorize("hasAuthority('lead:update')")
    public ApiResponse<Void> updateLeadScore(
            @PathVariable UUID id,
            @RequestParam Integer score) {
        leadService.updateLeadScore(id, score);
        return ApiResponse.success(null, "线索评分更新成功");
    }
    
    @PutMapping("/{id}/contact")
    @Operation(summary("更新最后联系时间")
    @PreAuthorize("hasAuthority('lead:update')")
    public ApiResponse<Void> updateLastContactTime(@PathVariable UUID id) {
        leadService.updateLastContactTime(id);
        return ApiResponse.success(null, "最后联系时间更新成功");
    }
    
    @PostMapping("/{id}/follow-up")
    @Operation(summary("添加跟进记录")
    @PreAuthorize("hasAuthority('lead:update')")
    public ApiResponse<LeadFollowUp> addFollowUp(
            @PathVariable UUID id,
            @RequestBody LeadFollowUp followUp) {
        LeadFollowUp result = leadService.addFollowUp(id, followUp);
        return ApiResponse.success(result, "跟进记录添加成功");
    }
    
    @GetMapping("/{id}/follow-ups")
    @Operation(summary("获取线索跟进记录")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<List<LeadFollowUp>> getLeadFollowUps(@PathVariable UUID id) {
        List<LeadFollowUp> followUps = leadService.getLeadFollowUps(id);
        return ApiResponse.success(followUps);
    }
    
    @GetMapping("/generate-code")
    @Operation(summary("生成线索编号")
    @PreAuthorize("hasAuthority('lead:create')")
    public ApiResponse<String> generateLeadCode() {
        String code = leadService.generateLeadCode();
        return ApiResponse.success(code);
    }
    
    @PostMapping("/import")
    @Operation(summary("导入线索")
    @PreAuthorize("hasAuthority('lead:create')")
    public ApiResponse<List<Lead>> importLeads(@RequestBody List<LeadDTO> leadDTOs) {
        List<Lead> leads = leadService.importLeads(leadDTOs);
        return ApiResponse.success(leads, "线索导入成功");
    }
    
    @PostMapping("/export")
    @Operation(summary("导出线索")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<List<Lead>> exportLeads(@RequestBody LeadSearchDTO searchDTO) {
        List<Lead> leads = leadService.exportLeads(searchDTO);
        return ApiResponse.success(leads);
    }
    
    @GetMapping("/statistics")
    @Operation(summary("获取线索统计信息")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<LeadService.LeadStatistics> getLeadStatistics(
            @RequestParam(required = false) UUID ownerId) {
        LeadService.LeadStatistics statistics = leadService.getLeadStatistics(ownerId);
        return ApiResponse.success(statistics);
    }
    
    @PostMapping("/check-duplicates")
    @Operation(summary("检查线索重复")
    @PreAuthorize("hasAuthority('lead:view')")
    public ApiResponse<List<Lead>> checkDuplicates(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) String contactEmail) {
        List<Lead> duplicates = leadService.checkDuplicates(name, companyName, contactPhone, contactEmail);
        return ApiResponse.success(duplicates);
    }
}
