package com.magic.crm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.magic.crm.dto.FormTemplateSummaryDto;
import com.magic.crm.entity.Dictionary;
import com.magic.crm.entity.FormTemplate;
import com.magic.crm.repository.FormTemplateRepository;
import com.magic.crm.service.DictionaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表单模板服务层
 */
@Service
@Transactional
public class FormTemplateService {

    @Autowired
    private FormTemplateRepository formTemplateRepository;

    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 创建表单模板
     */
    public FormTemplate createFormTemplate(FormTemplate formTemplate) {
        // 检查表单键是否已存在
        if (formTemplateRepository.existsByFormKey(formTemplate.getFormKey())) {
            throw new RuntimeException("表单键已存在: " + formTemplate.getFormKey());
        }

        // 验证表单配置
        if (!validateFormConfig(formTemplate.getFormConfig())) {
            throw new RuntimeException("表单配置格式不正确");
        }

        return formTemplateRepository.save(formTemplate);
    }

    /**
     * 更新表单模板
     */
    public FormTemplate updateFormTemplate(Long id, FormTemplate formTemplate) {
        FormTemplate existingTemplate = formTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单模板不存在: " + id));

        // 检查表单键是否被其他模板使用
        if (!existingTemplate.getFormKey().equals(formTemplate.getFormKey()) &&
                formTemplateRepository.existsByFormKey(formTemplate.getFormKey())) {
            throw new RuntimeException("表单键已存在: " + formTemplate.getFormKey());
        }

        // 验证表单配置
        if (!validateFormConfig(formTemplate.getFormConfig())) {
            throw new RuntimeException("表单配置格式不正确");
        }

        existingTemplate.setFormKey(formTemplate.getFormKey());
        existingTemplate.setFormName(formTemplate.getFormName());
        existingTemplate.setFormDescription(formTemplate.getFormDescription());
        existingTemplate.setFormConfig(formTemplate.getFormConfig());
        existingTemplate.setCategory(formTemplate.getCategory());
        existingTemplate.setStatus(formTemplate.getStatus());
        existingTemplate.setTags(formTemplate.getTags());
        existingTemplate.setUpdatedBy(formTemplate.getUpdatedBy());

        return formTemplateRepository.save(existingTemplate);
    }

    /**
     * 删除表单模板
     */
    public void deleteFormTemplate(Long id) {
        if (!formTemplateRepository.existsById(id)) {
            throw new RuntimeException("表单模板不存在: " + id);
        }
        formTemplateRepository.deleteById(id);
    }

    /**
     * 根据ID查找表单模板
     */
    public Optional<FormTemplate> findById(Long id) {
        return formTemplateRepository.findById(id);
    }

    /**
     * 根据表单键查找表单模板
     */
    public Optional<FormTemplate> findByFormKey(String formKey) {
        return formTemplateRepository.findByFormKey(formKey);
    }

    /**
     * 获取所有表单模板
     */
    public List<FormTemplate> findAll() {
        return formTemplateRepository.findAll();
    }

    /**
     * 根据状态查找表单模板
     */
    public List<FormTemplate> findByStatus(FormTemplate.FormStatus status) {
        return formTemplateRepository.findByStatus(status);
    }

    /**
     * 根据分类查找表单模板
     */
    public List<FormTemplate> findByCategory(String category) {
        return formTemplateRepository.findByCategory(category);
    }

    /**
     * 分页查询表单模板（返回摘要DTO）
     */
    public Page<FormTemplateSummaryDto> findFormTemplatesWithFilters(String formName, String category,
            FormTemplate.FormStatus status, Pageable pageable) {
        Page<FormTemplate> formTemplates = formTemplateRepository.findFormTemplatesWithFilters(formName, category,
                status, pageable);

        List<FormTemplateSummaryDto> summaryDtos = formTemplates.getContent().stream()
                .map(FormTemplateSummaryDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(summaryDtos, pageable, formTemplates.getTotalElements());
    }

    /**
     * 根据ID获取完整的表单模板（包含关联数据）
     */
    public Optional<FormTemplate> findByIdWithFields(Long id) {
        return formTemplateRepository.findByIdWithFields(id);
    }

    /**
     * 获取所有分类（从字典表获取）
     */
    public List<String> findAllCategories() {
        List<Dictionary> categories = dictionaryService.findByDictType("form_category");
        return categories.stream()
                .map(Dictionary::getDictValue)
                .collect(Collectors.toList());
    }

    /**
     * 复制表单模板
     */
    public FormTemplate copyFormTemplate(Long id, String newFormKey, String newFormName) {
        FormTemplate originalTemplate = formTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单模板不存在: " + id));

        if (formTemplateRepository.existsByFormKey(newFormKey)) {
            throw new RuntimeException("表单键已存在: " + newFormKey);
        }

        FormTemplate newTemplate = new FormTemplate();
        newTemplate.setFormKey(newFormKey);
        newTemplate.setFormName(newFormName);
        newTemplate.setFormDescription(originalTemplate.getFormDescription());
        newTemplate.setFormConfig(originalTemplate.getFormConfig());
        newTemplate.setCategory(originalTemplate.getCategory());
        newTemplate.setTags(originalTemplate.getTags());
        newTemplate.setStatus(FormTemplate.FormStatus.DRAFT);
        newTemplate.setCreatedBy(originalTemplate.getCreatedBy());

        return formTemplateRepository.save(newTemplate);
    }

    /**
     * 激活表单模板
     */
    public FormTemplate activateFormTemplate(Long id) {
        FormTemplate template = formTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单模板不存在: " + id));

        template.setStatus(FormTemplate.FormStatus.ACTIVE);
        return formTemplateRepository.save(template);
    }

    /**
     * 停用表单模板
     */
    public FormTemplate deactivateFormTemplate(Long id) {
        FormTemplate template = formTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单模板不存在: " + id));

        template.setStatus(FormTemplate.FormStatus.INACTIVE);
        return formTemplateRepository.save(template);
    }

    /**
     * 验证表单配置
     */
    public boolean validateFormConfig(JsonNode formConfig) {
        try {
            // 检查是否有fields字段
            if (!formConfig.has("components")) {
                return false;
            }

            JsonNode fields = formConfig.get("components");
            if (!fields.isArray() || fields.size() == 0) {
                return false;
            }

            // 验证每个字段的必要属性
            for (JsonNode field : fields) {
                if (!field.has("id") || !field.has("field") || !field.has("label") || !field.has("cmpType")) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据标签查找表单模板
     */
    public List<FormTemplate> findByTag(String tag) {
        return formTemplateRepository.findByTag(tag);
    }
}