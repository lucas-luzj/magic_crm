package com.magic.crm.service;

import com.magic.crm.entity.ProcessFormMapping;
import com.magic.crm.repository.ProcessFormMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 流程表单映射服务层
 */
@Service
@Transactional
public class ProcessFormMappingService {

    @Autowired
    private ProcessFormMappingRepository processFormMappingRepository;

    /**
     * 创建流程表单映射
     */
    public ProcessFormMapping createProcessFormMapping(ProcessFormMapping mapping) {
        // 如果设置为默认表单，需要先取消其他默认表单
        if (mapping.getIsDefault()) {
            setAsDefaultMapping(mapping);
        }

        return processFormMappingRepository.save(mapping);
    }

    /**
     * 更新流程表单映射
     */
    public ProcessFormMapping updateProcessFormMapping(Long id, ProcessFormMapping mapping) {
        ProcessFormMapping existingMapping = processFormMappingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("流程表单映射不存在: " + id));

        existingMapping.setProcessDefinitionKey(mapping.getProcessDefinitionKey());
        existingMapping.setTaskDefinitionKey(mapping.getTaskDefinitionKey());
        existingMapping.setFormTemplateId(mapping.getFormTemplateId());
        existingMapping.setMappingType(mapping.getMappingType());
        existingMapping.setFormVersion(mapping.getFormVersion());
        existingMapping.setFormPosition(mapping.getFormPosition());

        // 如果设置为默认表单，需要先取消其他默认表单
        if (mapping.getIsDefault()) {
            setAsDefaultMapping(existingMapping);
        } else {
            existingMapping.setIsDefault(false);
        }

        return processFormMappingRepository.save(existingMapping);
    }

    /**
     * 删除流程表单映射
     */
    public void deleteProcessFormMapping(Long id) {
        if (!processFormMappingRepository.existsById(id)) {
            throw new RuntimeException("流程表单映射不存在: " + id);
        }
        processFormMappingRepository.deleteById(id);
    }

    /**
     * 根据ID查找流程表单映射
     */
    public Optional<ProcessFormMapping> findById(Long id) {
        return processFormMappingRepository.findById(id);
    }

    /**
     * 根据流程定义Key查找映射
     */
    public List<ProcessFormMapping> findByProcessDefinitionKey(String processDefinitionKey) {
        return processFormMappingRepository.findByProcessDefinitionKey(processDefinitionKey);
    }

    /**
     * 根据任务定义Key查找映射
     */
    public List<ProcessFormMapping> findByTaskDefinitionKey(String taskDefinitionKey) {
        return processFormMappingRepository.findByTaskDefinitionKey(taskDefinitionKey);
    }

    /**
     * 根据表单模板ID查找映射
     */
    public List<ProcessFormMapping> findByFormTemplateId(Long formTemplateId) {
        return processFormMappingRepository.findByFormTemplateId(formTemplateId);
    }

    /**
     * 查找默认表单映射
     */
    public Optional<ProcessFormMapping> findDefaultFormMapping(String processDefinitionKey, String taskDefinitionKey) {
        return processFormMappingRepository.findDefaultFormMapping(processDefinitionKey, taskDefinitionKey);
    }

    /**
     * 根据流程定义Key和任务定义Key查找映射
     */
    public List<ProcessFormMapping> findByProcessDefinitionKeyAndTaskDefinitionKey(String processDefinitionKey,
            String taskDefinitionKey) {
        return processFormMappingRepository.findByProcessDefinitionKeyAndTaskDefinitionKey(processDefinitionKey,
                taskDefinitionKey);
    }

    /**
     * 根据流程定义Key和映射类型查找映射
     */
    public List<ProcessFormMapping> findByProcessDefinitionKeyAndMappingType(String processDefinitionKey,
            ProcessFormMapping.MappingType mappingType) {
        return processFormMappingRepository.findByProcessDefinitionKeyAndMappingType(processDefinitionKey, mappingType);
    }

    /**
     * 设置默认表单映射
     */
    private void setAsDefaultMapping(ProcessFormMapping mapping) {
        // 先取消同流程同任务的默认映射
        List<ProcessFormMapping> existingMappings = processFormMappingRepository
                .findByProcessDefinitionKeyAndTaskDefinitionKey(mapping.getProcessDefinitionKey(),
                        mapping.getTaskDefinitionKey());

        for (ProcessFormMapping existingMapping : existingMappings) {
            if (existingMapping.getIsDefault()) {
                existingMapping.setIsDefault(false);
                processFormMappingRepository.save(existingMapping);
            }
        }

        mapping.setIsDefault(true);
    }

    /**
     * 批量创建流程表单映射
     */
    public List<ProcessFormMapping> createBatchMappings(List<ProcessFormMapping> mappings) {
        for (ProcessFormMapping mapping : mappings) {
            if (mapping.getIsDefault()) {
                setAsDefaultMapping(mapping);
            }
        }
        return processFormMappingRepository.saveAll(mappings);
    }

    /**
     * 根据流程定义Key删除所有映射
     */
    public void deleteByProcessDefinitionKey(String processDefinitionKey) {
        List<ProcessFormMapping> mappings = processFormMappingRepository
                .findByProcessDefinitionKey(processDefinitionKey);
        processFormMappingRepository.deleteAll(mappings);
    }

    /**
     * 根据表单模板ID删除所有映射
     */
    public void deleteByFormTemplateId(Long formTemplateId) {
        List<ProcessFormMapping> mappings = processFormMappingRepository.findByFormTemplateId(formTemplateId);
        processFormMappingRepository.deleteAll(mappings);
    }
}
