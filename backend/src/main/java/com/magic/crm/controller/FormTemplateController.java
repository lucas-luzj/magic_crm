package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.dto.FormTemplateSummaryDto;
import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.FormTemplate;
import com.magic.crm.service.FormTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/form-templates")
public class FormTemplateController {

    @Autowired
    private FormTemplateService formTemplateService;

    /**
     * 分页查询表单模板（返回摘要数据）
     */
    @GetMapping
    public ApiResponse<PageResponse<FormTemplateSummaryDto>> getFormTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String formName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        FormTemplate.FormStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = FormTemplate.FormStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 忽略无效的状态值
            }
        }

        Page<FormTemplateSummaryDto> result = formTemplateService.findFormTemplatesWithFilters(
                formName, category, statusEnum, pageable);

        PageResponse<FormTemplateSummaryDto> pageResponse = new PageResponse<>(result);

        return ApiResponse.success(pageResponse);
    }

    /**
     * 根据ID获取表单模板（返回完整数据，包含关联字段）
     */
    @GetMapping("/{id}")
    public ApiResponse<FormTemplate> getFormTemplateById(@PathVariable Long id) {
        Optional<FormTemplate> formTemplate = formTemplateService.findByIdWithFields(id);
        return formTemplate.map(ApiResponse::success)
                .orElse(ApiResponse.error("表单模板不存在"));
    }

    /**
     * 根据表单键获取表单模板
     */
    @GetMapping("/by-key/{formKey}")
    public ApiResponse<FormTemplate> getFormTemplateByKey(@PathVariable String formKey) {
        Optional<FormTemplate> formTemplate = formTemplateService.findByFormKey(formKey);
        return formTemplate.map(ApiResponse::success)
                .orElse(ApiResponse.error("表单模板不存在"));
    }

    /**
     * 创建表单模板
     */
    @PostMapping
    public ApiResponse<FormTemplateSummaryDto> createFormTemplate(@RequestBody FormTemplate formTemplate) {
        try {
            FormTemplate savedTemplate = formTemplateService.createFormTemplate(formTemplate);
            FormTemplateSummaryDto summaryDto = new FormTemplateSummaryDto(savedTemplate);
            return ApiResponse.success("创建表单模板成功", summaryDto);
        } catch (Exception e) {
            return ApiResponse.error("创建表单模板失败：" + e.getMessage());
        }
    }

    /**
     * 更新表单模板
     */
    @PutMapping("/{id}")
    public ApiResponse<FormTemplateSummaryDto> updateFormTemplate(
            @PathVariable Long id,
            @RequestBody FormTemplate formTemplate) {

        try {
            FormTemplate updatedTemplate = formTemplateService.updateFormTemplate(id, formTemplate);
            FormTemplateSummaryDto summaryDto = new FormTemplateSummaryDto(updatedTemplate);
            return ApiResponse.success("更新表单模板成功", summaryDto);
        } catch (Exception e) {
            return ApiResponse.error("更新表单模板失败：" + e.getMessage());
        }
    }

    /**
     * 删除表单模板
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFormTemplate(@PathVariable Long id) {
        try {
            formTemplateService.deleteFormTemplate(id);
            return ApiResponse.success("删除表单模板成功");
        } catch (Exception e) {
            return ApiResponse.error("删除表单模板失败：" + e.getMessage());
        }
    }

    /**
     * 复制表单模板
     */
    @PostMapping("/{id}/copy")
    public ApiResponse<FormTemplate> copyFormTemplate(
            @PathVariable Long id,
            @RequestParam String newFormKey,
            @RequestParam String newFormName) {

        try {
            FormTemplate copiedTemplate = formTemplateService.copyFormTemplate(id, newFormKey, newFormName);
            return ApiResponse.success("复制表单模板成功", copiedTemplate);
        } catch (RuntimeException e) {
            return ApiResponse.error("复制表单模板失败：" + e.getMessage());
        }
    }

    /**
     * 激活表单模板
     */
    @PutMapping("/{id}/activate")
    public ApiResponse<FormTemplateSummaryDto> activateFormTemplate(@PathVariable Long id) {
        try {
            FormTemplate activatedTemplate = formTemplateService.activateFormTemplate(id);
            FormTemplateSummaryDto summaryDto = new FormTemplateSummaryDto(activatedTemplate);
            return ApiResponse.success("激活表单模板成功", summaryDto);
        } catch (RuntimeException e) {
            return ApiResponse.error("激活表单模板失败：" + e.getMessage());
        }
    }

    /**
     * 停用表单模板
     */
    @PutMapping("/{id}/deactivate")
    public ApiResponse<FormTemplate> deactivateFormTemplate(@PathVariable Long id) {
        try {
            FormTemplate deactivatedTemplate = formTemplateService.deactivateFormTemplate(id);
            return ApiResponse.success("停用表单模板成功", deactivatedTemplate);
        } catch (RuntimeException e) {
            return ApiResponse.error("停用表单模板失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public ApiResponse<List<String>> getCategories() {
        try {
            List<String> categories = formTemplateService.findAllCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.error("获取分类失败：" + e.getMessage());
        }
    }

    /**
     * 根据标签查找表单模板
     */
    @GetMapping("/by-tag/{tag}")
    public ApiResponse<List<FormTemplate>> getFormTemplatesByTag(@PathVariable String tag) {
        try {
            List<FormTemplate> templates = formTemplateService.findByTag(tag);
            return ApiResponse.success(templates);
        } catch (Exception e) {
            return ApiResponse.error("查询表单模板失败：" + e.getMessage());
        }
    }
}